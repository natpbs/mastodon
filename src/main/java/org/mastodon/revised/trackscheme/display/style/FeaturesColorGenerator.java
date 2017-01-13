package org.mastodon.revised.trackscheme.display.style;

import java.awt.Color;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle.UpdateListener;
import org.mastodon.revised.ui.ColorMode.EdgeColorMode;
import org.mastodon.revised.ui.ColorMode.VertexColorMode;
import org.mastodon.revised.ui.EdgeColorGenerator;
import org.mastodon.revised.ui.VertexColorGenerator;
import org.mastodon.revised.ui.util.ColorMap;

/**
 * Color generator for vertices and edges from a feature model, following hints
 * from a {@link TrackSchemeStyle}.
 * <p>
 * This color generator can deal with vertex and edge features and attribute
 * them to vertices and edges. Interestingly, it can color a vertex using an
 * edge feature, and vice-versa, abiding to the different modes of
 * {@link VertexColorMode} and {@link EdgeColorMode}. This color generator does
 * not deal with the {@link VertexColorMode#BRANCH_VERTEX},
 * {@link VertexColorMode#BRANCH_EDGE}, {@link EdgeColorMode#BRANCH_VERTEX} and
 * {@link EdgeColorMode#BRANCH_EDGE} cases.
 *
 * @param <V>
 *            the type of vertices to color.
 * @param <E>
 *            the type of edges to color.
 * @author Jean-Yves Tinevez
 */
public class FeaturesColorGenerator< V extends Vertex< E >, E extends Edge< V > >
		implements UpdateListener, VertexColorGenerator< V >, EdgeColorGenerator< E >
{

	protected final ReadOnlyGraph< V, E > graph;

	protected TrackSchemeStyle style = TrackSchemeStyle.defaultStyle();

	protected VertexColorGenerator< V > vertexColorGenerator;

	protected EdgeColorGenerator< E > edgeColorGenerator;

	private final FeatureModel< V, E > features;

	public FeaturesColorGenerator(
			final ReadOnlyGraph< V, E > graph,
			final FeatureModel< V, E > features )
	{
		this.graph = graph;
		this.features = features;
		trackSchemeStyleChanged();
	}

	public void setStyle( final TrackSchemeStyle s )
	{
		if ( null != style )
			style.removeUpdateListener( this );

		this.style = s;
		s.addUpdateListener( this );
		trackSchemeStyleChanged();
	}

	@Override
	public void trackSchemeStyleChanged()
	{
		switch ( style.getVertexColorMode() )
		{
		case FIXED:
		default:
			vertexColorGenerator = new FixedVertexColorGenerator( style.getVertexFillColor() );
			break;
		case INCOMING_EDGE:
		{
			final FeatureProjection< E > vfp = features.getEdgeProjection( style.getVertexFeatureKey() );
			vertexColorGenerator = new IncomingEdgeVertexColorGenerator( vfp, style.getVertexColorMap(), style.getMinVertexColorRange(), style.getMaxVertexColorRange() );
			break;
		}
		case OUTGOING_EDGE:
		{
			final FeatureProjection< E > vfp = features.getEdgeProjection( style.getVertexFeatureKey() );
			vertexColorGenerator = new OutgoingEdgeVertexColorGenerator( vfp, style.getVertexColorMap(), style.getMinVertexColorRange(), style.getMaxVertexColorRange() );
			break;
		}
		case VERTEX:
		{
			final FeatureProjection< V > vfp = features.getVertexProjection( style.getVertexFeatureKey() );
			vertexColorGenerator = new ThisVertexColorGenerator( vfp, style.getVertexColorMap(), style.getMinVertexColorRange(), style.getMaxVertexColorRange() );
			break;
		}
		}

		switch ( style.getEdgeColorMode() )
		{
		case FIXED:
		default:
		{
			edgeColorGenerator = new FixedEdgeColorGenerator( style.getEdgeColor() );
			break;
		}
		case EDGE:
		{
			final FeatureProjection< E > efp = features.getEdgeProjection( style.getEdgeFeatureKey() );
			edgeColorGenerator = new ThisEdgeColorGenerator( efp, style.getEdgeColorMap(), style.getMinEdgeColorRange(), style.getMaxEdgeColorRange() );
			break;
		}
		case SOURCE_VERTEX:
		{
			final FeatureProjection< V > efp = features.getVertexProjection( style.getEdgeFeatureKey() );
			edgeColorGenerator = new SourceVertexEdgeColorGenerator( efp, style.getEdgeColorMap(), style.getMinEdgeColorRange(), style.getMaxEdgeColorRange() );
			break;
		}
		case TARGET_VERTEX:
		{
			final FeatureProjection< V > efp = features.getVertexProjection( style.getEdgeFeatureKey() );
			edgeColorGenerator = new TargetVertexEdgeColorGenerator( efp, style.getEdgeColorMap(), style.getMinEdgeColorRange(), style.getMaxEdgeColorRange() );
			break;
		}
		}
	}

	@Override
	public Color color( final E edge )
	{
		return edgeColorGenerator.color( edge );
	}

	@Override
	public Color color( final V vertex )
	{
		return vertexColorGenerator.color( vertex );
	}

	/*
	 * Colorer classes.
	 */

	private class FixedVertexColorGenerator implements VertexColorGenerator< V >
	{

		private final Color color;

		public FixedVertexColorGenerator( final Color color )
		{
			this.color = color;
		}

		@Override
		public Color color( final V vertex )
		{
			return color;
		}
	}

	protected class ThisVertexColorGenerator implements VertexColorGenerator< V >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< V > featureProjection;

		public ThisVertexColorGenerator( final FeatureProjection< V > featureProjection, final ColorMap colorMap, final double min, final double max )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@Override
		public Color color( final V vertex )
		{
			if ( !featureProjection.isSet( vertex ) )
				return colorMap.getMissingColor();

			final double value = featureProjection.value( vertex );
			return colorMap.get( ( value - min ) / ( max - min ) );
		}
	}

	private class IncomingEdgeVertexColorGenerator implements VertexColorGenerator< V >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< E > featureProjection;

		public IncomingEdgeVertexColorGenerator( final FeatureProjection< E > featureProjection, final ColorMap colorMap, final double min, final double max )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@Override
		public Color color( final V vertex )
		{
			if ( vertex.incomingEdges().size() != 1 )
				return colorMap.get( Double.NaN );

			final Color color;
			final E ref = graph.edgeRef();
			final E edge = vertex.incomingEdges().get( 0, ref );
			if ( !featureProjection.isSet( edge ) )
				color = colorMap.getMissingColor();
			else
			{
				final double value = featureProjection.value( edge );
				color = colorMap.get( ( value - min ) / ( max - min ) );
			}
			graph.releaseRef( ref );
			return color;
		}
	}

	private class OutgoingEdgeVertexColorGenerator implements VertexColorGenerator< V >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< E > featureProjection;

		public OutgoingEdgeVertexColorGenerator( final FeatureProjection< E > featureProjection, final ColorMap colorMap, final double min, final double max )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@Override
		public Color color( final V vertex )
		{
			if ( vertex.outgoingEdges().size() != 1 )
				return colorMap.get( Double.NaN );

			final Color color;
			final E ref = graph.edgeRef();
			final E edge = vertex.outgoingEdges().get( 0, ref );
			if ( !featureProjection.isSet( edge ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = featureProjection.value( edge );
				color = colorMap.get( ( value - min ) / ( max - min ) );
			}
			graph.releaseRef( ref );
			return color;
		}
	}

	private class FixedEdgeColorGenerator implements EdgeColorGenerator< E >
	{

		private final Color color;

		public FixedEdgeColorGenerator( final Color color )
		{
			this.color = color;
		}

		@Override
		public Color color( final E edge )
		{
			return color;
		}
	}

	protected class ThisEdgeColorGenerator implements EdgeColorGenerator< E >
	{
		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< E > featureProjection;

		public ThisEdgeColorGenerator( final FeatureProjection< E > featureProjection, final ColorMap colorMap, final double min, final double max )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@Override
		public Color color( final E edge )
		{
			if ( !featureProjection.isSet( edge ) )
				return colorMap.getMissingColor();

			final double value = featureProjection.value( edge );
			return colorMap.get( ( value - min ) / ( max - min ) );
		}
	}

	private class SourceVertexEdgeColorGenerator implements EdgeColorGenerator< E >
	{
		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< V > featureProjection;

		public SourceVertexEdgeColorGenerator( final FeatureProjection< V > featureProjection, final ColorMap colorMap, final double min, final double max )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@Override
		public Color color( final E edge )
		{
			Color color;
			final V ref = graph.vertexRef();
			final V vertex = edge.getSource( ref );
			if ( !featureProjection.isSet( vertex ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = featureProjection.value( vertex );
				color = colorMap.get( ( value - min ) / ( max - min ) );
			}
			graph.releaseRef( ref );
			return color;
		}
	}

	private class TargetVertexEdgeColorGenerator implements EdgeColorGenerator< E >
	{
		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< V > featureProjection;

		public TargetVertexEdgeColorGenerator( final FeatureProjection< V > featureProjection, final ColorMap colorMap, final double min, final double max )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@Override
		public Color color( final E edge )
		{
			Color color;
			final V ref = graph.vertexRef();
			final V vertex = edge.getTarget( ref );
			if ( !featureProjection.isSet( vertex ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = featureProjection.value( vertex );
				color = colorMap.get( ( value - min ) / ( max - min ) );
			}
			graph.releaseRef( ref );
			return color;
		}
	}
}

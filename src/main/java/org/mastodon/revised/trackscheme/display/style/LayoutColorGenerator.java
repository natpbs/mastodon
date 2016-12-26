package org.mastodon.revised.trackscheme.display.style;

import java.awt.Color;

import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle.UpdateListener;
import org.mastodon.revised.ui.util.ColorMap;

/**
 * Generate color generators for vertices and edges following hints from a
 * {@link TrackSchemeStyle}.
 *
 * @author Jean-Yves Tinevez
 */
public class LayoutColorGenerator implements UpdateListener, VertexColorGenerator< TrackSchemeVertex >, EdgeColorGenerator< TrackSchemeEdge >
{

	private final TrackSchemeGraph< ?, ? > graph;

	private TrackSchemeStyle style = TrackSchemeStyle.defaultStyle();

	private VertexColorGenerator< TrackSchemeVertex > vertexColorGenerator;

	private EdgeColorGenerator< TrackSchemeEdge > edgeColorGenerator;

	@SuppressWarnings( "rawtypes" )
	private FeatureProjection vertexFeatureProperties;

	@SuppressWarnings( "rawtypes" )
	private FeatureProjection edgeFeatureProperties;

	private final FeatureModel< TrackSchemeVertex, TrackSchemeEdge > features;

	public LayoutColorGenerator( final TrackSchemeGraph< ?, ? > graph,
			final FeatureModel< TrackSchemeVertex, TrackSchemeEdge > features )
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
		final VertexColorGenerator< TrackSchemeVertex > vcg;
		final FeatureProjection< ? > vfp;
		switch ( style.colorVertexBy )
		{
		case FIXED:
		default:
			vcg = new FixedVertexColorGenerator( style.vertexFillColor );
			vfp = null;
			break;
		case INCOMING_EDGE:
			vcg = new IncomingEdgeVertexColorGenerator( style.vertexColorMap, style.minVertexColorRange, style.maxVertexColorRange );
			vfp = features.getVertexProjection( style.vertexColorFeatureKey );
			break;
		case OUTGOING_EDGE:
			vcg = new OutgoingEdgeVertexColorGenerator( style.vertexColorMap, style.minVertexColorRange, style.maxVertexColorRange );
			vfp = features.getVertexProjection( style.vertexColorFeatureKey );
			break;
		case VERTEX:
			vcg = new ThisVertexColorGenerator( style.vertexColorMap, style.minVertexColorRange, style.maxVertexColorRange );
			vfp = features.getVertexProjection( style.vertexColorFeatureKey );
			break;
		}
		vertexColorGenerator = vcg;
		vertexFeatureProperties = vfp;

		final EdgeColorGenerator< TrackSchemeEdge > ecg;
		final FeatureProjection< ? > efp;
		switch ( style.colorEdgeBy )
		{
		case FIXED:
		default:
			ecg = new FixedEdgeColorGenerator( style.edgeColor );
			efp = null;
			break;
		case EDGE:
			ecg = new ThisEdgeColorGenerator( style.edgeColorMap, style.minEdgeColorRange, style.maxEdgeColorRange );
			efp = features.getVertexProjection( style.edgeColorFeatureKey );
			break;
		case SOURCE_VERTEX:
			ecg = new SourceVertexEdgeGenerator( style.edgeColorMap, style.minEdgeColorRange, style.maxEdgeColorRange );
			efp = features.getVertexProjection( style.edgeColorFeatureKey );
			break;
		case TARGET_VERTEX:
			ecg = new TargetVertexEdgeGenerator( style.edgeColorMap, style.minEdgeColorRange, style.maxEdgeColorRange );
			efp = features.getVertexProjection( style.edgeColorFeatureKey );
			break;
		}
		edgeColorGenerator = ecg;
		edgeFeatureProperties = efp;
	}


	@Override
	public Color color( final TrackSchemeEdge edge )
	{
		return edgeColorGenerator.color( edge );
	}

	@Override
	public Color color( final TrackSchemeVertex vertex )
	{
		return vertexColorGenerator.color( vertex );
	}

	/*
	 * Colorer classes.
	 */

	private static class FixedVertexColorGenerator implements VertexColorGenerator< TrackSchemeVertex >
	{

		private final Color color;

		public FixedVertexColorGenerator( final Color color )
		{
			this.color = color;
		}

		@Override
		public Color color( final TrackSchemeVertex vertex )
		{
			return color;
		}
	}

	private class ThisVertexColorGenerator implements VertexColorGenerator< TrackSchemeVertex >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		public ThisVertexColorGenerator( final ColorMap colorMap, final double min, final double max )
		{
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public Color color( final TrackSchemeVertex vertex )
		{
			if ( !vertexFeatureProperties.isSet( vertex ) )
				return colorMap.getMissingColor();

			final double value = vertexFeatureProperties.value( vertex );
			return colorMap.get( normalize( value, min, max ) );
		}
	}

	private class IncomingEdgeVertexColorGenerator implements VertexColorGenerator< TrackSchemeVertex >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		public IncomingEdgeVertexColorGenerator( final ColorMap colorMap, final double min, final double max )
		{
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public Color color( final TrackSchemeVertex vertex )
		{
			if ( vertex.incomingEdges().size() != 1 )
				return colorMap.get( Double.NaN );

			final Color color;
			final TrackSchemeEdge ref = graph.edgeRef();
			final TrackSchemeEdge edge = vertex.incomingEdges().get( 0, ref );
			if ( !vertexFeatureProperties.isSet( edge ) )
				color = colorMap.getMissingColor();
			else
			{
				final double value = vertexFeatureProperties.value( edge );
				color = colorMap.get( normalize( value, min, max ) );
			}
			graph.releaseRef( ref );
			return color;
		}
	}

	private class OutgoingEdgeVertexColorGenerator implements VertexColorGenerator< TrackSchemeVertex >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		public OutgoingEdgeVertexColorGenerator( final ColorMap colorMap, final double min, final double max )
		{
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public Color color( final TrackSchemeVertex vertex )
		{
			if ( vertex.outgoingEdges().size() != 1 )
				return colorMap.get( Double.NaN );

			final Color color;
			final TrackSchemeEdge ref = graph.edgeRef();
			final TrackSchemeEdge edge = vertex.outgoingEdges().get( 0, ref );
			if ( !vertexFeatureProperties.isSet( edge ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = vertexFeatureProperties.value( edge );
				color = colorMap.get( normalize( value, min, max ) );
			}
			graph.releaseRef( ref );
			return color;
		}
	}

	private class FixedEdgeColorGenerator implements EdgeColorGenerator< TrackSchemeEdge >
	{

		private final Color color;

		public FixedEdgeColorGenerator( final Color color )
		{
			this.color = color;
		}

		@Override
		public Color color( final TrackSchemeEdge edge )
		{
			return color;
		}
	}

	private class ThisEdgeColorGenerator implements EdgeColorGenerator< TrackSchemeEdge >
	{
		private final ColorMap colorMap;

		private final double min;

		private final double max;

		public ThisEdgeColorGenerator( final ColorMap colorMap, final double min, final double max )
		{
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public Color color( final TrackSchemeEdge edge )
		{
			if ( !edgeFeatureProperties.isSet( edge ) )
				return colorMap.getMissingColor();

			final double value = edgeFeatureProperties.value( edge );
			return colorMap.get( normalize( value, min, max ) );
		}
	}

	private class SourceVertexEdgeGenerator implements EdgeColorGenerator< TrackSchemeEdge >
	{
		private final ColorMap colorMap;

		private final double min;

		private final double max;

		public SourceVertexEdgeGenerator( final ColorMap colorMap, final double min, final double max )
		{
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public Color color( final TrackSchemeEdge edge )
		{
			Color color;
			final TrackSchemeVertex ref = graph.vertexRef();
			final TrackSchemeVertex vertex = edge.getSource( ref );
			if ( !edgeFeatureProperties.isSet( vertex ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = edgeFeatureProperties.value( vertex );
				color = colorMap.get( normalize( value, min, max ) );
			}
			graph.releaseRef( ref );
			return color;
		}
	}

	private class TargetVertexEdgeGenerator implements EdgeColorGenerator< TrackSchemeEdge >
	{
		private final ColorMap colorMap;

		private final double min;

		private final double max;

		public TargetVertexEdgeGenerator( final ColorMap colorMap, final double min, final double max )
		{
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public Color color( final TrackSchemeEdge edge )
		{
			Color color;
			final TrackSchemeVertex ref = graph.vertexRef();
			final TrackSchemeVertex vertex = edge.getTarget( ref );
			if ( !edgeFeatureProperties.isSet( vertex ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = edgeFeatureProperties.value( vertex );
				color = colorMap.get( normalize( value, min, max ) );
			}
			graph.releaseRef( ref );
			return color;
		}
	}

	private static final double normalize( final double value, final double min, final double max )
	{
		return ( value - min ) / ( max - min );
	}
}

package org.mastodon.revised.trackscheme.display.style;

import java.awt.Color;

import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle.UpdateListener;
import org.mastodon.revised.ui.EdgeColorGenerator;
import org.mastodon.revised.ui.VertexColorGenerator;
import org.mastodon.revised.ui.util.ColorMap;

/**
 * Color generator for vertices and edges from a feature model, following hints
 * from a {@link TrackSchemeStyle}.
 *
 * @author Jean-Yves Tinevez
 */
public class TrackSchemeFeaturesColorGenerator implements UpdateListener, VertexColorGenerator< TrackSchemeVertex >, EdgeColorGenerator< TrackSchemeEdge >
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

	private final BranchGraph< TrackSchemeVertex, TrackSchemeEdge > branchGraph;

	public TrackSchemeFeaturesColorGenerator( final TrackSchemeGraph< ?, ? > graph,
			final BranchGraph< TrackSchemeVertex, TrackSchemeEdge > branchGraph,
			final FeatureModel< TrackSchemeVertex, TrackSchemeEdge > features )
	{
		this.graph = graph;
		this.branchGraph = branchGraph;
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
			vfp = features.getEdgeProjection( style.vertexColorFeatureKey );
			break;
		case OUTGOING_EDGE:
			vcg = new OutgoingEdgeVertexColorGenerator( style.vertexColorMap, style.minVertexColorRange, style.maxVertexColorRange );
			vfp = features.getEdgeProjection( style.vertexColorFeatureKey );
			break;
		case VERTEX:
			vcg = new ThisVertexColorGenerator( style.vertexColorMap, style.minVertexColorRange, style.maxVertexColorRange );
			vfp = features.getVertexProjection( style.vertexColorFeatureKey );
			break;
		case BRANCH_VERTEX:
			vcg = new BranchVertexVertexColorGenerator( style.vertexColorMap, style.minVertexColorRange, style.maxVertexColorRange );
			vfp = features.getBranchVertexProjection( style.vertexColorFeatureKey );
			break;
		case BRANCH_EDGE:
			vcg = new BranchEdgeVertexColorGenerator( style.vertexColorMap, style.minVertexColorRange, style.maxVertexColorRange );
			vfp = features.getBranchEdgeProjection( style.vertexColorFeatureKey );
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
			efp = features.getEdgeProjection( style.edgeColorFeatureKey );
			break;
		case SOURCE_VERTEX:
			ecg = new SourceVertexEdgeColorGenerator( style.edgeColorMap, style.minEdgeColorRange, style.maxEdgeColorRange );
			efp = features.getVertexProjection( style.edgeColorFeatureKey );
			break;
		case TARGET_VERTEX:
			ecg = new TargetVertexEdgeColorGenerator( style.edgeColorMap, style.minEdgeColorRange, style.maxEdgeColorRange );
			efp = features.getVertexProjection( style.edgeColorFeatureKey );
			break;
		case BRANCH_EDGE:
			ecg = new BranchEdgeEdgeColorGenerator( style.edgeColorMap, style.minEdgeColorRange, style.maxEdgeColorRange );
			efp = features.getBranchEdgeProjection( style.edgeColorFeatureKey );
			break;
		case BRANCH_VERTEX:
			ecg = new BranchVertexEdgeColorGenerator( style.edgeColorMap, style.minEdgeColorRange, style.maxEdgeColorRange );
			efp = features.getBranchVertexProjection( style.edgeColorFeatureKey );
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

	private final class BranchVertexVertexColorGenerator implements VertexColorGenerator< TrackSchemeVertex >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		public BranchVertexVertexColorGenerator( final ColorMap colorMap, final double min, final double max )
		{
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public Color color( final TrackSchemeVertex vertex )
		{
			final Color color;
			final BranchVertex ref = branchGraph.vertexRef();
			final TrackSchemeVertex vref = graph.vertexRef();
			final TrackSchemeEdge eref = graph.edgeRef();

			TrackSchemeVertex source = vertex;
			BranchVertex bv = branchGraph.getBranchVertex( source, ref );
			while ( null == bv && !source.incomingEdges().isEmpty() )
			{
				// Climb up to find branch vertex
				source = source.incomingEdges().get( 0, eref ).getSource( vref );
				bv = branchGraph.getBranchVertex( source, ref );
			}

			if ( null == bv || !vertexFeatureProperties.isSet( bv ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = vertexFeatureProperties.value( bv );
				color = colorMap.get( normalize( value, min, max ) );
			}
			branchGraph.releaseRef( ref );
			graph.releaseRef( vref );
			graph.releaseRef( eref );
			return color;
		}

	}

	private class BranchEdgeVertexColorGenerator implements VertexColorGenerator< TrackSchemeVertex >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		public BranchEdgeVertexColorGenerator( final ColorMap colorMap, final double min, final double max )
		{
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public Color color( final TrackSchemeVertex vertex )
		{
			final Color color;
			final BranchEdge ref = branchGraph.edgeRef();
			final BranchEdge be = branchGraph.getBranchEdge( vertex, ref );

			if ( null == be || !vertexFeatureProperties.isSet( be ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = vertexFeatureProperties.value( be );
				color = colorMap.get( normalize( value, min, max ) );
			}
			branchGraph.releaseRef( ref );
			return color;
		}
	}

	private static class FixedEdgeColorGenerator implements EdgeColorGenerator< TrackSchemeEdge >
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

	private class SourceVertexEdgeColorGenerator implements EdgeColorGenerator< TrackSchemeEdge >
	{
		private final ColorMap colorMap;

		private final double min;

		private final double max;

		public SourceVertexEdgeColorGenerator( final ColorMap colorMap, final double min, final double max )
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

	private class TargetVertexEdgeColorGenerator implements EdgeColorGenerator< TrackSchemeEdge >
	{
		private final ColorMap colorMap;

		private final double min;

		private final double max;

		public TargetVertexEdgeColorGenerator( final ColorMap colorMap, final double min, final double max )
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

	private class BranchEdgeEdgeColorGenerator implements EdgeColorGenerator< TrackSchemeEdge >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		public BranchEdgeEdgeColorGenerator( final ColorMap colorMap, final double min, final double max )
		{
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public Color color( final TrackSchemeEdge edge )
		{
			final Color color;
			final BranchEdge ref = branchGraph.edgeRef();
			final BranchEdge be = branchGraph.getBranchEdge( edge, ref );

			if ( !edgeFeatureProperties.isSet( be ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = edgeFeatureProperties.value( be );
				color = colorMap.get( normalize( value, min, max ) );
			}
			branchGraph.releaseRef( ref );
			return color;
		}
	}

	private final class BranchVertexEdgeColorGenerator implements EdgeColorGenerator< TrackSchemeEdge >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		public BranchVertexEdgeColorGenerator( final ColorMap colorMap, final double min, final double max )
		{
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public Color color( final TrackSchemeEdge edge )
		{
			final Color color;
			final BranchVertex ref = branchGraph.vertexRef();
			final TrackSchemeVertex vref = graph.vertexRef();
			final TrackSchemeEdge eref = graph.edgeRef();

			TrackSchemeVertex source = edge.getSource( vref );
			BranchVertex bv = branchGraph.getBranchVertex( source, ref );
			while ( null == bv && !source.incomingEdges().isEmpty() )
			{
				// Climb up to find branch vertex
				source = source.incomingEdges().get( 0, eref ).getSource( vref );
				bv = branchGraph.getBranchVertex( source, ref );
			}

			if ( null == bv || !edgeFeatureProperties.isSet( bv ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = edgeFeatureProperties.value( bv );
				color = colorMap.get( normalize( value, min, max ) );
			}
			branchGraph.releaseRef( ref );
			graph.releaseRef( vref );
			graph.releaseRef( eref );
			return color;
		}

	}

	private static final double normalize( final double value, final double min, final double max )
	{
		return ( value - min ) / ( max - min );
	}
}

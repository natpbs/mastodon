package org.mastodon.revised.model.feature;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

public class DefaultFeatureRangeCalculator< V extends Vertex< E >, E extends Edge< V > > implements FeatureRangeCalculator
{

	private final FeatureModel< V, E > featureModel;

	private final ReadOnlyGraph< V, E > graph;

	public DefaultFeatureRangeCalculator( final ReadOnlyGraph< V, E > graph, final FeatureModel< V, E > featureModel )
	{
		this.graph = graph;
		this.featureModel = featureModel;
	}

	@Override
	public double[] getRange( final String projectionKey )
	{
		switch ( featureModel.getProjectionTarget( projectionKey ) )
		{
		case EDGE:
		{
			final FeatureProjection< E > projection = featureModel.getEdgeProjection( projectionKey );
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;
			for ( final E edge : graph.edges() )
			{
				if ( projection.isSet( edge ) )
				{
					final double value = projection.value( edge );
					if ( value > max )
						max = value;
					if ( value < min )
						min = value;
				}
			}
			return new double[] { min, max };
		}
		case GRAPH:
			break;
		case TIMEPOINT:
			break;
		case VERTEX:
			final FeatureProjection< V > projection = featureModel.getVertexProjection( projectionKey );
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;
			for ( final V vertex : graph.vertices() )
			{
				if ( projection.isSet( vertex ) )
				{
					final double value = projection.value( vertex );
					if ( value > max )
						max = value;
					if ( value < min )
						min = value;
				}
			}
			return new double[] { min, max };
		default:
			break;
		}
		return null;
	}

}

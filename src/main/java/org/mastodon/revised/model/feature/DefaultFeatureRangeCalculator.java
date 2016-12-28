package org.mastodon.revised.model.feature;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.spatial.HasTimepoint;

public class DefaultFeatureRangeCalculator< V extends Vertex< E > & HasTimepoint, E extends Edge< V > > implements FeatureRangeCalculator
{

	private final FeatureModel< V, E > featureModel;

	private final ReadOnlyGraph< V, E > graph;

	private final BranchGraph< V, E > branchGraph;

	public DefaultFeatureRangeCalculator(
			final ReadOnlyGraph< V, E > graph,
			final BranchGraph< V, E > branchGraph,
			final FeatureModel< V, E > featureModel )
	{
		this.graph = graph;
		this.branchGraph = branchGraph;
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
		case VERTEX:
		{
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
		}
		case BRANCH_EDGE:
		{
			final FeatureProjection< BranchEdge > projection = featureModel.getBranchEdgeProjection( projectionKey );
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;
			for ( final BranchEdge edge : branchGraph.edges() )
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
		default:
			break;
		}
		return null;
	}

}

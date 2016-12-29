package org.mastodon.revised.model.branchgraph;

import java.util.Collections;
import java.util.Set;

import org.mastodon.features.Feature;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.revised.model.feature.FeatureComputer;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.model.feature.FeatureTarget;

/**
 * Exposes branch vertex and edge features as vertex and edge features in a
 * feature model of {@link BranchVertex} and {@link BranchEdge}. What would be
 * the branch vertex feature of a branch graph (the branch graph of a branch
 * graph) are identical. This adapter actually restricts the amount of available
 * features and projections so that the feature model can be used along with a
 * branch graph.
 * <p>
 * When requested for the target of some available feature or projection, the
 * following downgrade is applied:
 * <ul>
 * <li>BranchVertex feature & projection -> Vertex feature & projection.
 * <li>BranchEdge feature & projection -> Edge feature & projection.
 * <li>TimePoint feature & projection -> Timepoint feature & projection.
 * <li>Vertex feature & projection -> Undefined feature & projection.
 * <li>Edge feature & projection -> Undefined feature & projection.
 * <li>Undefined feature & projection -> Undefined feature & projection.
 * </ul>
 * <p>
 * Concomitantly, when requested for an actual projection, the vertex or edge
 * projection type is upgraded to its branch graph counterpart. This is made so
 * that when requested for a <b>vertex</b> feature projection of a <b>branch
 * graph</b>, a <b>branch vertex</b> feature projection is returned.
 *
 * @author Jean-Yves Tinevez
 * @param <V>
 *            the type of the non-branch vertex in the linked graph.
 * @param <E>
 *            the type of the non-branch edge in the linked graph.
 */
public class BranchGraphFeatureModelAdapter< V extends Vertex< E >, E extends Edge< V > >
		implements FeatureModel< BranchVertex, BranchEdge >
{

	private final FeatureModel< V, E > featureModel;

	public BranchGraphFeatureModelAdapter( final FeatureModel< V, E > featureModel )
	{
		this.featureModel = featureModel;
	}

	@Override
	public FeatureTarget getFeatureTarget( final String featureKey )
	{
		final FeatureTarget target = featureModel.getFeatureTarget( featureKey );
		// Downgrade branch vertex and edge.
		switch ( target )
		{
		case BRANCH_EDGE:
			return FeatureTarget.EDGE;
		case BRANCH_VERTEX:
			return FeatureTarget.VERTEX;
		case TIMEPOINT:
			return FeatureTarget.TIMEPOINT;
		default:
			return FeatureTarget.UNDEFINED;
		}
	}

	@Override
	public Set< String > getFeatureKeys( final FeatureTarget target )
	{
		// Upgrade request.
		switch ( target )
		{
		case BRANCH_EDGE:
		case EDGE:
			return featureModel.getFeatureKeys( FeatureTarget.BRANCH_EDGE );
		case VERTEX:
		case BRANCH_VERTEX:
			return featureModel.getFeatureKeys( FeatureTarget.BRANCH_VERTEX );
		case TIMEPOINT:
			return featureModel.getFeatureKeys( FeatureTarget.TIMEPOINT );
		default:
			return Collections.emptySet();
		}
	}

	@Override
	public FeatureTarget getProjectionTarget( final String projectionKey )
	{
		final FeatureTarget target = featureModel.getProjectionTarget( projectionKey );
		// Downgrade branch vertex and edge.
		switch ( target )
		{
		case BRANCH_EDGE:
			return FeatureTarget.EDGE;
		case BRANCH_VERTEX:
			return FeatureTarget.VERTEX;
		case TIMEPOINT:
			return FeatureTarget.TIMEPOINT;
		default:
			return FeatureTarget.UNDEFINED;
		}
	}

	@Override
	public Set< String > getProjectionKeys( final FeatureTarget target )
	{
		// Upgrade request.
		switch ( target )
		{
		case BRANCH_EDGE:
		case EDGE:
			return featureModel.getProjectionKeys( FeatureTarget.BRANCH_EDGE );
		case VERTEX:
		case BRANCH_VERTEX:
			return featureModel.getProjectionKeys( FeatureTarget.BRANCH_VERTEX );
		case TIMEPOINT:
			return featureModel.getProjectionKeys( FeatureTarget.TIMEPOINT );
		default:
			return Collections.emptySet();
		}
	}

	@Override
	public FeatureProjection< BranchEdge > getEdgeProjection( final String projectionKey )
	{
		// Upgrade
		return featureModel.getBranchEdgeProjection( projectionKey );
	}

	@Override
	public FeatureProjection< BranchVertex > getVertexProjection( final String projectionKey )
	{
		// Upgrade
		return featureModel.getBranchVertexProjection( projectionKey );
	}

	@Override
	public FeatureProjection< BranchEdge > getBranchEdgeProjection( final String projectionKey )
	{
		return featureModel.getBranchEdgeProjection( projectionKey );
	}

	@Override
	public FeatureProjection< BranchVertex > getBranchVertexProjection( final String projectionKey )
	{
		return featureModel.getBranchVertexProjection( projectionKey );
	}

	@Override
	public Feature< ?, ?, ? > getFeature( final String featureKey )
	{
		return featureModel.getFeature( featureKey );
	}

	@Override
	public void clear()
	{
		featureModel.clear();
	}

	@Override
	public void declareFeature( final FeatureComputer< ?, ?, ? > fc )
	{
		throw new UnsupportedOperationException( "Cannot declare a generic feature through a BranchGraph adapter." );
	}

}

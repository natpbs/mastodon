package org.mastodon.revised.model.feature;

import org.mastodon.features.Feature;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchVertex;

public interface FeatureModel< V, E > extends FeatureKeys
{

	public FeatureProjection< E > getEdgeProjection( final String projectionKey );

	public FeatureProjection< V > getVertexProjection( final String projectionKey );

	public FeatureProjection< BranchEdge > getBranchEdgeProjection( final String projectionKey );

	public FeatureProjection< BranchVertex > getBranchVertexProjection( final String projectionKey );

	public Feature< ?, ?, ? > getFeature( final String featureKey );

	public void clear();

	public void declareFeature( final FeatureComputer< ?, ?, ? > fc );
}

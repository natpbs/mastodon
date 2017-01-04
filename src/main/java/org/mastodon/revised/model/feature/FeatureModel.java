package org.mastodon.revised.model.feature;

import org.mastodon.features.Feature;

public interface FeatureModel< V, E > extends FeatureKeys
{
	public FeatureProjection< E > getEdgeProjection( final String projectionKey );

	public FeatureProjection< V > getVertexProjection( final String projectionKey );

	public Feature< ?, ?, ? > getFeature( final String featureKey );

	public void clear();

	public void declareFeature( final FeatureComputer< ?, ?, ? > fc );
}

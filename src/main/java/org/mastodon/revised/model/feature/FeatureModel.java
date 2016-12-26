package org.mastodon.revised.model.feature;

import org.mastodon.features.Feature;

public interface FeatureModel< V, E > extends FeatureKeys
{

	public enum FeatureTarget
	{
		VERTEX, EDGE, GRAPH, TIMEPOINT;
	}

	public FeatureTarget getProjectionTarget( final String projectionKey );

	public FeatureTarget getFeatureTarget( final String featureKey );

	public FeatureProjection< E > getEdgeProjection( final String projectionKey );

	public FeatureProjection< V > getVertexProjection( final String projectionKey );

	public Feature< ?, ?, ? > getFeature( final String featureKey );

	public void clear();

	public void declareFeature( final FeatureComputer< ?, ?, ? > fc );
}

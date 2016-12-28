package org.mastodon.revised.model.feature;

import java.util.Set;

public interface FeatureKeys
{

	public FeatureTarget getFeatureTarget( final String featureKey );

	public Set< String > getFeatureKeys( FeatureTarget target );

	public FeatureTarget getProjectionTarget( final String projectionKey );

	public Set< String > getProjectionKeys( FeatureTarget target );
}

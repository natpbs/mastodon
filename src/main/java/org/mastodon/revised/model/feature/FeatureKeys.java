package org.mastodon.revised.model.feature;

import java.util.Set;

public interface FeatureKeys
{
	public Set< String > getEdgeProjectionKeys();

	public Set< String > getEdgeFeatureKeys();

	public Set< String > getVertexProjectionKeys();

	public Set< String > getVertexFeatureKeys();
}

package org.mastodon.revised.model.feature;

public class FeatureFilter
{

	public final String key;

	public final double threshold;

	public final boolean above;

	public FeatureFilter( final String key, final double threshold, final boolean above )
	{
		this.key = key;
		this.threshold = threshold;
		this.above = above;
	}
}

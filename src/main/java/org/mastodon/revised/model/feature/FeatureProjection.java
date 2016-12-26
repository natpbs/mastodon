package org.mastodon.revised.model.feature;

public interface FeatureProjection< K >
{
	public boolean isSet( K obj );

	public double value( K obj );

}

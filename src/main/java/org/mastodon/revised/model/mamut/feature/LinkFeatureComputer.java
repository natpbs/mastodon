package org.mastodon.revised.model.mamut.feature;

import org.mastodon.revised.model.mamut.FeatureModel.FeatureTarget;

public abstract class LinkFeatureComputer implements FeatureComputer
{
	@Override
	public FeatureTarget getTarget()
	{
		return FeatureTarget.EDGE;
	}

}
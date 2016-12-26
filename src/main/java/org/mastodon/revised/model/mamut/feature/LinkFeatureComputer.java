package org.mastodon.revised.model.mamut.feature;

import org.mastodon.features.Feature;
import org.mastodon.revised.model.AbstractModel;
import org.mastodon.revised.model.feature.FeatureComputer;
import org.mastodon.revised.model.feature.FeatureModel.FeatureTarget;
import org.mastodon.revised.model.mamut.Link;

public abstract class LinkFeatureComputer< K extends Feature< ?, Link, ? >, AM extends AbstractModel< ?, ?, ? > > implements FeatureComputer< K, Link, AM >
{
	@Override
	public FeatureTarget getTarget()
	{
		return FeatureTarget.EDGE;
	}

}
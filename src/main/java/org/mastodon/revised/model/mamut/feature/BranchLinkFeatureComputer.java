package org.mastodon.revised.model.mamut.feature;

import org.mastodon.features.Feature;
import org.mastodon.revised.model.AbstractModel;
import org.mastodon.revised.model.feature.FeatureComputer;
import org.mastodon.revised.model.feature.FeatureTarget;
import org.mastodon.revised.model.mamut.branchgraph.BranchEdge;

public abstract class BranchLinkFeatureComputer< K extends Feature< ?, BranchEdge, ? >, AM extends AbstractModel< ?, ?, ? > >
		implements FeatureComputer< K, BranchEdge, AM >
{

	@Override
	public FeatureTarget getTarget()
	{
		return FeatureTarget.EDGE;
	}

}

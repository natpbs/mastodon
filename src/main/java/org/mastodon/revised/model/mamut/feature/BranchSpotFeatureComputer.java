package org.mastodon.revised.model.mamut.feature;

import org.mastodon.features.Feature;
import org.mastodon.revised.model.AbstractModel;
import org.mastodon.revised.model.feature.FeatureComputer;
import org.mastodon.revised.model.feature.FeatureTarget;
import org.mastodon.revised.model.mamut.branchgraph.BranchVertex;

public abstract class BranchSpotFeatureComputer< K extends Feature< ?, BranchVertex, ? >, AM extends AbstractModel< ?, ?, ? > >
		implements FeatureComputer< K, BranchVertex, AM >
{

	@Override
	public FeatureTarget getTarget()
	{
		return FeatureTarget.VERTEX;
	}

}

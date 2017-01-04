package org.mastodon.revised.model.mamut.feature;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.mastodon.features.IntFeature;
import org.mastodon.revised.model.feature.DefaultFeatureProjectors;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.branchgraph.BranchVertex;
import org.scijava.plugin.Plugin;

@Plugin( type = SpotFeatureComputer.class, name = "N divisions" )
public class BranchSpotNdivisionFeatureComputer extends BranchSpotFeatureComputer< IntFeature< BranchVertex >, Model >
{

	private static final String NAME = "N divisions";

	private static final IntFeature< BranchVertex > FEATURE = new IntFeature<>( NAME, -1 );

	public static final FeatureProjection< BranchVertex > FEATURE_PROJECTION = DefaultFeatureProjectors.project( FEATURE );

	@Override
	public Set< String > getDependencies()
	{
		return Collections.emptySet();
	}

	@Override
	public void compute( final Model model )
	{
		for ( final BranchVertex bv : model.getBranchGraph().vertices() )
			bv.feature( FEATURE ).set( bv.outgoingEdges().size() );
	}

	@Override
	public Map< String, FeatureProjection< BranchVertex > > getProjections()
	{
		return Collections.singletonMap( NAME, FEATURE_PROJECTION );
	}

	@Override
	public IntFeature< BranchVertex > getFeature()
	{
		return FEATURE;
	}

}

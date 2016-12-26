package org.mastodon.revised.model.mamut.feature;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.mastodon.features.IntFeature;
import org.mastodon.pool.PoolCollectionWrapper;
import org.mastodon.revised.model.feature.DefaultFeatureProjectors;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.Spot;
import org.scijava.plugin.Plugin;

@Plugin( type = SpotFeatureComputer.class, name = "spot_nlinks" )
public class SpotNLinksComputer extends SpotFeatureComputer< IntFeature< Spot >, Model >
{
	private static final String NAME = "spot_nlinks";

	private static final IntFeature< Spot > FEATURE = new IntFeature<>( NAME, -1 );

	public static final FeatureProjection< Spot > FEATURE_PROJECTION = DefaultFeatureProjectors.project( FEATURE );

	@Override
	public Set< String > getDependencies()
	{
		return Collections.emptySet();
	}

	@Override
	public void compute( final Model model )
	{
		final PoolCollectionWrapper< Spot > vertices = model.getGraph().vertices();
		for ( final Spot spot : vertices )
			spot.feature( FEATURE ).set( spot.edges().size() );
	}

	@Override
	public IntFeature< Spot > getFeature()
	{
		return FEATURE;
	}

	@Override
	public Map< String, FeatureProjection< Spot > > getProjections()
	{
		return Collections.singletonMap( FEATURE.getKey(), FEATURE_PROJECTION );
	}
}
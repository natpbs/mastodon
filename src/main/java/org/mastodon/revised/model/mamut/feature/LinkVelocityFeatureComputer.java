package org.mastodon.revised.model.mamut.feature;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.mastodon.features.DoubleFeature;
import org.mastodon.revised.model.feature.DefaultFeatureProjectors;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.ModelGraph;
import org.mastodon.revised.model.mamut.Spot;
import org.scijava.plugin.Plugin;

@Plugin( type = LinkFeatureComputer.class, name = "link_velocity" )
public class LinkVelocityFeatureComputer extends LinkFeatureComputer< DoubleFeature< Link >, Model >
{

	public static final String NAME = "link_velocity";

	public static final DoubleFeature< Link > FEATURE = new DoubleFeature<>( NAME, Double.NaN );

	public static final FeatureProjection< Link > FEATURE_PROJECTION = DefaultFeatureProjectors.project( FEATURE );

	@Override
	public Set< String > getDependencies()
	{
		return Collections.singleton( LinkDisplacementComputer.NAME );
	}

	@Override
	public void compute( final Model model )
	{
		final ModelGraph graph = model.getGraph();
		final Spot ref1 = graph.vertexRef();
		final Spot ref2 = graph.vertexRef();

		for ( final Link link : graph.edges() )
		{
			if ( link.feature( LinkDisplacementComputer.FEATURE ).isSet() )
			{
				final double disp = link.feature( LinkDisplacementComputer.FEATURE ).getDouble();
				final Spot source = link.getSource( ref1 );
				final Spot target = link.getTarget( ref2 );
				final double dt = Math.abs( source.getTimepoint() - target.getTimepoint() );
				link.feature( FEATURE ).set( disp / dt );
			}
		}

		graph.releaseRef( ref1 );
		graph.releaseRef( ref2 );
	}

	@Override
	public DoubleFeature< Link > getFeature()
	{
		return FEATURE;
	}

	@Override
	public Map< String, FeatureProjection< Link > > getProjections()
	{
		return Collections.singletonMap( FEATURE.getKey(), FEATURE_PROJECTION );
	}
}
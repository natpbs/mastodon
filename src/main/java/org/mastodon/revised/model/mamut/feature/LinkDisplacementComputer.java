package org.mastodon.revised.model.mamut.feature;

import java.util.Collections;
import java.util.Set;

import org.mastodon.features.DoubleFeature;
import org.mastodon.features.Feature;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.ModelGraph;
import org.mastodon.revised.model.mamut.Spot;
import org.scijava.plugin.Plugin;

@Plugin( type = LinkFeatureComputer.class, name = "link_displacement" )
public class LinkDisplacementComputer extends LinkFeatureComputer
{

	public static final String NAME = "link_displacement";

	public static final DoubleFeature< Link > FEATURE = new DoubleFeature<>( NAME, Double.NaN );

	@Override
	public Set< String > getDependencies()
	{
		return Collections.emptySet();
	}

	@Override
	public void compute( final Model model )
	{
		final ModelGraph graph = model.getGraph();
		final Spot ref1 = graph.vertexRef();
		final Spot ref2 = graph.vertexRef();

		for ( final Link link : graph.edges() )
		{
			final Spot source = link.getSource( ref1 );
			final Spot target = link.getTarget( ref2 );
			double d2 = 0.;
			for ( int d = 0; d < 3; d++ )
			{
				final double dx = source.getDoublePosition( d ) - target.getDoublePosition( d );
				d2 += dx * dx;
			}
			link.feature( FEATURE ).set( Math.sqrt( d2 ) );
		}

		graph.releaseRef( ref1 );
		graph.releaseRef( ref2 );
	}

	@Override
	public Feature< ?, ?, ? > getFeature()
	{
		return FEATURE;
	}
}
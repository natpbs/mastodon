package org.mastodon.revised.model.mamut.feature;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.mastodon.features.IntFeature;
import org.mastodon.revised.model.feature.DefaultFeatureProjectors;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.ModelGraph;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.model.mamut.branchgraph.BranchEdge;
import org.mastodon.revised.model.mamut.branchgraph.BranchVertex;
import org.mastodon.revised.model.mamut.branchgraph.ModelBranchGraph;
import org.scijava.plugin.Plugin;

@Plugin( type = BranchLinkFeatureComputer.class, name = "Branch N spots" )
public class BranchLinkNSpotsFeatureComputer extends BranchLinkFeatureComputer< IntFeature< BranchEdge >, Model >
{

	private static final String NAME = "Branch N spots";

	public static final IntFeature< BranchEdge > FEATURE = new IntFeature<>( NAME, -1 );

	public static final FeatureProjection< BranchEdge > FEATURE_PROJECTION = DefaultFeatureProjectors.project( FEATURE );

	@Override
	public Set< String > getDependencies()
	{
		return Collections.emptySet();
	}

	@Override
	public void compute( final Model model )
	{
		final ModelGraph graph = model.getGraph();
		final ModelBranchGraph branchGraph = model.getBranchGraph();

		final Link eref = graph.edgeRef();
		final Spot vref = graph.vertexRef();
		final BranchVertex bvref = branchGraph.vertexRef();
		for ( final BranchEdge be : branchGraph.edges() )
		{
			int nspots = 0;
			Link link = branchGraph.getLinkedEdge( be, eref );
			Spot target = link.getTarget( vref );
			while ( null == branchGraph.getBranchVertex( target, bvref ) && ( !target.outgoingEdges().isEmpty() ) )
			{
				link = target.outgoingEdges().get( 0, eref );
				target = link.getTarget( vref );
				nspots++;
			}
			be.feature( FEATURE ).set( nspots );
		}
		branchGraph.releaseRef( bvref );
		graph.releaseRef( vref );
		graph.releaseRef( eref );
	}

	@Override
	public IntFeature< BranchEdge > getFeature()
	{
		return FEATURE;
	}

	@Override
	public Map< String, FeatureProjection< BranchEdge > > getProjections()
	{
		return Collections.singletonMap( NAME, FEATURE_PROJECTION );
	}

}

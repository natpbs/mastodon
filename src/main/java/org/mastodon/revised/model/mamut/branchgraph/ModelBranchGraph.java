package org.mastodon.revised.model.mamut.branchgraph;

import org.mastodon.RefPool;
import org.mastodon.graph.branch.BranchGraphImp;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.ModelGraph;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.spatial.HasTimepoint;

import net.imglib2.RealLocalizable;

/**
 * A branch-graph specific for {@link ModelGraph}, whose vertices implements the
 * {@link RealLocalizable} and {@link HasTimepoint} interfaces, exposing the
 * {@link Spot} they are linked to.
 *
 * @author Jean-Yves Tinevez.
 *
 */
public class ModelBranchGraph
		extends BranchGraphImp< Spot, Link, BranchVertex, BranchEdge, BranchVertexPool, BranchEdgePool, ByteMappedElement >
{

	public ModelBranchGraph( final ModelGraph graph, final RefPool< Spot > vertexBimap )
	{
		super( graph, new BranchEdgePool( 1000, new BranchVertexPool( vertexBimap, 1000 ) ) );
	}

	@Override
	public BranchVertex init( final BranchVertex bv, final Spot v )
	{
		return bv.init( v );
	}

	@Override
	public BranchEdge init( final BranchEdge be, final Link e )
	{
		return be.init();
	}
}

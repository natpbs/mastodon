package org.mastodon.revised.model.branchgraph;

import org.mastodon.adapter.RefBimap;
import org.mastodon.collection.RefCollection;
import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphChangeListener;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;

public class BranchGraphAdapter< V extends Vertex< E >, E extends Edge< V >, WV extends Vertex< WE >, WE extends Edge< WV > >
		implements BranchGraph< WV, WE >
{

	private final BranchGraph< V, E > branchGraph;

	private final RefBimap< V, WV > vertexMap;

	private final RefBimap< E, WE > edgeMap;

	public BranchGraphAdapter(
			final BranchGraph< V, E > branchGraph,
			final RefBimap< V, WV > vertexMap,
			final RefBimap< E, WE > edgeMap )
	{
		this.branchGraph = branchGraph;
		this.vertexMap = vertexMap;
		this.edgeMap = edgeMap;
	}

	@Override
	public WE getLinkedEdge( final BranchEdge be, final WE ref )
	{
		return edgeMap.getRight( branchGraph.getLinkedEdge( be, edgeMap.reusableLeftRef( ref ) ),
				edgeMap.reusableRightRef() );
	}

	@Override
	public WV getLinkedVertex( final BranchVertex bv, final WV ref )
	{
		return vertexMap.getRight( branchGraph.getLinkedVertex( bv, vertexMap.reusableLeftRef( ref ) ),
				vertexMap.reusableRightRef() );
	}

	@Override
	public BranchEdge getBranchEdge( final WE edge, final BranchEdge ref )
	{
		return branchGraph.getBranchEdge( edgeMap.getLeft( edge ), ref );
	}

	@Override
	public BranchEdge getBranchEdge( final WV vertex, final BranchEdge ref )
	{
		return branchGraph.getBranchEdge( vertexMap.getLeft( vertex ), ref );
	}

	@Override
	public BranchVertex getBranchVertex( final WV vertex, final BranchVertex ref )
	{
		return branchGraph.getBranchVertex( vertexMap.getLeft( vertex ), ref );
	}

	@Override
	public BranchEdge getEdge( final BranchVertex source, final BranchVertex target )
	{
		return branchGraph.getEdge( source, target );
	}

	@Override
	public BranchEdge getEdge( final BranchVertex source, final BranchVertex target, final BranchEdge ref )
	{
		return branchGraph.getEdge( source, target, ref );
	}

	@Override
	public BranchVertex vertexRef()
	{
		return branchGraph.vertexRef();
	}

	@Override
	public BranchEdge edgeRef()
	{
		return branchGraph.edgeRef();
	}

	@Override
	public void releaseRef( final BranchVertex ref )
	{
		branchGraph.releaseRef( ref );
	}

	@Override
	public void releaseRef( final BranchEdge ref )
	{
		branchGraph.releaseRef( ref );
	}

	@Override
	public RefCollection< BranchVertex > vertices()
	{
		return branchGraph.vertices();
	}

	@Override
	public RefCollection< BranchEdge > edges()
	{
		return branchGraph.edges();
	}

	@Override
	public GraphIdBimap< BranchVertex, BranchEdge > getGraphIdBimap()
	{
		return branchGraph.getGraphIdBimap();
	}

	@Override
	public boolean addGraphListener( final GraphListener< BranchVertex, BranchEdge > listener )
	{
		return branchGraph.addGraphListener( listener );
	}

	@Override
	public boolean removeGraphListener( final GraphListener< BranchVertex, BranchEdge > listener )
	{
		return branchGraph.removeGraphListener( listener );
	}

	@Override
	public boolean addGraphChangeListener( final GraphChangeListener listener )
	{
		return branchGraph.addGraphChangeListener( listener );
	}

	@Override
	public boolean removeGraphChangeListener( final GraphChangeListener listener )
	{
		return branchGraph.removeGraphChangeListener( listener );
	}
}

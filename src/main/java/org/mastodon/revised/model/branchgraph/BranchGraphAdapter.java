package org.mastodon.revised.model.branchgraph;

import java.util.Iterator;

import org.mastodon.adapter.RefBimap;
import org.mastodon.collection.RefCollection;
import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphChangeListener;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchGraph;

public class BranchGraphAdapter<
	BV extends Vertex< BE >,
	BE extends Edge< BV >,
	V extends Vertex< E >,
	E extends Edge< V >,
	WV extends Vertex< WE >,
	WE extends Edge< WV > >
		implements BranchGraph< BV, BE, WV, WE >
{

	private final BranchGraph< BV, BE, V, E > branchGraph;

	private final RefBimap< V, WV > vertexMap;

	private final RefBimap< E, WE > edgeMap;

	public BranchGraphAdapter(
			final BranchGraph< BV, BE, V, E > branchGraph,
			final RefBimap< V, WV > vertexMap,
			final RefBimap< E, WE > edgeMap )
	{
		this.branchGraph = branchGraph;
		this.vertexMap = vertexMap;
		this.edgeMap = edgeMap;
	}

	@Override
	public boolean addGraphListener( final GraphListener< BV, BE > listener )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeGraphListener( final GraphListener< BV, BE > listener )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addGraphChangeListener( final GraphChangeListener listener )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeGraphChangeListener( final GraphChangeListener listener )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BE getEdge( final BV source, final BV target )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BE getEdge( final BV source, final BV target, final BE ref )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BV vertexRef()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BE edgeRef()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void releaseRef( final BV ref )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseRef( final BE ref )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public RefCollection< BV > vertices()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RefCollection< BE > edges()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WE getLinkedEdge( final BE be, final WE ref )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WV getLinkedVertex( final BV bv, final WV ref )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BE getBranchEdge( final WE edge, final BE ref )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BE getBranchEdge( final WV vertex, final BE ref )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BV getBranchVertex( final WV vertex, final BV ref )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphIdBimap< BV, BE > getGraphIdBimap()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator< WV > vertexBranchIterator( final BE edge )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator< WE > edgeBranchIterator( final BE edge )
	{
		// TODO Auto-generated method stub
		return null;
	}

}

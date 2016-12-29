package org.mastodon.revised.model.branchgraph;

import org.mastodon.collection.RefCollection;
import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphChangeListener;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.spatial.HasTimepoint;

/**
 * The branch graph of a branch graph.
 * <p>
 * The branch graph of a branch graph is by definition itself, with the type of
 * its wrapped vertex and edge changed to {@link BranchVertex} and
 * {@link BranchEdge}.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <V>
 * @param <E>
 */
public class BranchGraphIdentity< V extends Vertex< E > & HasTimepoint, E extends Edge< V > >
		implements BranchGraph< BranchVertex, BranchEdge >
{

	private final BranchGraph< V, E > graph;

	public BranchGraphIdentity( final BranchGraph< V, E > graph )
	{
		this.graph = graph;
	}

	@Override
	public boolean addGraphListener( final GraphListener< BranchVertex, BranchEdge > listener )
	{
		return graph.addGraphListener( listener );
	}

	@Override
	public boolean removeGraphListener( final GraphListener< BranchVertex, BranchEdge > listener )
	{
		return graph.removeGraphListener( listener );
	}

	@Override
	public boolean addGraphChangeListener( final GraphChangeListener listener )
	{
		return graph.addGraphChangeListener( listener );
	}

	@Override
	public boolean removeGraphChangeListener( final GraphChangeListener listener )
	{
		return graph.removeGraphChangeListener( listener );
	}

	@Override
	public BranchEdge getEdge( final BranchVertex source, final BranchVertex target )
	{
		return getEdge( source, target, graph.edgeRef() );
	}

	@Override
	public BranchEdge getEdge( final BranchVertex source, final BranchVertex target, final BranchEdge ref )
	{
		return graph.getEdge( source, target, ref );
	}

	@Override
	public BranchVertex vertexRef()
	{
		return graph.vertexRef();
	}

	@Override
	public BranchEdge edgeRef()
	{
		return graph.edgeRef();
	}

	@Override
	public void releaseRef( final BranchVertex ref )
	{
		graph.releaseRef( ref );
	}

	@Override
	public void releaseRef( final BranchEdge ref )
	{
		graph.releaseRef( ref );
	}

	@Override
	public RefCollection< BranchVertex > vertices()
	{
		return graph.vertices();
	}

	@Override
	public RefCollection< BranchEdge > edges()
	{
		return graph.edges();
	}

	@Override
	public BranchEdge getLinkedEdge( final BranchEdge be, final BranchEdge ref )
	{
		return be;
	}

	@Override
	public BranchVertex getLinkedVertex( final BranchVertex bv, final BranchVertex ref )
	{
		return bv;
	}

	@Override
	public BranchEdge getBranchEdge( final BranchEdge edge, final BranchEdge ref )
	{
		return edge;
	}

	@Override
	public BranchEdge getBranchEdge( final BranchVertex vertex, final BranchEdge ref )
	{
		return null;
	}

	@Override
	public BranchVertex getBranchVertex( final BranchVertex vertex, final BranchVertex ref )
	{
		return vertex;
	}

	@Override
	public GraphIdBimap< BranchVertex, BranchEdge > getGraphIdBimap()
	{
		return graph.getGraphIdBimap();
	}
}

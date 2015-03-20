package net.trackmate.graph.algorithm;

import java.util.Iterator;

import net.trackmate.graph.Edge;
import net.trackmate.graph.Graph;
import net.trackmate.graph.Vertex;
import net.trackmate.graph.collection.RefCollection;
import net.trackmate.graph.collection.RefList;
import net.trackmate.graph.collection.RefSet;

/**
 * A topological order sort for a direct acyclic graph.
 * <p>
 * If the graph provided is not acyclic, the flag returned by the
 * {@link #hasFailed()} method set to <code>true</code> to indicate the problem.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <V>
 *            the vertices type.
 */
public class TopologicalSort< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphAlgorithm< V, E >
{
	private boolean failed;

	private final RefCollection< V > vertices;

	private RefSet< V > marked;

	private RefSet< V > temporaryMarked;

	private final RefList< V > list;

	public TopologicalSort( final RefCollection< V > vertices, final Graph< V, E > graph )
	{
		super( graph );
		this.vertices = vertices;
		this.failed = false;
		this.marked = createVertexSet( vertices.size() );
		this.temporaryMarked = createVertexSet();
		this.list = createVertexList( vertices.size() );
		fetchList();
	}

	/**
	 * Returns the topologically sorted vertices in a list.
	 *
	 * @return a new {@link RefList} resulting from this sort operation.
	 */
	public RefList< V > get()
	{
		return list;
	}

	/**
	 * Returns <code>true</code> if the graph iterated has a cycle.
	 *
	 * @return <code>true</code> if the graph iterated is not a directed acyclic
	 *         graph.
	 */
	public boolean hasFailed()
	{
		return failed;
	}

	private void fetchList()
	{
		final Iterator< V > vit = vertices.iterator();
		while ( vit.hasNext() && !failed )
		{
			final V v1 = vit.next();
			if ( !marked.contains( v1 ) )
			{
				visit( v1 );
			}
		}
		marked = null;
		temporaryMarked = null;

	}

	private void visit( final V vertex )
	{
		if ( temporaryMarked.contains( vertex ) )
		{
			failed = true;
			return;
		}

		if ( !marked.contains( vertex ) )
		{
			V v2 = graph.vertexRef();
			temporaryMarked.add( vertex );
			for ( final Edge< V > e : vertex.outgoingEdges() )
			{
				v2 = e.getTarget( v2 );
				visit( v2 );
			}
			graph.releaseRef( v2 );

			marked.add( vertex );
			temporaryMarked.remove( vertex );
			list.add( vertex );
		}
	}
}
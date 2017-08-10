package org.mastodon.revised.model.mamut;

import java.util.Collection;
import java.util.Iterator;

import org.mastodon.collection.RefCollection;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Edges;
import org.mastodon.graph.GraphChangeListener;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.properties.BooleanPropertyMap;

public class GraphVisibilityWrapper< V extends Vertex< E >, E extends Edge< V > > implements ListenableGraph< V, E >
{

	private final ListenableGraph< V, E > graph;

	private final BooleanPropertyMap< V > visibility;

	private final VisibleVertices visibleVertices;

	public GraphVisibilityWrapper( final ListenableGraph< V, E > graph, final BooleanPropertyMap< V > visibility )
	{
		this.graph = graph;
		this.visibility = visibility;
		this.visibleVertices = new VisibleVertices();
	}

	@Override
	public V addVertex()
	{
		return graph.addVertex();
	}

	@Override
	public V addVertex( final V ref )
	{
		return graph.addVertex( ref );
	}

	@Override
	public E addEdge( final V source, final V target )
	{
		return graph.addEdge( source, target );
	}

	@Override
	public E addEdge( final V source, final V target, final E ref )
	{
		return graph.addEdge( source, target, ref );
	}

	@Override
	public E insertEdge( final V source, final int sourceOutIndex, final V target, final int targetInIndex )
	{
		return graph.insertEdge( source, sourceOutIndex, target, targetInIndex );
	}

	@Override
	public E insertEdge( final V source, final int sourceOutIndex, final V target, final int targetInIndex, final E ref )
	{
		return graph.insertEdge( source, sourceOutIndex, target, targetInIndex, ref );
	}

	@Override
	public void remove( final V vertex )
	{
		graph.remove( vertex );
	}

	@Override
	public void remove( final E edge )
	{
		graph.remove( edge );
	}

	@Override
	public void removeAllLinkedEdges( final V vertex )
	{
		graph.removeAllLinkedEdges( vertex );
	}

	@Override
	public E getEdge( final V source, final V target )
	{
		return graph.getEdge( source, target );
	}

	@Override
	public E getEdge( final V source, final V target, final E ref )
	{
		return graph.getEdge( source, target, ref );
	}

	@Override
	public Edges< E > getEdges( final V source, final V target )
	{
		return graph.getEdges( source, target );
	}

	@Override
	public Edges< E > getEdges( final V source, final V target, final V ref )
	{
		return graph.getEdges( source, target, ref );
	}

	@Override
	public V vertexRef()
	{
		return graph.vertexRef();
	}

	@Override
	public E edgeRef()
	{
		return graph.edgeRef();
	}

	@Override
	public void releaseRef( final V ref )
	{
		graph.releaseRef( ref );
	}

	@Override
	public void releaseRef( final E ref )
	{
		graph.releaseRef( ref );
	}

	@Override
	public RefCollection< V > vertices()
	{
		return visibleVertices;
	}

	@Override
	public RefCollection< E > edges()
	{
		/*
		 * Here we return all edges. We do not care whether we return edges that
		 * are connected non-visible vertices. In practice this will not impact
		 * the application, lest a view chooses to query vertices through edges
		 * instead of the opposite.
		 */
		return graph.edges();
	}

	@Override
	public boolean addGraphListener( final GraphListener< V, E > listener )
	{
		return graph.addGraphListener( listener );
	}

	@Override
	public boolean removeGraphListener( final GraphListener< V, E > listener )
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

	private class VisibleVertices implements RefCollection< V >
	{

		@Override
		public int size()
		{
			return visibility.nTrue();
		}

		@Override
		public Iterator< V > iterator()
		{
			return visibility.trueValueIterator();
		}

		@Override
		public boolean isEmpty()
		{
			return size() == 0;
		}

		@Override
		public V createRef()
		{
			return createRef();
		}

		@Override
		public void releaseRef( final V obj )
		{
			releaseRef( obj );
		}

		@Override
		public String toString()
		{
			if ( isEmpty() ) { return "[]"; }
			final StringBuffer sb = new StringBuffer();
			final Iterator< ? > it = iterator();
			sb.append( "[" + it.next().toString() );
			while ( it.hasNext() )
				sb.append( ", " + it.next().toString() );
			sb.append( "]" );
			return sb.toString();
		}

		/*
		 * The remaining RefCollection methods throw
		 * UnsupportedOperationException.
		 */

		/**
		 * This method is inapplicable to the vertex collection of a graph and
		 * throw an {@link UnsupportedOperationException}.
		 */
		@Override
		public boolean contains( final Object o )
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * This method is inapplicable to the vertex collection of a graph and
		 * throw an {@link UnsupportedOperationException}.
		 */
		@Override
		public Object[] toArray()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * This method is inapplicable to the vertex collection of a graph and
		 * throw an {@link UnsupportedOperationException}.
		 */
		@Override
		public < T > T[] toArray( final T[] a )
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * This method is inapplicable to the vertex collection of a graph and
		 * throw an {@link UnsupportedOperationException}.
		 */
		@Override
		public boolean add( final V e )
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * This method is inapplicable to the vertex collection of a graph and
		 * throw an {@link UnsupportedOperationException}.
		 */
		@Override
		public boolean remove( final Object o )
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * This method is inapplicable to the vertex collection of a graph and
		 * throw an {@link UnsupportedOperationException}.
		 */
		@Override
		public boolean containsAll( final Collection< ? > c )
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * This method is inapplicable to the vertex collection of a graph and
		 * throw an {@link UnsupportedOperationException}.
		 */
		@Override
		public boolean addAll( final Collection< ? extends V > c )
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * This method is inapplicable to the vertex collection of a graph and
		 * throw an {@link UnsupportedOperationException}.
		 */
		@Override
		public boolean removeAll( final Collection< ? > c )
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * This method is inapplicable to the vertex collection of a graph and
		 * throw an {@link UnsupportedOperationException}.
		 */
		@Override
		public boolean retainAll( final Collection< ? > c )
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * This method is inapplicable to the vertex collection of a graph and
		 * throw an {@link UnsupportedOperationException}.
		 */
		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}
	}
}

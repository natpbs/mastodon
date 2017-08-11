package org.mastodon.revised.model.mamut;

import java.util.Iterator;

import org.mastodon.pool.PoolCollectionWrapper;
import org.mastodon.pool.attributes.BooleanAttribute;

public class ModelGraphWrapper extends ModelGraph
{

	private final VisibleVertices visibleVertices;

	public ModelGraphWrapper()
	{
		this( 1000 );
	}

	public ModelGraphWrapper( final int initialCapacity )
	{
		super( initialCapacity );
		this.visibleVertices = new VisibleVertices( vertexPool );
	}

	@Override
	public PoolCollectionWrapper< Spot > vertices()
	{
		return visibleVertices;
	}

	private class VisibleVertices extends PoolCollectionWrapper< Spot >
	{

		private final BooleanAttribute< Spot > visibility;

		public VisibleVertices( final SpotPool pool )
		{
			super( pool );
			this.visibility = pool.visibility;
		}

		@Override
		public int size()
		{
			int n = 0;
			final Iterator< Spot > it = iterator();
			while ( it.hasNext() )
			{
				it.next();
				n++;
			}
			return n;
		}

		@Override
		public Iterator< Spot > iterator()
		{
			return new VisibilityIterator( visibility );
		}

		@Override
		public boolean isEmpty()
		{
			return size() == 0;
		}

	}

	private class VisibilityIterator implements Iterator< Spot >
	{
		private final BooleanAttribute< Spot > visibility;

		private final Iterator< Spot > it;

		private Spot next;

		private final Spot out = vertexRef();

		public VisibilityIterator( final BooleanAttribute< Spot > visibility )
		{
			this.visibility = visibility;
			this.it = ModelGraphWrapper.super.vertices().iterator();
			fetchNext();
		}

		private void fetchNext()
		{
			while ( it.hasNext() )
			{
				next = it.next();
				if ( visibility.get( next ) )
					return;
			}
			next = null;
		}

		@Override
		public boolean hasNext()
		{
			return next != null;
		}

		@Override
		public Spot next()
		{
			out.refTo( next );
			fetchNext();
			return out;
		}

	}
}

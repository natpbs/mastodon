package org.mastodon.revised.model.mamut;

import java.util.Iterator;

import org.mastodon.pool.PoolCollectionWrapper;
import org.mastodon.properties.BooleanPropertyMap;

public class ModelGraphWrapper extends ModelGraph
{

	private final BooleanPropertyMap< Spot > visibility;

	private final VisibleVertices visibleVertices;

	public ModelGraphWrapper()
	{
		this( 1000 );
	}

	public ModelGraphWrapper( final int initialCapacity )
	{
		super( initialCapacity );
		this.visibility = new BooleanPropertyMap<>( vertexPool, initialCapacity );
		this.visibleVertices = new VisibleVertices( vertexPool );
	}

	@Override
	public Spot addVertex( final Spot vertex )
	{
		// Mark all newly created spot as visible.
		final Spot spot = super.addVertex( vertex );
		visibility.set( spot, true );
		return spot;
	}

	@Override
	public PoolCollectionWrapper< Spot > vertices()
	{
		return visibleVertices;
	}

	private class VisibleVertices extends PoolCollectionWrapper< Spot >
	{

		public VisibleVertices( final SpotPool pool )
		{
			super( pool );
		}

		@Override
		public int size()
		{
			return visibility.nTrue();
		}

		@Override
		public Iterator< Spot > iterator()
		{
			return visibility.trueValueIterator();
		}

		@Override
		public boolean isEmpty()
		{
			return size() == 0;
		}
	}
}

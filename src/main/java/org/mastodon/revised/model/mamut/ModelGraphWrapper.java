package org.mastodon.revised.model.mamut;

import java.util.Collection;
import java.util.Iterator;

import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolCollectionWrapper;
import org.mastodon.pool.PoolObject;
import org.mastodon.revised.model.HasVisibility;
import org.mastodon.revised.model.feature.FeatureFilter;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.feature.FeatureProjection;

public class ModelGraphWrapper extends ModelGraph
{

	private final VisibleEntities< Spot > visibleVertices;

	private final VisibleEntities< Link > visibleEdges;

	public ModelGraphWrapper()
	{
		this( 1000 );
	}

	public ModelGraphWrapper( final int initialCapacity )
	{
		super( initialCapacity );
		this.visibleVertices = new VisibleEntities<>( vertexPool );
		this.visibleEdges = new VisibleEntities<>( edgePool );
	}

	public void filter( final Collection< FeatureFilter > filters, final FeatureModel< Spot, Link > featureModel )
	{
		final RefArrayList< Spot > toHide = new RefArrayList<>( vertexPool );
		final PoolCollectionWrapper< Spot > allVertices = super.vertices();

		pauseListeners();
		for ( final FeatureFilter filter : filters )
		{
			final FeatureProjection< Spot > feature = featureModel.getVertexProjection( filter.key );
			if ( null == feature )
				continue;

			for ( final Spot spot : allVertices )
			{
				if ( !feature.isSet( spot )
						|| ( filter.above && feature.value( spot ) < filter.threshold )
						|| ( !filter.above && feature.value( spot ) > filter.threshold ) )
				{
					toHide.add( spot );
				}
			}
		}

		for ( final Spot spot : allVertices )
			spot.setVisibility( true );
		for ( final Link link : super.edges() )
			link.setVisibility( true );

		for ( final Spot spot : toHide )
		{
			spot.setVisibility( false );
			for ( final Link link : spot.edges() )
				link.setVisibility( false );
		}

		resumeListeners();
		notifyGraphChanged();
	}

	@Override
	public PoolCollectionWrapper< Spot > vertices()
	{
		return visibleVertices;
	}

	@Override
	public PoolCollectionWrapper< Link > edges()
	{
		return visibleEdges;
	}

	public PoolCollectionWrapper< Spot > allVertices()
	{
		return super.vertices();
	}

	private class VisibleEntities< O extends PoolObject< O, ?, ? > & HasVisibility > extends PoolCollectionWrapper< O >
	{

		private final Pool< O, ? > pool;

		public VisibleEntities( final Pool< O, ? > pool )
		{
			super( pool );
			this.pool = pool;
		}

		@Override
		public Iterator< O > iterator()
		{
			return new VisibilityIterator<>( pool );
		}

		@Override
		public boolean isEmpty()
		{
			return size() == 0;
		}

	}

	private class VisibilityIterator< O extends PoolObject< O, ?, ? > & HasVisibility > implements Iterator< O >
	{

		private O next;

		private final O out;

		private final Iterator< O > it;

		public VisibilityIterator( final Pool< O, ? > pool )
		{
			this.next = pool.createRef();
			this.it = pool.iterator( next );
			this.out = pool.createRef();
			fetchNext();
		}

		private void fetchNext()
		{
			while ( it.hasNext() )
			{
				it.next();
				if ( next.getVisibility() )
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
		public O next()
		{
			out.refTo( next );
			fetchNext();
			return out;
		}

	}
}

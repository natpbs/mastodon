package org.mastodon.revised.model.mamut;

import java.util.Collection;
import java.util.Iterator;

import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.pool.PoolCollectionWrapper;
import org.mastodon.properties.BooleanPropertyMap;
import org.mastodon.revised.model.feature.FeatureFilter;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.feature.FeatureProjection;

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

	public BooleanPropertyMap< Spot > getVisibility()
	{
		return visibility;
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
			visibility.set( spot, true );
		for ( final Spot spot : toHide )
			visibility.set( spot, false );

		resumeListeners();
		notifyGraphChanged();
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

	public PoolCollectionWrapper< Spot > allVertices()
	{
		return super.vertices();
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

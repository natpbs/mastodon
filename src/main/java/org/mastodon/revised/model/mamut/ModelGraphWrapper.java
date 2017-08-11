package org.mastodon.revised.model.mamut;

import java.util.Collection;
import java.util.Iterator;

import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolCollectionWrapper;
import org.mastodon.pool.PoolObject;
import org.mastodon.properties.BooleanPropertyMap;
import org.mastodon.revised.model.feature.FeatureFilter;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.feature.FeatureProjection;

public class ModelGraphWrapper extends ModelGraph
{

	private final BooleanPropertyMap< Spot > vertexVisibility;

	private final VisibleEntities< Spot > visibleVertices;

	private final BooleanPropertyMap< Link > edgeVisibility;

	private final VisibleEntities< Link > visibleEdges;

	public ModelGraphWrapper()
	{
		this( 1000 );
	}

	public ModelGraphWrapper( final int initialCapacity )
	{
		super( initialCapacity );
		this.vertexVisibility = new BooleanPropertyMap<>( vertexPool, initialCapacity );
		this.edgeVisibility = new BooleanPropertyMap<>( edgePool, initialCapacity );
		this.visibleVertices = new VisibleEntities<>( vertexPool, vertexVisibility );
		this.visibleEdges = new VisibleEntities<>( edgePool, edgeVisibility );
	}

	public BooleanPropertyMap< Spot > getVisibility()
	{
		return vertexVisibility;
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
			vertexVisibility.set( spot, true );
		for ( final Link link : super.edges() )
			edgeVisibility.set( link, true );

		for ( final Spot spot : toHide )
		{
			vertexVisibility.set( spot, false );
			for ( final Link link : spot.edges() )
				edgeVisibility.set( link, false );
		}

		resumeListeners();
		notifyGraphChanged();
	}

	@Override
	public Spot addVertex( final Spot vertex )
	{
		// Mark all newly created spot as visible.
		final Spot spot = super.addVertex( vertex );
		vertexVisibility.set( spot, true );
		return spot;
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

	private class VisibleEntities< O extends PoolObject< O, ?, ? > > extends PoolCollectionWrapper< O >
	{

		private final BooleanPropertyMap< O > visibility;

		public VisibleEntities( final Pool< O, ? > pool, final BooleanPropertyMap< O > visibility )
		{
			super( pool );
			this.visibility = visibility;
		}

		@Override
		public int size()
		{
			return visibility.nTrue();
		}

		@Override
		public Iterator< O > iterator()
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

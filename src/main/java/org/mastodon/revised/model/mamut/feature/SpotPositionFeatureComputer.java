package org.mastodon.revised.model.mamut.feature;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mastodon.collection.RefCollection;
import org.mastodon.features.Feature;
import org.mastodon.features.FeatureCleanup;
import org.mastodon.features.FeatureRegistry.DuplicateKeyException;
import org.mastodon.features.FeatureValue;
import org.mastodon.features.Features;
import org.mastodon.features.UndoFeatureMap;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.Spot;
import org.scijava.plugin.Plugin;

import net.imglib2.RealLocalizable;

@Plugin( type = SpotFeatureComputer.class, name = "Spot position" )
public class SpotPositionFeatureComputer
		extends SpotFeatureComputer< Feature< Map< Spot, RealLocalizable >, Spot, FeatureValue< RealLocalizable > >, Model >
{

	private static final String NAME = "Spot position";

	private static final IdentityFeature< Spot, RealLocalizable > FEATURE = new IdentityFeature<>( NAME );

	private static final Map< String, FeatureProjection< Spot > > PROJECTIONS;
	static
	{
		final HashMap< String, FeatureProjection< Spot > > map = new HashMap<>();
		for ( int d = 0; d < 3; d++ )
		{
			final String pname = "Spot " + ( char ) ( 'X' + d ) + " position";
			final SpotPositionProjection projection = new SpotPositionProjection( d );
			map.put( pname, projection );
		}
		PROJECTIONS = Collections.unmodifiableMap( map );
	}

	@Override
	public Set< String > getDependencies()
	{
		return Collections.emptySet();
	}

	@Override
	public Map< String, FeatureProjection< Spot > > getProjections()
	{
		return PROJECTIONS;
	}


	@Override
	public void compute( final Model model )
	{}

	@Override
	public Feature< Map< Spot, RealLocalizable >, Spot, FeatureValue< RealLocalizable > > getFeature()
	{
		return FEATURE;
	}

	private final static class SpotPositionProjection implements FeatureProjection< Spot >
	{

		private final int d;

		public SpotPositionProjection( final int d )
		{
			this.d = d;
		}

		@Override
		public boolean isSet( final Spot obj )
		{
			return true;
		}

		@Override
		public double value( final Spot obj )
		{
			return obj.getDoublePosition( d );
		}
	}

	private final static class IdentityFeature< O extends T, T > extends Feature< Map< O, T >, O, FeatureValue< T > >
	{

		protected IdentityFeature( final String key ) throws DuplicateKeyException
		{
			super( key );
		}

		@Override
		protected Map< O, T > createFeatureMap( final RefCollection< O > pool )
		{
			return Collections.emptyMap();
		}

		@Override
		public IdentityFeatureValue< T > createFeatureValue( final O object, final Features< O > features )
		{
			return new IdentityFeatureValue< T >( object );
		}

		@Override
		protected FeatureCleanup< O > createFeatureCleanup( final Map< O, T > featureMap )
		{
			return new FeatureCleanup< O >()
			{
				@Override
				public void delete( final O object )
				{}
			};
		}

		@Override
		public UndoFeatureMap< O > createUndoFeatureMap( final Map< O, T > featureMap )
		{
			return new UndoFeatureMap< O >()
			{
				@Override
				public void store( final int undoId, final O object )
				{}

				@Override
				public void retrieve( final int undoId, final O object )
				{}

				@Override
				public void swap( final int undoId, final O object )
				{}

				@Override
				public void clear( final int undoId )
				{}
			};
		}
	}

	private final static class IdentityFeatureValue<O> implements FeatureValue< O >
	{

		private final O object;

		public IdentityFeatureValue(final O object)
		{
			this.object = object;
		}

		@Override
		public void set( final O value )
		{}

		@Override
		public void remove()
		{}

		@Override
		public O get()
		{
			return object;
		}

		@Override
		public boolean isSet()
		{
			return true;
		}
	}
}

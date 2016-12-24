package org.mastodon.revised.model.mamut.feature;

import org.mastodon.features.DoubleFeature;
import org.mastodon.features.Feature;
import org.mastodon.features.IntFeature;
import org.mastodon.graph.EdgeWithFeatures;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.VertexWithFeatures;
import org.mastodon.revised.model.mamut.FeatureModel;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.model.mamut.feature.ScalarFeatureProperties.FeatureProjector;

public class MamutFeatureProjector implements FeatureProjector< Spot, Link >
{

	private final Model model;

	public MamutFeatureProjector( final Model model )
	{
		this.model = model;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public ScalarFeatureProperties< Spot > createVertexFeatureProperties( final String featureKey )
	{
		final FeatureModel fm = model.featureModel();
		if ( !fm.getFeatureKeys().contains( featureKey ) )
			return new EmptyFeatureProperties< Spot >();

		final Feature< ?, ?, ? > feature = fm.getFeature( featureKey );
		if ( feature instanceof DoubleFeature ) { return new DoubleFeatureVertexProperties< Spot >( ( DoubleFeature< Spot > ) feature ); }
		if ( feature instanceof IntFeature ) { return new IntFeatureVertexProperties< Spot >( ( IntFeature< Spot > ) feature ); }
		throw new UnsupportedOperationException( "Do not know how to project feature " + feature + " onto double yet." );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public ScalarFeatureProperties< Link > createEdgeFeatureProperties( final String featureKey )
	{
		final FeatureModel fm = model.featureModel();
		if ( !fm.getFeatureKeys().contains( featureKey ) )
			return new EmptyFeatureProperties< Link >();

		final Feature< ?, ?, ? > feature = fm.getFeature( featureKey );
		if ( feature instanceof DoubleFeature ) { return new DoubleFeatureEdgeProperties< Link >( ( DoubleFeature< Link > ) feature ); }
		if ( feature instanceof IntFeature ) { return new IntFeatureEdgeProperties< Link >( ( IntFeature< Link > ) feature ); }
		throw new UnsupportedOperationException( "Do not know how to project feature " + feature + " onto double yet." );
	}

	@Override
	public ScalarFeatureProperties< ReadOnlyGraph< Spot, Link > > createGraphFeatureProperties( final String featureKey )
	{
		throw new UnsupportedOperationException( "Do not know how to project feature " + featureKey + " onto double yet." );
	}

	@Override
	public ScalarFeatureProperties< Integer > createTimepointFeatureProperties( final String featureKey )
	{
		throw new UnsupportedOperationException( "Do not know how to project feature " + featureKey + " onto double yet." );
	}

	public static final class EmptyFeatureProperties< K > implements ScalarFeatureProperties< K >
	{

		@Override public boolean isSet( final K obj ) { return false; }

		@Override public double value( final K obj ) { return Double.NaN; }
	}

	private static class DoubleFeatureVertexProperties< V extends VertexWithFeatures< V, ? > > implements ScalarFeatureProperties< V >
	{

		private final DoubleFeature< V > feature;

		public DoubleFeatureVertexProperties( final DoubleFeature< V > feature )
		{
			this.feature = feature;
		}

		@Override
		public boolean isSet( final V v )
		{
			return v.feature( feature ).isSet();
		}

		@Override
		public double value( final V v )
		{
			return v.feature( feature ).getDouble();
		}
	}

	private static class IntFeatureVertexProperties< V extends VertexWithFeatures< V, ? > > implements ScalarFeatureProperties< V >
	{

		private final IntFeature< V > feature;

		public IntFeatureVertexProperties( final IntFeature< V > feature )
		{
			this.feature = feature;
		}

		@Override
		public boolean isSet( final V v )
		{
			return v.feature( feature ).isSet();
		}

		@Override
		public double value( final V v )
		{
			return v.feature( feature ).getInt();
		}
	}

	private static class DoubleFeatureEdgeProperties< E extends EdgeWithFeatures< E, ? > > implements ScalarFeatureProperties< E >
	{

		private final DoubleFeature< E > feature;

		public DoubleFeatureEdgeProperties( final DoubleFeature< E > feature )
		{
			this.feature = feature;
		}

		@Override
		public boolean isSet( final E e )
		{
			return e.feature( feature ).isSet();
		}

		@Override
		public double value( final E e )
		{
			return e.feature( feature ).getDouble();
		}
	}

	private static class IntFeatureEdgeProperties< E extends EdgeWithFeatures< E, ? > > implements ScalarFeatureProperties< E >
	{

		private final IntFeature< E > feature;

		public IntFeatureEdgeProperties( final IntFeature< E > feature )
		{
			this.feature = feature;
		}

		@Override
		public boolean isSet( final E e )
		{
			return e.feature( feature ).isSet();
		}

		@Override
		public double value( final E e )
		{
			return e.feature( feature ).getInt();
		}
	}


}

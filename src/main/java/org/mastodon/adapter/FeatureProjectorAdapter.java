package org.mastodon.adapter;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.revised.model.mamut.feature.ScalarFeatureProperties;
import org.mastodon.revised.model.mamut.feature.ScalarFeatureProperties.FeatureProjector;

public class FeatureProjectorAdapter< V extends Vertex< E >, E extends Edge< V >, WV extends Vertex< WE >, WE extends Edge< WV > >
		implements FeatureProjector< WV, WE >
{


	private final FeatureProjector< V, E > featureProjector;

	private final RefBimap< V, WV > vertexMap;

	private final RefBimap< E, WE > edgeMap;

	private final ReadOnlyGraph< V, E > graph;

	public FeatureProjectorAdapter(
			final FeatureProjector< V, E > featureProjector,
			final RefBimap< V, WV > vertexMap,
			final RefBimap< E, WE > edgeMap,
			final ReadOnlyGraph< V, E > graph )
	{
		this.featureProjector = featureProjector;
		this.vertexMap = vertexMap;
		this.edgeMap = edgeMap;
		this.graph = graph;
	}

	@Override
	public ScalarFeatureProperties< WV > createVertexFeatureProperties( final String featureKey )
	{
		return new ScalarFeaturePropertiesAdapter< V, WV >( featureProjector.createVertexFeatureProperties( featureKey ), vertexMap );
	}

	@Override
	public ScalarFeatureProperties< WE > createEdgeFeatureProperties( final String featureKey )
	{
		return new ScalarFeaturePropertiesAdapter< E, WE >( featureProjector.createEdgeFeatureProperties( featureKey ), edgeMap );
	}

	@Override
	public ScalarFeatureProperties< ReadOnlyGraph< WV, WE > > createGraphFeatureProperties( final String featureKey )
	{
		final ScalarFeatureProperties< ReadOnlyGraph< V, E > > properties = featureProjector.createGraphFeatureProperties( featureKey );
		return new ScalarFeatureProperties< ReadOnlyGraph< WV, WE > >()
		{

			@Override
			public boolean isSet( final ReadOnlyGraph< WV, WE > obj )
			{
				return properties.isSet( graph );
			}

			@Override
			public double value( final ReadOnlyGraph< WV, WE > obj )
			{
				return properties.value( graph );
			}
		};
	}

	@Override
	public ScalarFeatureProperties< Integer > createTimepointFeatureProperties( final String featureKey )
	{
		return featureProjector.createTimepointFeatureProperties( featureKey );
	}

	private static final class ScalarFeaturePropertiesAdapter< V, WV > implements ScalarFeatureProperties< WV >
	{

		private final ScalarFeatureProperties< V > featureProperties;

		private final RefBimap< V, WV > map;

		public ScalarFeaturePropertiesAdapter(
				final ScalarFeatureProperties< V > featureProperties,
				final RefBimap< V, WV > map )
		{
			this.featureProperties = featureProperties;
			this.map = map;
		}

		@Override
		public boolean isSet( final WV obj )
		{
			return featureProperties.isSet( map.getLeft( obj ) );
		}

		@Override
		public double value( final WV obj )
		{
			return featureProperties.value( map.getLeft( obj ) );
		}

	}
}

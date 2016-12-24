package org.mastodon.revised.model.mamut.feature;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

public interface ScalarFeatureProperties< K >
{
	public boolean isSet( K obj );

	public double value( K obj );

	public static interface FeatureProjector< V extends Vertex< E >, E extends Edge< V > >
	{
		public ScalarFeatureProperties< V > createVertexFeatureProperties( String featureKey );

		public ScalarFeatureProperties< E > createEdgeFeatureProperties( String featureKey );

		public ScalarFeatureProperties< ReadOnlyGraph< V, E > > createGraphFeatureProperties( String featureKey );

		public ScalarFeatureProperties< Integer > createTimepointFeatureProperties( String featureKey );

	}
}

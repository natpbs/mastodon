package org.mastodon.revised.model.feature;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mastodon.features.Feature;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Vertex;

public class DefaultFeatureModel< V extends Vertex< E >, E extends Edge< V > > implements FeatureModel< V, E >
{

	private final Map< String, FeatureTarget > featureTargets;

	private final Map< String, Feature< ?, ?, ? > > feature;

	private final Map< String, FeatureProjection< V > > vertexProjections;

	private final Map< String, FeatureProjection< E > > edgeProjections;

	private final Map< String, FeatureTarget > projectionTargets;

	private final Set< String > vertexFeatureKeys;

	private final Set< String > edgeFeatureKeys;

	public DefaultFeatureModel()
	{
		featureTargets = new HashMap<>();
		feature = new HashMap<>();
		vertexProjections = new HashMap<>();
		edgeProjections = new HashMap<>();
		vertexFeatureKeys = new HashSet<>();
		edgeFeatureKeys = new HashSet<>();
		projectionTargets = new HashMap<>();
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public void declareFeature( final FeatureComputer< ?, ?, ? > fc )
	{
		final FeatureTarget target = fc.getTarget();
		final String featureKey = fc.getFeature().getKey();
		featureTargets.put( featureKey, target );
		feature.put( featureKey, fc.getFeature() );
		switch ( target )
		{
		case EDGE:
			edgeProjections.putAll( ( Map< ? extends String, ? extends FeatureProjection< E > > ) fc.getProjections() );
			edgeFeatureKeys.add( featureKey );
			break;
		case GRAPH:
			break;
		case TIMEPOINT:
			break;
		case VERTEX:
			vertexProjections.putAll( ( Map< ? extends String, ? extends FeatureProjection< V > > ) fc.getProjections() );
			vertexFeatureKeys.add( featureKey );
			break;
		default:
			break;
		}
		for ( final String projectionKey : fc.getProjections().keySet() )
			projectionTargets.put( projectionKey, target );
	}

	@Override
	public void clear()
	{
		featureTargets.clear();
		feature.clear();
		vertexFeatureKeys.clear();
		edgeFeatureKeys.clear();
		vertexProjections.clear();
		edgeProjections.clear();
		projectionTargets.clear();
	}

	@Override
	public FeatureProjection< V > getVertexProjection( final String projectionKey )
	{
		return vertexProjections.get( projectionKey );
	}

	@Override
	public Feature< ?, ?, ? > getFeature( final String featureKey )
	{
		return feature.get( featureKey );
	}

	@Override
	public FeatureProjection< E > getEdgeProjection( final String projectionKey )
	{
		return edgeProjections.get( projectionKey );
	}

	@Override
	public FeatureTarget getFeatureTarget( final String featureKey )
	{
		return featureTargets.get( featureKey );
	}

	@Override
	public FeatureTarget getProjectionTarget( final String projectionKey )
	{
		return projectionTargets.get( projectionKey );
	}

	@Override
	public Set< String > getVertexFeatureKeys()
	{
		return Collections.unmodifiableSet( vertexFeatureKeys );
	}

	@Override
	public Set< String > getVertexProjectionKeys()
	{
		return Collections.unmodifiableSet( vertexProjections.keySet() );
	}

	@Override
	public Set< String > getEdgeFeatureKeys()
	{
		return Collections.unmodifiableSet( edgeFeatureKeys );
	}

	@Override
	public Set< String > getEdgeProjectionKeys()
	{
		return Collections.unmodifiableSet( edgeProjections.keySet() );
	}
}

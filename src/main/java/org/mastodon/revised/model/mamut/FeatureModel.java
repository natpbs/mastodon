package org.mastodon.revised.model.mamut;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mastodon.features.Feature;

public class FeatureModel
{

	public enum FeatureTarget
	{
		VERTEX, EDGE, GRAPH, TIMEPOINT;
	}

	private final Map< Feature< ?, ?, ? >, FeatureTarget > featureTargets;

	private final Map< String, Feature< ?, ?, ? > > featureKeys;

	public FeatureModel()
	{
		featureTargets = new HashMap<>();
		featureKeys = new HashMap<>();
	}

	public void declareFeature( final Feature< ?, ?, ? > feature, final FeatureTarget target )
	{
		featureTargets.put( feature, target );
		featureKeys.put( feature.getKey(), feature );
	}

	public void clearFeatures()
	{
		featureTargets.clear();
		featureKeys.clear();
	}

	public Feature< ?, ?, ? > getFeature( final String key )
	{
		return featureKeys.get( key );
	}

	public FeatureTarget getFeatureTarget( final Feature< ?, ?, ? > feature )
	{
		return featureTargets.get( feature );
	}

	public Set< String > getFeatureKeys()
	{
		return Collections.unmodifiableSet( featureKeys.keySet() );
	}
}

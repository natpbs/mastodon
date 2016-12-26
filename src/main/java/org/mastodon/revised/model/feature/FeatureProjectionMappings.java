package org.mastodon.revised.model.feature;

import java.util.HashMap;
import java.util.Map;

import org.mastodon.features.Feature;

/**
 * Relates features to their projection.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class FeatureProjectionMappings
{

	private final Map< String, Feature< ?, ?, ? > > featureMap;

	private final Map< String, Feature< ?, ?, ? > > projectionMap;

	private final Map< String, Integer > componentMap;

	public FeatureProjectionMappings()
	{
		this.featureMap = new HashMap<>();
		this.projectionMap = new HashMap<>();
		this.componentMap = new HashMap<>();
	}

	/**
	 * Registers the specified feature as a scalar, real feature.
	 *
	 * @param feature
	 *            the feature to register.
	 */
	public void register( final Feature< ?, ?, ? > feature )
	{
		register( feature, new String[] { feature.getKey() } );
	}

	/**
	 * Registers the feature, specifying it can be projected onto the components
	 * with the specified keys.
	 *
	 * @param feature
	 *            the feature to register.
	 * @param projectionKeys
	 *            the keys of the feature scalar components. Each key must be
	 *            unique.
	 */
	public void register( final Feature< ?, ?, ? > feature, final String... projectionKeys )
	{
		featureMap.put( feature.getKey(), feature );
		for ( int i = 0; i < projectionKeys.length; i++ )
		{
			final String projectionKey = projectionKeys[ i ];
			projectionMap.put( projectionKey, feature );
			componentMap.put( projectionKey, Integer.valueOf( i ) );
		}
	}

	public Feature< ?, ?, ? > getFeatureForProjection( final String projectionKey )
	{
		return projectionMap.get( projectionKey );
	}

	public int getProjectionComponent( final String projectionKey )
	{
		final Integer c = componentMap.get( projectionKey );
		return null == c ? -1 : c.intValue();
	}

}

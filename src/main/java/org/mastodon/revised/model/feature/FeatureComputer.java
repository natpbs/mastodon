package org.mastodon.revised.model.feature;

import java.util.Map;
import java.util.Set;

import org.mastodon.features.Feature;
import org.mastodon.revised.model.AbstractModel;
import org.scijava.plugin.SciJavaPlugin;

/**
 * Concrete implementations must be stateless, without side effects.
 *
 * @param <K>
 * @param <O>
 * @param <AM>
 */
public interface FeatureComputer< K extends Feature< ?, O, ? >, O, AM extends AbstractModel< ?, ?, ? > > extends SciJavaPlugin
{

	/**
	 * Returns the set of dependencies of this feature computer.
	 * <p>
	 * Dependencies are expressed as the set of feature computer names.
	 *
	 * @return the set of dependencies.
	 */
	public Set< String > getDependencies();

	public void compute( final AM model );

	public K getFeature();

	public Map< String, FeatureProjection< O > > getProjections();

	public FeatureTarget getTarget();

}
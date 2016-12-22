package org.mastodon.revised.model.mamut.feature;

import java.util.Set;

import org.mastodon.revised.model.mamut.Model;
import org.scijava.plugin.SciJavaPlugin;

/**
 * Concrete implementations must be stateless, without side effects.
 */
public interface FeatureComputer extends SciJavaPlugin
{

	/**
	 * Returns the set of dependencies of this feature computer.
	 * <p>
	 * Dependencies are expressed as the set of feature computer names.
	 * 
	 * @return the set of dependencies.
	 */
	public Set< String > getDependencies();

	public default void compute( final Model model )
	{};


}
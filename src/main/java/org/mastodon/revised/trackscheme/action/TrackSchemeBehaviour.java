package org.mastodon.revised.trackscheme.action;

import org.scijava.plugin.SciJavaPlugin;
import org.scijava.ui.behaviour.Behaviour;

public interface TrackSchemeBehaviour extends SciJavaPlugin, Behaviour
{

	/**
	 * Performs initialization tasks for this action. This method is called
	 * after the instance has been injected with context and services.
	 */
	public void initialize();
}

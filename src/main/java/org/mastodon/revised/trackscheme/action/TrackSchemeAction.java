package org.mastodon.revised.trackscheme.action;

import org.scijava.plugin.SciJavaPlugin;

public interface TrackSchemeAction extends SciJavaPlugin, Runnable
{

	/**
	 * Performs initialization tasks for this action. This method is called
	 * after the instance has been injected with context and services.
	 */
	public void initialize();

}

package org.mastodon.revised.trackscheme.action;

import org.scijava.plugin.Plugin;

@Plugin( type = TrackSchemeAction.class, enabled = false, name = "DISABLED_TEST", label = "Disabled action test" )
public class TrackSchemeDisabledActionTest implements TrackSchemeAction
{

	public TrackSchemeDisabledActionTest()
	{
		System.out.println( "Just instantiated " + toString() );
	}

}

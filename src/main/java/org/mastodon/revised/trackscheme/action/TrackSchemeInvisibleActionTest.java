package org.mastodon.revised.trackscheme.action;

import org.scijava.plugin.Plugin;

@Plugin( type = TrackSchemeAction.class, visible = false, name = "INVISIBLE_TEST", label = "Invisible action test" )
public class TrackSchemeInvisibleActionTest implements TrackSchemeAction
{

	public TrackSchemeInvisibleActionTest()
	{
		System.out.println( "Just instantiated " + toString() );
	}

}

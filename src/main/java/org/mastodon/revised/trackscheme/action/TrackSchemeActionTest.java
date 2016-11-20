package org.mastodon.revised.trackscheme.action;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.behaviour.ClickBehaviour;

@Plugin( type = TrackSchemeAction.class, name = "ts test", label = "Standard action test" )
public class TrackSchemeActionTest implements TrackSchemeAction, ClickBehaviour
{

	@Parameter
	private TrackSchemeService service;

	public TrackSchemeActionTest()
	{
		System.out.println( "Just instantiated " + toString() );
	}

	@Override
	public void click( final int x, final int y )
	{
		System.out.println( "Hello world from " + toString() + "! Service is:  " + service + ", clicked on " + x + ", " + y );
	}
}
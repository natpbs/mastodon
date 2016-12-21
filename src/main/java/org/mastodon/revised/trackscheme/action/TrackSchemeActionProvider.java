package org.mastodon.revised.trackscheme.action;

import org.scijava.Context;

public class TrackSchemeActionProvider extends AbstractProvider< TrackSchemeAction >
{
	public TrackSchemeActionProvider()
	{
		super( TrackSchemeAction.class );
	}

	public static void main( final String[] args )
	{
		final Context context = new Context();
		final TrackSchemeActionProvider provider = new TrackSchemeActionProvider();
		context.inject( provider );
		System.out.println( provider.echo() );
		context.dispose();
	}

}
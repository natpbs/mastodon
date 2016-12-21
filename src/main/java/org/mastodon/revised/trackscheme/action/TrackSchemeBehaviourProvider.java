package org.mastodon.revised.trackscheme.action;

import org.scijava.Context;

public class TrackSchemeBehaviourProvider extends AbstractProvider< TrackSchemeBehaviour >
{
	public TrackSchemeBehaviourProvider()
	{
		super( TrackSchemeBehaviour.class );
	}

	public static void main( final String[] args )
	{
		final Context context = new Context();
		final TrackSchemeBehaviourProvider provider = new TrackSchemeBehaviourProvider();
		context.inject( provider );
		System.out.println( provider.echo() );
		context.dispose();
	}

}
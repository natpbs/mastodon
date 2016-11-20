package org.mastodon.revised.trackscheme.action;

import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeSelection;
import org.scijava.Context;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

@Plugin( type = Service.class )
public class DefaultTrackSchemeService extends AbstractService implements TrackSchemeService
{

	public DefaultTrackSchemeService()
	{
		System.out.println( "Creating the service " + toString() ); // DEBUG
	}

	@Override
	public TrackSchemeGraph< ?, ? > getTrackSchemeGraph()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TrackSchemeSelection getTrackSchemeSelection()
	{
		// TODO Auto-generated method stub
		System.out.println( toString() + " -> Heyyyyyyyyyy..." );
		return null;
	}


	public static void main( final String[] args )
	{
		final Context context = new Context();
		final TrackSchemeService service = context.getService( TrackSchemeService.class );
		System.out.println( service ); // DEBUG
		System.out.println( service.getTrackSchemeSelection() ); // DEBUG
	}
}

package org.mastodon.revised.trackscheme.action;

import java.util.HashMap;
import java.util.Map;

import org.mastodon.revised.trackscheme.TrackSchemeFocus;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeHighlight;
import org.mastodon.revised.trackscheme.TrackSchemeNavigation;
import org.mastodon.revised.trackscheme.TrackSchemeSelection;
import org.mastodon.revised.trackscheme.display.TrackSchemeFrame;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

@Plugin( type = Service.class )
public class DefaultTrackSchemeService extends AbstractService implements TrackSchemeService
{

	private final Map< TrackSchemeFrame, TrackSchemeGraph< ?, ? > > graphs;

	private final Map< TrackSchemeFrame, TrackSchemeSelection > selections;

	private final Map< TrackSchemeFrame, TrackSchemeHighlight > highlights;

	private final Map< TrackSchemeFrame, TrackSchemeFocus > focuses;

	private final Map< TrackSchemeFrame, TrackSchemeNavigation > navigations;

	private final Map< Object, TrackSchemeFrame > actions;

	public DefaultTrackSchemeService()
	{
		System.out.println( "Creating the service " + toString() ); // DEBUG
		this.graphs = new HashMap<>();
		this.selections = new HashMap<>();
		this.highlights = new HashMap<>();
		this.focuses = new HashMap<>();
		this.navigations = new HashMap<>();
		this.actions = new HashMap<>();
	}

	@Override
	public void register(
			final TrackSchemeFrame frame,
			final TrackSchemeGraph< ?, ? > graph,
			final TrackSchemeSelection selection,
			final TrackSchemeHighlight highlight,
			final TrackSchemeFocus focus,
			final TrackSchemeNavigation navigation)
	{
		graphs.put( frame, graph );
		selections.put( frame, selection );
		highlights.put( frame, highlight );
		focuses.put( frame, focus );
		navigations.put( frame, navigation );
	}

	@Override
	public void put( final Object action, final TrackSchemeFrame frame )
	{
		actions.put( action, frame );
	}

	@Override
	public TrackSchemeFrame getFrame( final Object action )
	{
		final TrackSchemeFrame frame = actions.get( action );
		if ( null == frame )
			throw new IllegalArgumentException( "The action " + action + " is not registered with a known TrackScheme instance" );
		return frame;
	}

	@Override
	public TrackSchemeGraph< ?, ? > getGraph( final Object action )
	{
		return graphs.get( getFrame( action ) );
	}

	@Override
	public TrackSchemeSelection getSelection( final Object action )
	{
		return selections.get( getFrame( action ) );
	}

	@Override
	public TrackSchemeHighlight getHighlight( final Object action )
	{
		return highlights.get( getFrame( action ) );
	}

	@Override
	public TrackSchemeFocus getFocus( final Object action )
	{
		return focuses.get( getFrame( action ) );
	}

	@Override
	public TrackSchemeNavigation getNavigation( final Object action )
	{
		return navigations.get( getFrame( action ) );
	}

}

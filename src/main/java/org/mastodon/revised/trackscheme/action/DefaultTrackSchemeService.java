package org.mastodon.revised.trackscheme.action;

import java.util.HashMap;
import java.util.Map;

import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.trackscheme.display.TrackSchemeFrame;
import org.mastodon.revised.ui.selection.FocusModel;
import org.mastodon.revised.ui.selection.HighlightModel;
import org.mastodon.revised.ui.selection.NavigationHandler;
import org.mastodon.revised.ui.selection.Selection;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

@Plugin( type = Service.class )
public class DefaultTrackSchemeService extends AbstractService implements TrackSchemeService
{

	private final Map< TrackSchemeFrame, TrackSchemeGraph< ?, ? > > graphs;

	private final Map< TrackSchemeFrame, Selection< TrackSchemeVertex, TrackSchemeEdge > > selections;

	private final Map< TrackSchemeFrame, HighlightModel< TrackSchemeVertex, TrackSchemeEdge > > highlights;

	private final Map< TrackSchemeFrame, FocusModel< TrackSchemeVertex, TrackSchemeEdge > > focuses;

	private final Map< TrackSchemeFrame, NavigationHandler< TrackSchemeVertex, TrackSchemeEdge > > navigations;

	private final Map< Object, TrackSchemeFrame > actions;

	public DefaultTrackSchemeService()
	{
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
			final Selection< TrackSchemeVertex, TrackSchemeEdge > selection,
			final HighlightModel< TrackSchemeVertex, TrackSchemeEdge > highlight,
			final FocusModel< TrackSchemeVertex, TrackSchemeEdge > focus,
			final NavigationHandler< TrackSchemeVertex, TrackSchemeEdge > navigation )
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
	public Selection< TrackSchemeVertex, TrackSchemeEdge > getSelection( final Object action )
	{
		return selections.get( getFrame( action ) );
	}

	@Override
	public HighlightModel< TrackSchemeVertex, TrackSchemeEdge > getHighlight( final Object action )
	{
		return highlights.get( getFrame( action ) );
	}

	@Override
	public FocusModel< TrackSchemeVertex, TrackSchemeEdge > getFocus( final Object action )
	{
		return focuses.get( getFrame( action ) );
	}

	@Override
	public NavigationHandler< TrackSchemeVertex, TrackSchemeEdge > getNavigation( final Object action )
	{
		return navigations.get( getFrame( action ) );
	}

}

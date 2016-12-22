package org.mastodon.revised.trackscheme.action;

import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.trackscheme.display.TrackSchemeFrame;
import org.mastodon.revised.ui.selection.FocusModel;
import org.mastodon.revised.ui.selection.HighlightModel;
import org.mastodon.revised.ui.selection.NavigationHandler;
import org.mastodon.revised.ui.selection.Selection;
import org.scijava.service.SciJavaService;


public interface TrackSchemeService extends SciJavaService
{

	/**
	 * Registers the specified TrackScheme instance into this service.
	 *
	 * @param frame
	 * @param graph
	 * @param selection
	 * @param highlight
	 * @param focus
	 * @param navigation
	 */
	public void register(
			TrackSchemeFrame frame,
			TrackSchemeGraph< ?, ? > graph,
			Selection< TrackSchemeVertex, TrackSchemeEdge > selection,
			HighlightModel< TrackSchemeVertex, TrackSchemeEdge > highlight,
			FocusModel< TrackSchemeVertex, TrackSchemeEdge > focus,
			NavigationHandler< TrackSchemeVertex, TrackSchemeEdge > navigation );

	/**
	 * Associates the specified action to the specified TrackScheme instance.
	 *
	 * @param action
	 * @param frame
	 */
	public void put( Object action, TrackSchemeFrame frame );

	public TrackSchemeFrame getFrame( Object action );

	public TrackSchemeGraph< ?, ? > getGraph( Object action );

	public Selection< TrackSchemeVertex, TrackSchemeEdge > getSelection( Object action );

	public HighlightModel< TrackSchemeVertex, TrackSchemeEdge > getHighlight( Object action );

	public FocusModel< TrackSchemeVertex, TrackSchemeEdge > getFocus( Object action );

	public NavigationHandler< TrackSchemeVertex, TrackSchemeEdge > getNavigation( Object action );

}

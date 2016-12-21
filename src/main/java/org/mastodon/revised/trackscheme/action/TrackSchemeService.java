package org.mastodon.revised.trackscheme.action;

import org.mastodon.revised.trackscheme.TrackSchemeFocus;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeHighlight;
import org.mastodon.revised.trackscheme.TrackSchemeNavigation;
import org.mastodon.revised.trackscheme.TrackSchemeSelection;
import org.mastodon.revised.trackscheme.display.TrackSchemeFrame;
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
			TrackSchemeSelection selection,
			TrackSchemeHighlight highlight,
			TrackSchemeFocus focus,
			TrackSchemeNavigation navigation );

	/**
	 * Associates the specified action to the specified TrackScheme instance.
	 *
	 * @param action
	 * @param frame
	 */
	public void put( Object action, TrackSchemeFrame frame );

	public TrackSchemeFrame getFrame( Object action );

	public TrackSchemeGraph< ?, ? > getGraph( Object action );

	public TrackSchemeSelection getSelection( Object action );

	public TrackSchemeHighlight getHighlight( Object action );

	public TrackSchemeFocus getFocus( Object action );

	public TrackSchemeNavigation getNavigation( Object action );

}

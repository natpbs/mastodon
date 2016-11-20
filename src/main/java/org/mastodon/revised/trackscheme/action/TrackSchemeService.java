package org.mastodon.revised.trackscheme.action;

import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeSelection;
import org.scijava.service.SciJavaService;

public interface TrackSchemeService extends SciJavaService
{

	public TrackSchemeGraph< ?, ? > getTrackSchemeGraph();

	public TrackSchemeSelection getTrackSchemeSelection();

}

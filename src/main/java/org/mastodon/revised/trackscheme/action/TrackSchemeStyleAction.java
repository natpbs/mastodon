package org.mastodon.revised.trackscheme.action;

import java.awt.event.ActionEvent;

import org.mastodon.revised.trackscheme.display.DefaultTrackSchemeOverlay;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle.UpdateListener;
import org.scijava.ui.behaviour.util.AbstractNamedAction;

/**
 * Sets the style of a TrackScheme panel when executed & registers a
 * listener for style changes to repaint said panel.
 */
public class TrackSchemeStyleAction extends AbstractNamedAction
{

	private static final long serialVersionUID = 1L;

	private final TrackSchemeStyle style;

	private final UpdateListener panelRepainter;

	private final DefaultTrackSchemeOverlay overlay;

	public TrackSchemeStyleAction( final TrackSchemeStyle style, final DefaultTrackSchemeOverlay overlay, final UpdateListener panelRepainter )
	{
		super( style.name );
		this.style = style;
		this.overlay = overlay;
		this.panelRepainter = panelRepainter;
	}

	@Override
	public void actionPerformed( final ActionEvent e )
	{
		final TrackSchemeStyle oldStyle = overlay.setStyle( style );
		oldStyle.removeUpdateListener( panelRepainter );
		style.addUpdateListener( panelRepainter );
		panelRepainter.trackSchemeStyleChanged();
	}
}
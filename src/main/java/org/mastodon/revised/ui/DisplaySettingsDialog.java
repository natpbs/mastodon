package org.mastodon.revised.ui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.mastodon.revised.bdv.overlay.ui.RenderSettingsChooser;
import org.mastodon.revised.bdv.overlay.ui.RenderSettingsManager;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleChooser;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleManager;

public class DisplaySettingsDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public DisplaySettingsDialog(
			final RenderSettingsManager renderSettingsManager,
			final TrackSchemeStyleManager trackschemeStyleManager )
	{

		/*
		 * 
		 */

		setTitle( "Display settings" );
		setLayout( new BorderLayout() );

		// BDV display settings.
		final RenderSettingsChooser bdvDisplaySettingsChooser = new RenderSettingsChooser( null, renderSettingsManager );

		// TrackScheme display settings.
		final TrackSchemeStyleChooser trackSchemeStyleChooser = new TrackSchemeStyleChooser( null, trackschemeStyleManager );

		// Tabbed pane.
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add( "bdv", bdvDisplaySettingsChooser.getPanel() );
		tabbedPane.add( "trackscheme", trackSchemeStyleChooser.getPanel() );

		add( tabbedPane, BorderLayout.CENTER );

		pack();
	}


}

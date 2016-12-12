package org.mastodon.revised.ui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.mastodon.revised.bdv.overlay.ui.RenderSettingsChooser;
import org.mastodon.revised.bdv.overlay.ui.RenderSettingsManager;
import org.mastodon.revised.trackscheme.display.ui.TrackSchemeStyleChooser;

public class DisplaySettingsDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public DisplaySettingsDialog( final RenderSettingsManager renderSettingsManager )
	{

		/*
		 * 
		 */

		setTitle( "Display settings" );
		setSize( 420, 750 );
		setLayout( new BorderLayout() );

		// BDV display settings.
		final RenderSettingsChooser bdvDisplaySettingsChooser = new RenderSettingsChooser( null, renderSettingsManager );

		// TrackScheme display settings.
		final TrackSchemeStyleChooser trackSchemeStyleChooser = new TrackSchemeStyleChooser( null, null );

		// Tabbed pane.
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add( "bdv",
				new JScrollPane( bdvDisplaySettingsChooser.getPanel() ) );
//		tabbedPane.add( "trackscheme", trackSchemeStyleChooser.getDialog() );

		add( tabbedPane, BorderLayout.CENTER );

		pack();
	}


}

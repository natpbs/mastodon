package org.mastodon.revised.ui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.mastodon.revised.model.feature.FeatureKeys;
import org.mastodon.revised.model.feature.FeatureRangeCalculator;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleChooser;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleManager;

public class DisplaySettingsDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public DisplaySettingsDialog(
			final JFrame owner,
			final TrackSchemeStyleManager trackschemeStyleManager,
			final FeatureKeys graphFeatureKeys,
			final FeatureRangeCalculator graphFeatureRangeCalculator )
	{
		super( owner, "Display settings" );
		setLayout( new BorderLayout() );

		// TrackScheme display settings.
		final TrackSchemeStyleChooser trackSchemeStyleChooser =
				new TrackSchemeStyleChooser( trackschemeStyleManager,
						graphFeatureKeys, graphFeatureRangeCalculator,
						graphFeatureKeys, graphFeatureRangeCalculator );

		// Tabbed pane.
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add( "trackscheme", trackSchemeStyleChooser.getPanel() );

		getContentPane().add( tabbedPane, BorderLayout.CENTER );
		pack();
	}
}
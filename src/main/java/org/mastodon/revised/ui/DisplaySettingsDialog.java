package org.mastodon.revised.ui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.mastodon.revised.bdv.overlay.ui.RenderSettingsChooser;
import org.mastodon.revised.bdv.overlay.ui.RenderSettingsManager;
import org.mastodon.revised.model.feature.FeatureKeys;
import org.mastodon.revised.model.feature.FeatureRangeCalculator;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleChooser;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleManager;

public class DisplaySettingsDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public DisplaySettingsDialog(
			final JFrame owner,
			final RenderSettingsManager renderSettingsManager,
			final TrackSchemeStyleManager trackschemeStyleManager,
			final FeatureKeys graphFeatureKeys,
			final FeatureRangeCalculator graphFeatureRangeCalculator,
			final FeatureKeys branchGraphFeatureKeys,
			final FeatureRangeCalculator branchGraphFeatureRangeCalculator )
	{
		setTitle( "Display settings" );
		setLayout( new BorderLayout() );

		// BDV display settings.
		final RenderSettingsChooser bdvDisplaySettingsChooser = new RenderSettingsChooser( owner, renderSettingsManager );

		// TrackScheme display settings.
		final TrackSchemeStyleChooser trackSchemeStyleChooser =
				new TrackSchemeStyleChooser( owner, trackschemeStyleManager,
						graphFeatureKeys, graphFeatureRangeCalculator,
						branchGraphFeatureKeys, branchGraphFeatureRangeCalculator );

		// Tabbed pane.
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add( "bdv", bdvDisplaySettingsChooser.getPanel() );
		tabbedPane.add( "trackscheme", trackSchemeStyleChooser.getPanel() );

		add( tabbedPane, BorderLayout.CENTER );

		pack();
	}
}

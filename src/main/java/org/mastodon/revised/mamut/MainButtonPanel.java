package org.mastodon.revised.mamut;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.mastodon.revised.mamut.logger.MastodonLogger;
import org.scijava.ui.behaviour.util.RunnableAction;

public class MainButtonPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	public MainButtonPanel(final WindowManager windowManager)
	{
		final ActionMap actionMap = windowManager.getGlobalAppActions().getActionMap();
		
		final MastodonLogger logger = windowManager.getContext().getService( MastodonLogger.class );
		final RunnableAction notImplementedAction = new RunnableAction(
				"Notify not implemeted", () -> logger.error( "Not implemented yet." ) );

		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		final JLabel lblViews = new JLabel( "Views." );
		lblViews.setFont(lblViews.getFont().deriveFont(lblViews.getFont().getStyle() | Font.BOLD));
		final GridBagConstraints gbc_lblViews = new GridBagConstraints();
		gbc_lblViews.gridwidth = 2;
		gbc_lblViews.insets = new Insets(0, 0, 5, 0);
		gbc_lblViews.anchor = GridBagConstraints.WEST;
		gbc_lblViews.gridx = 0;
		gbc_lblViews.gridy = 2;
		add(lblViews, gbc_lblViews);

		final JLabel lblBigDataViewer = new JLabel("Big Data Viewer");
		final GridBagConstraints gbc_lblBigDataViewer = new GridBagConstraints();
		gbc_lblBigDataViewer.anchor = GridBagConstraints.EAST;
		gbc_lblBigDataViewer.insets = new Insets(0, 0, 5, 5);
		gbc_lblBigDataViewer.gridx = 0;
		gbc_lblBigDataViewer.gridy = 3;
		add(lblBigDataViewer, gbc_lblBigDataViewer);

		final JButton btnBdv = new JButton();
		btnBdv.setAction( actionMap.get( WindowManager.NEW_BDV_VIEW ) );
		btnBdv.setText( "bdv" );
		final GridBagConstraints gbc_btnBdv = new GridBagConstraints();
		gbc_btnBdv.anchor = GridBagConstraints.WEST;
		gbc_btnBdv.insets = new Insets(0, 0, 5, 0);
		gbc_btnBdv.gridx = 1;
		gbc_btnBdv.gridy = 3;
		add(btnBdv, gbc_btnBdv);

		final JLabel lblTrackscheme = new JLabel("TrackScheme");
		final GridBagConstraints gbc_lblTrackscheme = new GridBagConstraints();
		gbc_lblTrackscheme.anchor = GridBagConstraints.EAST;
		gbc_lblTrackscheme.insets = new Insets(0, 0, 5, 5);
		gbc_lblTrackscheme.gridx = 0;
		gbc_lblTrackscheme.gridy = 4;
		add(lblTrackscheme, gbc_lblTrackscheme);

		final JButton btnTs = new JButton();
		btnTs.setAction( actionMap.get( WindowManager.NEW_TRACKSCHEME_VIEW ) );
		btnTs.setText( "trackscheme" );
		final GridBagConstraints gbc_btnTs = new GridBagConstraints();
		gbc_btnTs.anchor = GridBagConstraints.WEST;
		gbc_btnTs.insets = new Insets(0, 0, 5, 0);
		gbc_btnTs.gridx = 1;
		gbc_btnTs.gridy = 4;
		add(btnTs, gbc_btnTs);

		final JLabel lblTable = new JLabel("Table");
		final GridBagConstraints gbc_lblTable = new GridBagConstraints();
		gbc_lblTable.anchor = GridBagConstraints.EAST;
		gbc_lblTable.insets = new Insets(0, 0, 5, 5);
		gbc_lblTable.gridx = 0;
		gbc_lblTable.gridy = 5;
		add(lblTable, gbc_lblTable);

		final JButton btnTa = new JButton();
		btnTa.setAction( notImplementedAction );
		btnTa.setText( "table" );
		final GridBagConstraints gbc_btnTa = new GridBagConstraints();
		gbc_btnTa.anchor = GridBagConstraints.WEST;
		gbc_btnTa.insets = new Insets(0, 0, 5, 0);
		gbc_btnTa.gridx = 1;
		gbc_btnTa.gridy = 5;
		add(btnTa, gbc_btnTa);

		final JLabel lblSelTable = new JLabel( "Selection Table" );
		final GridBagConstraints gbc_lblSelTable = new GridBagConstraints();
		gbc_lblSelTable.anchor = GridBagConstraints.EAST;
		gbc_lblSelTable.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblSelTable.gridx = 0;
		gbc_lblSelTable.gridy = 6;
		add( lblSelTable, gbc_lblSelTable );

		final JButton btnSelTa = new JButton();
		btnSelTa.setAction( notImplementedAction );
		btnSelTa.setText( "sel. table" );
		final GridBagConstraints gbc_btnSelTa = new GridBagConstraints();
		gbc_btnSelTa.anchor = GridBagConstraints.WEST;
		gbc_btnSelTa.insets = new Insets( 0, 0, 5, 0 );
		gbc_btnSelTa.gridx = 1;
		gbc_btnSelTa.gridy = 6;
		add( btnSelTa, gbc_btnSelTa );

		final JSeparator separator_1 = new JSeparator();
		final GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.insets = new Insets(0, 0, 5, 0);
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.gridwidth = 2;
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 7;
		add(separator_1, gbc_separator_1);

		final JLabel lblTags = new JLabel("Tags.");
		lblTags.setFont(lblTags.getFont().deriveFont(lblTags.getFont().getStyle() | Font.BOLD));
		final GridBagConstraints gbc_lblTags = new GridBagConstraints();
		gbc_lblTags.insets = new Insets(0, 0, 5, 0);
		gbc_lblTags.gridwidth = 2;
		gbc_lblTags.anchor = GridBagConstraints.WEST;
		gbc_lblTags.gridx = 0;
		gbc_lblTags.gridy = 8;
		add(lblTags, gbc_lblTags);

		final JLabel lblDefineTags = new JLabel("Define tags");
		final GridBagConstraints gbc_lblDefineTags = new GridBagConstraints();
		gbc_lblDefineTags.anchor = GridBagConstraints.EAST;
		gbc_lblDefineTags.insets = new Insets(0, 0, 5, 5);
		gbc_lblDefineTags.gridx = 0;
		gbc_lblDefineTags.gridy = 9;
		add(lblDefineTags, gbc_lblDefineTags);

		final JButton btnTg = new JButton();
		btnTg.setAction( actionMap.get( WindowManager.TAGSETS_DIALOG ) );
		btnTg.setText( "tags" );
		final GridBagConstraints gbc_btnTg = new GridBagConstraints();
		gbc_btnTg.anchor = GridBagConstraints.WEST;
		gbc_btnTg.insets = new Insets(0, 0, 5, 0);
		gbc_btnTg.gridx = 1;
		gbc_btnTg.gridy = 9;
		add(btnTg, gbc_btnTg);

		final JSeparator separator_2 = new JSeparator();
		final GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.insets = new Insets(0, 0, 5, 0);
		gbc_separator_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_2.gridwidth = 2;
		gbc_separator_2.gridx = 0;
		gbc_separator_2.gridy = 10;
		add(separator_2, gbc_separator_2);

		final JLabel lblFeatures = new JLabel("Features.");
		lblFeatures.setFont(lblFeatures.getFont().deriveFont(lblFeatures.getFont().getStyle() | Font.BOLD));
		final GridBagConstraints gbc_lblFeatures = new GridBagConstraints();
		gbc_lblFeatures.insets = new Insets(0, 0, 5, 0);
		gbc_lblFeatures.gridwidth = 2;
		gbc_lblFeatures.anchor = GridBagConstraints.WEST;
		gbc_lblFeatures.gridx = 0;
		gbc_lblFeatures.gridy = 11;
		add(lblFeatures, gbc_lblFeatures);

		final JLabel lblComputeFeatures = new JLabel("Compute features");
		final GridBagConstraints gbc_lblComputeFeatures = new GridBagConstraints();
		gbc_lblComputeFeatures.anchor = GridBagConstraints.EAST;
		gbc_lblComputeFeatures.insets = new Insets(0, 0, 5, 5);
		gbc_lblComputeFeatures.gridx = 0;
		gbc_lblComputeFeatures.gridy = 12;
		add(lblComputeFeatures, gbc_lblComputeFeatures);

		final JButton btnFc = new JButton();
		btnFc.setAction( actionMap.get( WindowManager.COMPUTE_FEATURE_DIALOG ) );
		btnFc.setText( "features" );
		final GridBagConstraints gbc_btnFc = new GridBagConstraints();
		gbc_btnFc.anchor = GridBagConstraints.WEST;
		gbc_btnFc.insets = new Insets(0, 0, 5, 0);
		gbc_btnFc.gridx = 1;
		gbc_btnFc.gridy = 12;
		add(btnFc, gbc_btnFc);

		final JSeparator separator_3 = new JSeparator();
		final GridBagConstraints gbc_separator_3 = new GridBagConstraints();
		gbc_separator_3.insets = new Insets(0, 0, 5, 0);
		gbc_separator_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_3.gridwidth = 2;
		gbc_separator_3.gridx = 0;
		gbc_separator_3.gridy = 13;
		add(separator_3, gbc_separator_3);

		final JLabel lblSaving = new JLabel("Saving.");
		lblSaving.setFont(lblSaving.getFont().deriveFont(lblSaving.getFont().getStyle() | Font.BOLD));
		final GridBagConstraints gbc_lblSaving = new GridBagConstraints();
		gbc_lblSaving.insets = new Insets(0, 0, 5, 0);
		gbc_lblSaving.gridwidth = 2;
		gbc_lblSaving.anchor = GridBagConstraints.WEST;
		gbc_lblSaving.gridx = 0;
		gbc_lblSaving.gridy = 14;
		add(lblSaving, gbc_lblSaving);

		final JLabel lblSave = new JLabel("Save");
		final GridBagConstraints gbc_lblSave = new GridBagConstraints();
		gbc_lblSave.anchor = GridBagConstraints.EAST;
		gbc_lblSave.insets = new Insets(0, 0, 5, 5);
		gbc_lblSave.gridx = 0;
		gbc_lblSave.gridy = 15;
		add(lblSave, gbc_lblSave);

		final JButton btnS = new JButton();
		btnS.setAction( actionMap.get( ProjectManager.SAVE_PROJECT ) );
		btnS.setText( "save" );
		final GridBagConstraints gbc_btnS = new GridBagConstraints();
		gbc_btnS.anchor = GridBagConstraints.WEST;
		gbc_btnS.insets = new Insets(0, 0, 5, 0);
		gbc_btnS.gridx = 1;
		gbc_btnS.gridy = 15;
		add(btnS, gbc_btnS);

		final JLabel lblSaveAs = new JLabel("Save as...");
		final GridBagConstraints gbc_lblSaveAs = new GridBagConstraints();
		gbc_lblSaveAs.anchor = GridBagConstraints.EAST;
		gbc_lblSaveAs.insets = new Insets(0, 0, 5, 5);
		gbc_lblSaveAs.gridx = 0;
		gbc_lblSaveAs.gridy = 16;
		add(lblSaveAs, gbc_lblSaveAs);

		final JButton btnSa = new JButton();
		btnSa.setAction( actionMap.get( ProjectManager.SAVE_PROJECT_AS ) );
		btnSa.setText( "save as..." );
		final GridBagConstraints gbc_btnSa = new GridBagConstraints();
		gbc_btnSa.anchor = GridBagConstraints.WEST;
		gbc_btnSa.insets = new Insets(0, 0, 5, 0);
		gbc_btnSa.gridx = 1;
		gbc_btnSa.gridy = 16;
		add(btnSa, gbc_btnSa);
	}
}

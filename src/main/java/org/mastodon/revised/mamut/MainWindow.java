package org.mastodon.revised.mamut;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.ui.util.FileChooser;
import org.mastodon.revised.ui.util.XmlFileFilter;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.io.yaml.YamlConfigIO;

import bdv.spimdata.SpimDataMinimal;
import bdv.spimdata.XmlIoSpimDataMinimal;
import mpicbg.spim.data.SpimDataException;

public class MainWindow extends JFrame
{
	private static final long serialVersionUID = 1L;

	/*
	 * FIELDS.
	 */

	private final MamutProject project;

	private final WindowManager windowManager;

	private File proposedProjectFile;

	private JButton featureComputationButton;

	private JButton displaySettingsButton;


	public MainWindow( final Model model, final SpimDataMinimal spimData, final String bdvFile, final InputTriggerConfig keyconf )
	{
		this.project = new MamutProject( new File( "." ), new File( bdvFile ), null );
		this.windowManager = new WindowManager(
				this,
				bdvFile,
				spimData,
				model,
				keyconf );
		go();
	}


	public MainWindow( final MamutProject project, final InputTriggerConfig keyconf ) throws IOException, SpimDataException
	{
		setTitle( "Mastodon MaMuT" );
		this.project = project;

		/*
		 * Load Model
		 */
		final Model model = new Model();
		if ( project.getRawModelFile() != null )
			model.loadRaw( project.getRawModelFile() );

		/*
		 * Load SpimData
		 */
		final String spimDataXmlFilename = project.getDatasetXmlFile().getAbsolutePath();
		final SpimDataMinimal spimData = new XmlIoSpimDataMinimal().load( spimDataXmlFilename );

		this.windowManager = new WindowManager(
				this,
				spimDataXmlFilename,
				spimData,
				model,
				keyconf );
		go();
	}

	private void go()
	{

		final JPanel buttonsPanel = new JPanel();
		final GridBagLayout gbl_buttonsPanel = new GridBagLayout();
		gbl_buttonsPanel.columnWidths = new int[] { 135, 135, 0 };
		gbl_buttonsPanel.rowHeights = new int[] { 0, 0, 25, 0, 0, 0, 25, 25, 0, 0, 25, 0 };
		gbl_buttonsPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_buttonsPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		buttonsPanel.setLayout( gbl_buttonsPanel );

		final JLabel lblViews = new JLabel( "Views" );
		final GridBagConstraints gbc_lblViews = new GridBagConstraints();
		gbc_lblViews.anchor = GridBagConstraints.WEST;
		gbc_lblViews.gridwidth = 2;
		gbc_lblViews.insets = new Insets( 0, 0, 5, 0 );
		gbc_lblViews.gridx = 0;
		gbc_lblViews.gridy = 1;
		buttonsPanel.add( lblViews, gbc_lblViews );
		final JButton bdvButton = new JButton( windowManager.getCreateBdvAction() );
		final GridBagConstraints gbc_bdvButton = new GridBagConstraints();
		gbc_bdvButton.fill = GridBagConstraints.BOTH;
		gbc_bdvButton.insets = new Insets( 0, 0, 5, 0 );
		gbc_bdvButton.gridx = 1;
		gbc_bdvButton.gridy = 2;
		buttonsPanel.add( bdvButton, gbc_bdvButton );
		final JButton trackschemeButton = new JButton( windowManager.getCreateTrackSchemeAction() );
		final GridBagConstraints gbc_trackschemeButton = new GridBagConstraints();
		gbc_trackschemeButton.fill = GridBagConstraints.BOTH;
		gbc_trackschemeButton.insets = new Insets( 0, 0, 5, 0 );
		gbc_trackschemeButton.gridx = 1;
		gbc_trackschemeButton.gridy = 3;
		buttonsPanel.add( trackschemeButton, gbc_trackschemeButton );

		final JButton btnCloseAll = new JButton( windowManager.getCloseAllAction() );
		final GridBagConstraints gbc_btnCloseAll = new GridBagConstraints();
		gbc_btnCloseAll.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCloseAll.insets = new Insets( 0, 0, 5, 0 );
		gbc_btnCloseAll.gridx = 1;
		gbc_btnCloseAll.gridy = 4;
		buttonsPanel.add( btnCloseAll, gbc_btnCloseAll );

		final JLabel lblFeatureSettings = new JLabel( "Feature & Settings" );
		final GridBagConstraints gbc_lblFeatureSettings = new GridBagConstraints();
		gbc_lblFeatureSettings.anchor = GridBagConstraints.WEST;
		gbc_lblFeatureSettings.gridwidth = 2;
		gbc_lblFeatureSettings.insets = new Insets( 0, 0, 5, 0 );
		gbc_lblFeatureSettings.gridx = 0;
		gbc_lblFeatureSettings.gridy = 5;
		buttonsPanel.add( lblFeatureSettings, gbc_lblFeatureSettings );

		this.featureComputationButton = new JButton( windowManager.getFeatureCalculationAction() );
		final GridBagConstraints gbc_featureComputationButton = new GridBagConstraints();
		gbc_featureComputationButton.fill = GridBagConstraints.BOTH;
		gbc_featureComputationButton.insets = new Insets( 0, 0, 5, 0 );
		gbc_featureComputationButton.gridx = 1;
		gbc_featureComputationButton.gridy = 6;
		buttonsPanel.add( featureComputationButton, gbc_featureComputationButton );

		this.displaySettingsButton = new JButton( windowManager.getDisplaySettingsAction() );
		final GridBagConstraints gbc_displaySettingsButton = new GridBagConstraints();
		gbc_displaySettingsButton.fill = GridBagConstraints.BOTH;
		gbc_displaySettingsButton.insets = new Insets( 0, 0, 5, 0 );
		gbc_displaySettingsButton.gridx = 1;
		gbc_displaySettingsButton.gridy = 7;
		buttonsPanel.add( displaySettingsButton, gbc_displaySettingsButton );

		final JLabel lblProject = new JLabel( "Project" );
		final GridBagConstraints gbc_lblProject = new GridBagConstraints();
		gbc_lblProject.anchor = GridBagConstraints.WEST;
		gbc_lblProject.gridwidth = 2;
		gbc_lblProject.insets = new Insets( 0, 0, 5, 0 );
		gbc_lblProject.gridx = 0;
		gbc_lblProject.gridy = 8;
		buttonsPanel.add( lblProject, gbc_lblProject );

		final Container content = getContentPane();
		content.add( buttonsPanel, BorderLayout.NORTH );

		final JButton saveProjectButton = new JButton( "save project" );
		saveProjectButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				saveProject();
			}
		} );


		final JButton importButton = new JButton( windowManager.getTgmmImportAction() );
		final GridBagConstraints gbc_importButton = new GridBagConstraints();
		gbc_importButton.fill = GridBagConstraints.BOTH;
		gbc_importButton.insets = new Insets( 0, 0, 5, 0 );
		gbc_importButton.gridx = 1;
		gbc_importButton.gridy = 9;
		buttonsPanel.add( importButton, gbc_importButton );
		final GridBagConstraints gbc_saveProjectButton = new GridBagConstraints();
		gbc_saveProjectButton.fill = GridBagConstraints.BOTH;
		gbc_saveProjectButton.gridx = 1;
		gbc_saveProjectButton.gridy = 10;
		buttonsPanel.add( saveProjectButton, gbc_saveProjectButton );

		setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosed( final WindowEvent e )
			{
				windowManager.closeAllWindows();
			}
		} );

		setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		pack();
	}

	public void saveProject( final File projectFile ) throws IOException
	{
		File modelFile = project.getRawModelFile();
		if ( modelFile == null )
		{
			modelFile = MamutProject.deriveRawModelFile( projectFile );
			project.setRawModelFile( modelFile );
		}

		project.setBasePath( projectFile.getParentFile() );

		final Model model = windowManager.getModel();
		model.saveRaw( modelFile );

		new MamutProjectIO().save( project, projectFile.getAbsolutePath() );
	}

	public WindowManager getWindowManager()
	{
		return windowManager;
	}

	public void saveProject()
	{
		String fn = proposedProjectFile == null ? null : proposedProjectFile.getAbsolutePath();

		File file = FileChooser.chooseFile(
				this,
				fn,
				new XmlFileFilter(),
				"Save MaMuT Project File",
				FileChooser.DialogType.SAVE );
		if ( file == null )
			return;

		fn = file.getAbsolutePath();
		if ( !fn.endsWith( ".xml" ) )
			file = new File( fn + ".xml" );

		if ( !file.equals( proposedProjectFile ) )
			project.setRawModelFile( MamutProject.deriveRawModelFile( file ) );

		try
		{
			proposedProjectFile = file;
			saveProject( proposedProjectFile );
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
		}
	}
	/**
	 * Try to load {@link InputTriggerConfig} from files in this order:
	 * <ol>
	 * <li>"keyconfig.yaml" in the current directory.
	 * <li>".mastodon/keyconfig.yaml" in the user's home directory.
	 * </ol>
	 */
	public static final InputTriggerConfig getInputTriggerConfig()
	{
		InputTriggerConfig conf = null;

		// try "keyconfig.yaml" in current directory
		if ( new File( "keyconfig.yaml" ).isFile() )
		{
			try
			{
				conf = new InputTriggerConfig( YamlConfigIO.read( "keyconfig.yaml" ) );
			}
			catch ( final IOException e )
			{}
		}

		// try "~/.mastodon/keyconfig.yaml"
		if ( conf == null )
		{
			final String fn = System.getProperty( "user.home" ) + "/.mastodon/keyconfig.yaml";
			if ( new File( fn ).isFile() )
			{
				try
				{
					conf = new InputTriggerConfig( YamlConfigIO.read( fn ) );
				}
				catch ( final IOException e )
				{}
			}
		}

		if ( conf == null )
		{
			conf = new InputTriggerConfig();
		}

		return conf;
	}

	public static void main( final String[] args ) throws IOException, SpimDataException, InvocationTargetException, InterruptedException, ExecutionException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		Locale.setDefault( Locale.US );
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

		final String bdvFile = "samples/datasethdf5.xml";
		final String modelFile = "samples/model_revised.raw";
		final MamutProject project = new MamutProject( new File( "." ), new File( bdvFile ), new File( modelFile ) );

		System.setProperty( "apple.laf.useScreenMenuBar", "true" );

		final MainWindow mw = new MainWindow( project, getInputTriggerConfig() );
		mw.setVisible( true );
	}
}

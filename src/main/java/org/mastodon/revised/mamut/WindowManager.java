package org.mastodon.revised.mamut;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.mastodon.revised.bdv.SharedBigDataViewerData;
import org.mastodon.revised.mamut.BdvManager.BdvWindow;
import org.mastodon.revised.mamut.TrackSchemeManager.TsWindow;
import org.mastodon.revised.model.feature.DefaultFeatureRangeCalculator;
import org.mastodon.revised.model.feature.FeatureComputerService;
import org.mastodon.revised.model.feature.FeatureRangeCalculator;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.ui.DisplaySettingsDialog;
import org.scijava.Context;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.AbstractNamedAction;

import bdv.spimdata.SpimDataMinimal;
import bdv.tools.ToggleDialogAction;
import bdv.util.BehaviourTransformEventHandlerPlanar;
import bdv.viewer.RequestRepaint;
import bdv.viewer.ViewerFrame;
import bdv.viewer.ViewerOptions;
import mpicbg.spim.data.generic.sequence.BasicViewSetup;
import net.imglib2.Dimensions;

public class WindowManager
{

	private final MamutAppModel mamutAppModel;

	private final MamutWindowModel mamutWindowModel;

	private final TrackSchemeManager trackSchemeManager;

	private final BdvManager bdvManager;

	private final ToggleDialogAction displaySettingsAction;

	private final ToggleDialogAction featureCalculationAction;

	private final AbstractNamedAction createBdvAction;

	private final AbstractNamedAction createTrackSchemeAction;

	private final AbstractNamedAction closeAllAction;

	private final AbstractNamedAction tgmmImportAction;

	/**
	 * <code>true</code> if the provided image data is 2D over time and not 3D.
	 */
	private final boolean is2D;

	public WindowManager(
			final JFrame owner,
			final String spimDataXmlFilename,
			final SpimDataMinimal spimData,
			final Model model,
			final InputTriggerConfig keyconf )
	{
		this.mamutWindowModel = new MamutWindowModel( keyconf );
		final RequestRepaint requestRepaint = new RequestRepaint()
		{
			@Override
			public void requestRepaint()
			{
				for ( final BdvWindow w : mamutWindowModel.bdvWindows )
					w.getViewerFrame().getViewerPanel().requestRepaint();
			}
		};

		final ViewerOptions options = ViewerOptions.options()
				.inputTriggerConfig( keyconf )
				.shareKeyPressedEvents( mamutWindowModel.keyPressedManager );

		// Test if we have 2D data.
		final List< BasicViewSetup > setups = spimData.getSequenceDescription().getViewSetupsOrdered();
		boolean testIs2D = true;
		for ( final BasicViewSetup setup : setups )
		{
			final Dimensions size = setup.getSize();
			if (size.dimension( 2 ) > 1)
			{
				testIs2D = false;
				break;
			}
		}
		this.is2D = testIs2D;
		if ( is2D )
			options.transformEventHandlerFactory( BehaviourTransformEventHandlerPlanar.factory() );

		final SharedBigDataViewerData sharedBdvData = new SharedBigDataViewerData( spimDataXmlFilename, spimData, options, requestRepaint );
		this.mamutAppModel = new MamutAppModel( model, sharedBdvData );
		this.trackSchemeManager = new TrackSchemeManager( mamutAppModel, mamutWindowModel );
		this.bdvManager = new BdvManager( mamutAppModel, mamutWindowModel );

		/*
		 * Create BDV window action.
		 */

		this.createBdvAction = new AbstractNamedAction( "bdv" )
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed( final ActionEvent arg0 )
			{
				createBigDataViewer();
			}
		};

		/*
		 * Create TrackScheme window action.
		 */

		this.createTrackSchemeAction = new AbstractNamedAction( "trackscheme" )
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed( final ActionEvent e )
			{
				createTrackScheme();
			}
		};

		/*
		 * Close all action.
		 */

		this.closeAllAction = new AbstractNamedAction( "close all" )
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed( final ActionEvent e )
			{
				closeAllWindows();
			}
		};

		/*
		 * Display settings.
		 */

		final FeatureRangeCalculator graphFeatureRangeCalculator =
				new DefaultFeatureRangeCalculator<>(
						mamutAppModel.model.getGraph(),
						mamutAppModel.model.getGraphFeatureModel() );

		final DisplaySettingsDialog displaySettingsDialog =
				new DisplaySettingsDialog(
						owner,
						trackSchemeManager.trackSchemeStyleManager,
						mamutAppModel.model.getGraphFeatureModel(),
						graphFeatureRangeCalculator );
		displaySettingsDialog.setSize( 480, 1000 );
		this.displaySettingsAction = new ToggleDialogAction( "display settings", displaySettingsDialog );

		/*
		 * Feature calculation dialog.
		 */

		/*
		 * TODO FIXE Ugly hack to get proper service instantiation. Fix it by
		 * proposing a proper Command decoupled from the GUI.
		 */
		final Context context = new Context();
		@SuppressWarnings( "unchecked" )
		final FeatureComputerService< Model > featureComputerService = context.getService( FeatureComputerService.class );
		final Dialog featureComputationDialog = new FeatureAndTagDialog( owner, mamutAppModel.model, featureComputerService );
		featureComputationDialog.setSize( 400, 400 );
		this.featureCalculationAction = new ToggleDialogAction( "features and tags", featureComputationDialog );

		/*
		 * TGMM import dialog.
		 */

		final TgmmImportDialog tgmmImportDialog = new TgmmImportDialog( owner );
		this.tgmmImportAction = new AbstractNamedAction( "import tgmm" )
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed( final ActionEvent e )
			{
				tgmmImportDialog.showImportDialog( mamutAppModel.sharedBdvData.getSpimData(), mamutAppModel.model );
			}
		};
	}

	public AbstractNamedAction getTgmmImportAction()
	{
		return tgmmImportAction;
	}

	public ToggleDialogAction getDisplaySettingsAction()
	{
		return displaySettingsAction;
	}

	public ToggleDialogAction getFeatureCalculationAction()
	{
		return featureCalculationAction;
	}

	public AbstractNamedAction getCreateBdvAction()
	{
		return createBdvAction;
	}

	public AbstractNamedAction getCreateTrackSchemeAction()
	{
		return createTrackSchemeAction;
	}

	public AbstractNamedAction getCloseAllAction()
	{
		return closeAllAction;
	}

	public ViewerFrame createBigDataViewer()
	{
		return bdvManager.createBigDataViewer( is2D );
	}

	private void createTrackScheme()
	{
		trackSchemeManager.createTrackScheme();
	}

	public void closeAllWindows()
	{
		final ArrayList< JFrame > frames = new ArrayList<>();
		for ( final BdvWindow w : mamutWindowModel.bdvWindows )
			frames.add( w.getViewerFrame() );
		for ( final TsWindow w : mamutWindowModel.tsWindows )
			frames.add( w.getTrackSchemeFrame() );
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				for ( final JFrame f : frames )
					f.dispatchEvent( new WindowEvent( f, WindowEvent.WINDOW_CLOSING ) );
			}
		} );
	}

	public Model getModel()
	{
		return mamutAppModel.model;
	}

	public SharedBigDataViewerData getSharedBigDataViewerData()
	{
		return mamutAppModel.sharedBdvData;
	}

	public MamutWindowModel getMamutWindowModel()
	{
		return mamutWindowModel;
	}

}

package org.mastodon.app;

import java.io.IOException;
import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.scijava.Context;

import mpicbg.spim.data.SpimDataException;

public class MastodonLogServiceExample
{

	public static void main( final String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException, SpimDataException
	{
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		Locale.setDefault( Locale.ROOT );
		System.setProperty( "apple.laf.useScreenMenuBar", "true" );

		final Context context = new Context();
//		final String projectFile = "../TrackMate3/samples/mamutproject";
//		final WindowManager windowManager = new WindowManager( context );
//		final MainWindow mw = new MainWindow( windowManager );
//		final MamutProject project = new MamutProjectIO().load( projectFile );
//		windowManager.getProjectManager().open( project );
//		mw.setVisible( true );

		final DefaultMastodonLogService logService = context.getService( DefaultMastodonLogService.class );
		logService.showDialog();
	}

}

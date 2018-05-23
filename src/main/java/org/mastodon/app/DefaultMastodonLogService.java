package org.mastodon.app;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.ui.swing.console.LoggingPanel;

@Plugin( type = MastodonLogService.class )
public class DefaultMastodonLogService extends AbstractService implements MastodonLogService
{

	private LoggingPanel loggingPanel;

	private class MyDialog extends JDialog
	{

		private static final long serialVersionUID = 1L;

		private static final String TITLE = "Mastodon log";

		public MyDialog()
		{
			super( ( JFrame ) null, TITLE );
			setSize( 300, 600 );
			loggingPanel = new LoggingPanel( getContext(), TITLE );
			getContentPane().add( loggingPanel, BorderLayout.CENTER );
		}

	}

	private JDialog dialog;

	public JDialog showDialog()
	{
		if ( dialog == null )
		{
			dialog = new MyDialog();
		}

		dialog.setVisible( true );
		return dialog;
	}

	@Override
	public void log( final String message, final Object source, final Color color )
	{
		if ( dialog == null || !dialog.isVisible() )
			showDialog();

	}

	@Override
	public void setProgress( final double val, final Object source )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus( final String status, final Object source )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void clearStatus( final Object source )
	{
		// TODO Auto-generated method stub

	}

}

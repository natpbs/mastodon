package org.mastodon.revised.trackscheme.action;

import javax.swing.JDialog;

import org.mastodon.revised.trackscheme.display.TrackSchemeFrame;
import org.mastodon.revised.trackscheme.display.ui.TrackSchemeStyleChooser;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin( type = TrackSchemeAction.class, name = "render settings", label = "show display settings" )
public class TrackSchemeStyleChooserAction implements TrackSchemeAction
{

	@Parameter
	private TrackSchemeService service;

	private JDialog styleDialog;

	@Override
	public void initialize()
	{
		final TrackSchemeFrame frame = service.getFrame( this );
		final TrackSchemeStyleChooser styleChooser = new TrackSchemeStyleChooser( frame, frame.getTrackschemePanel() );
		this.styleDialog = styleChooser.getDialog();

	}

	@Override
	public void run()
	{
		styleDialog.setVisible( !styleDialog.isVisible() );
	}

}

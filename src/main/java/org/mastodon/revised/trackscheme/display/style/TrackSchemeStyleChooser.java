/**
 *
 */
package org.mastodon.revised.trackscheme.display.style;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.MutableComboBoxModel;

import org.mastodon.revised.trackscheme.display.AbstractTrackSchemeOverlay;
import org.mastodon.revised.trackscheme.display.DefaultTrackSchemeOverlay;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleEditorPanel.TrackSchemeStyleEditorDialog;

/**
 * @author Jean=Yves Tinevez
 */
public class TrackSchemeStyleChooser
{

	private final TrackSchemeStyleChooserPanel panel;

	private final TrackSchemeStyleManager styleManager;

	private final MutableComboBoxModel< TrackSchemeStyle > model;

	public TrackSchemeStyleChooser( final JFrame owner, final TrackSchemeStyleManager trackschemeStyleManager )
	{

		this.styleManager = trackschemeStyleManager;
		this.model = styleManager.createComboBoxModel();
		if ( model.getSize() > 0 )
			model.setSelectedItem( model.getElementAt( 0 ) );

		this.panel = new TrackSchemeStyleChooserPanel( owner, model );
		panel.buttonDeleteStyle.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				delete();
			}
		} );
		panel.buttonEditStyle.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				edit();
			}
		} );
		panel.buttonNewStyle.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				newStyle();
			}
		} );
		panel.buttonSetStyleName.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				setStyleName();
			}
		} );
		panel.saveButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				panel.saveButton.setEnabled( false );
				try
				{
					saveStyles();
				}
				finally
				{
					panel.saveButton.setEnabled( true );
				}
			}
		} );
	}

	private void saveStyles()
	{
		styleManager.saveStyles();
	}

	private void setStyleName()
	{
		final TrackSchemeStyle current = ( TrackSchemeStyle ) model.getSelectedItem();
		if ( null == current || TrackSchemeStyle.defaults.contains( current ) )
			return;

		final String newName = ( String ) JOptionPane.showInputDialog(
				panel,
				"Enter the style name:",
				"Style name",
				JOptionPane.PLAIN_MESSAGE, null, null, current.name );
		current.name = newName;
	}

	private void newStyle()
	{
		TrackSchemeStyle current = ( TrackSchemeStyle ) model.getSelectedItem();
		styleManager.copy( current );
		if ( null == current )
			current = TrackSchemeStyle.defaultStyle();

		final String name = current.name;
		final Pattern pattern = Pattern.compile( "(.+) \\((\\d+)\\)$" );
		final Matcher matcher = pattern.matcher( name );
		int n;
		String prefix;
		if ( matcher.matches() )
		{
			final String nstr = matcher.group( 2 );
			n = Integer.parseInt( nstr );
			prefix = matcher.group( 1 );
		}
		else
		{
			n = 1;
			prefix = name;
		}
		String newName;
		INCREMENT: while ( true )
		{
			newName = prefix + " (" + ( ++n ) + ")";
			for ( int j = 0; j < model.getSize(); j++ )
			{
				if ( model.getElementAt( j ).name.equals( newName ) )
					continue INCREMENT;
			}
			break;
		}

		final TrackSchemeStyle newStyle = current.copy( newName );
		model.addElement( newStyle );
		model.setSelectedItem( newStyle );
	}

	private void edit()
	{
		final TrackSchemeStyle current = ( TrackSchemeStyle ) model.getSelectedItem();
		if ( null == current || TrackSchemeStyle.defaults.contains( current ) )
			return;

		final TrackSchemeStyle.UpdateListener listener = new TrackSchemeStyle.UpdateListener()
		{
			@Override
			public void trackSchemeStyleChanged()
			{
				final AbstractTrackSchemeOverlay overlay = panel.panelPreview.getGraphOverlay();
				if ( overlay instanceof DefaultTrackSchemeOverlay )
				{
					final DefaultTrackSchemeOverlay dtso = ( DefaultTrackSchemeOverlay ) overlay;
					dtso.setStyle( current );
				}
				panel.panelPreview.repaint();
			}
		};
		current.addUpdateListener( listener );
		final TrackSchemeStyleEditorDialog nameDialog = new TrackSchemeStyleEditorDialog( panel, current );
		nameDialog.addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final java.awt.event.WindowEvent e )
			{
				current.removeUpdateListener( listener );
			};
		} );
		nameDialog.setModal( true );
		nameDialog.setVisible( true );
	}

	private void delete()
	{
		if ( TrackSchemeStyle.defaults.contains( model.getSelectedItem() ) )
			return;

		model.removeElement( model.getSelectedItem() );
	}

	public TrackSchemeStyleChooserPanel getPanel()
	{
		return panel;
	}
}

/**
 *
 */
package org.mastodon.revised.trackscheme.display.style;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.MutableComboBoxModel;

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

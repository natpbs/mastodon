/**
 *
 */
package org.mastodon.revised.bdv.overlay.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;

import org.mastodon.revised.bdv.overlay.RenderSettings;

/**
 * @author Jean=Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt;
 *
 */
public class RenderSettingsChooser
{

	private final RenderSettingsPanel panel;

	private final RenderSettings targetSettings;

	private final MutableComboBoxModel< RenderSettings > model;

	public RenderSettingsChooser( final JFrame owner, final RenderSettingsManager renderSettingsManager )
	{
		// Give the choose its own render settings instance.
		this.targetSettings = RenderSettings.defaultStyle().copy( RenderSettings.defaultStyle().getName() );
		this.model = renderSettingsManager.createComboBoxModel();
		panel = new RenderSettingsPanel( owner, model, targetSettings );
		panel.okButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				okPressed();
			}
		} );
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
					renderSettingsManager.saveStyles();
				}
				finally
				{
					panel.saveButton.setEnabled( true );
				}
			}
		} );

	}

	private void setStyleName()
	{
		final RenderSettings current = ( RenderSettings ) model.getSelectedItem();
		if ( null == current || RenderSettings.defaults.contains( current ) )
			return;

		final String newName = ( String ) JOptionPane.showInputDialog( panel, "Enter the render settings name:", "Style name", JOptionPane.PLAIN_MESSAGE, null, null, current.getName() );
		current.setName( newName );
	}

	private void newStyle()
	{
		RenderSettings current = ( RenderSettings ) model.getSelectedItem();
		if ( null == current )
			current = RenderSettings.defaultStyle();

		final String name = current.getName();
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
				if ( model.getElementAt( j ).getName().equals( newName ) )
					continue INCREMENT;
			}
			break;
		}

		final RenderSettings newStyle = current.copy( newName );
		model.addElement( newStyle );
		model.setSelectedItem( newStyle );
	}

	private void delete()
	{
		if ( RenderSettings.defaults.contains( model.getSelectedItem() ) )
			return;

		model.removeElement( model.getSelectedItem() );
	}

	private void okPressed()
	{
		// TODO
	}

	public JPanel getPanel()
	{
		return panel;
	}

	public RenderSettings getRenderSettings()
	{
		return targetSettings;
	}
}

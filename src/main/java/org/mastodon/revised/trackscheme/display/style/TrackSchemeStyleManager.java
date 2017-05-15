package org.mastodon.revised.trackscheme.display.style;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;

/**
 * Manages a collection of {@link TrackSchemeStyle}.
 * <p>
 * Has serialization / deserialization facilities and can return models based on
 * the collection it manages.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class TrackSchemeStyleManager
{
	private static final String STYLE_FILE = System.getProperty( "user.home" ) + "/.mastodon/trackschemestyles.yaml";

	private final Vector< TrackSchemeStyle > tss;

	public TrackSchemeStyleManager()
	{
		this.tss = new Vector<>();
		for ( final TrackSchemeStyle ts : TrackSchemeStyle.defaults )
			tss.add( ts );

		loadStyles();
	}

	public void add( final TrackSchemeStyle ts )
	{
		tss.add( ts );
	}

	public TrackSchemeStyle copy( TrackSchemeStyle current )
	{
		if ( null == current )
			current = TrackSchemeStyle.defaultStyle();

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
			for ( int j = 0; j < tss.size(); j++ )
			{
				if ( tss.get( j ).getName().equals( newName ) )
					continue INCREMENT;
			}
			break;
		}

		final TrackSchemeStyle newStyle = current.copy( newName );
		tss.add( newStyle );
		return newStyle;
	}

	/**
	 * Exposes the collection of styles managed.
	 *
	 * @return the collection of styles.
	 */
	public Vector< TrackSchemeStyle > getStyles()
	{
		return tss;
	}

	private void loadStyles()
	{
		try
		{
			final FileReader input = new FileReader( STYLE_FILE );
			final Yaml yaml = TrackSchemeStyleIO.createYaml();
			final Iterable< Object > objs = yaml.loadAll( input );
			for ( final Object obj : objs )
			{
				final TrackSchemeStyle ts = ( TrackSchemeStyle ) obj;
				if ( null != ts )
					tss.add( ts );
			}

		}
		catch ( final FileNotFoundException e )
		{
			System.out.println( "TrackScheme style file " + STYLE_FILE + " not found. Using builtin styles." );
		}
	}

	public void saveStyles()
	{
		try
		{
			final List< TrackSchemeStyle > stylesToSave = new ArrayList<>();
			for ( int i = 0; i < tss.size(); i++ )
			{
				final TrackSchemeStyle style = tss.get( i );
				if ( TrackSchemeStyle.defaults.contains( style ) )
					continue;
				stylesToSave.add( style );
			}

			mkdirs( STYLE_FILE );
			final FileWriter output = new FileWriter( STYLE_FILE );
			final Yaml yaml = TrackSchemeStyleIO.createYaml();
			yaml.dumpAll( stylesToSave.iterator(), output );
			output.close();
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
		}
	}

	/*
	 * STATIC UTILITIES
	 */

	private static boolean mkdirs( final String fileName )
	{
		final File dir = new File( fileName ).getParentFile();
		return dir == null ? false : dir.mkdirs();
	}

}
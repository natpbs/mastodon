package org.mastodon.revised.util;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class AppUtil
{

	private static final String PROPERTIES_FILE_PATH = "../../../../mastodon-app.properties";

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern( "d-MMM-yyyy HH-mm-ss" );

	/**
	 * Returns the Mastodon-app artifact version, as stored in the Maven pom.xml
	 * of this project. For this to work, the pom must include a section where
	 * said version is written into a property file.
	 *
	 * @return the Mastodon-app artifact version.
	 */
	public static String getVersion()
	{
		final InputStream is = AppUtil.class.getResourceAsStream( PROPERTIES_FILE_PATH );
		final Properties p = new Properties();
		try
		{
			p.load( is );
			final String version = p.getProperty( "version" );
			return version;
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
			return "unknown";
		}
	}

	public static String now()
	{
		final LocalDateTime now = LocalDateTime.now();
		final String formatDateTime = now.format( DATE_FORMATTER );
		return formatDateTime;
	}

	public static void main( final String[] args )
	{
		System.out.println( getVersion() );
	}
}

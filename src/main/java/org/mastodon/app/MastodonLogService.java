package org.mastodon.app;

import java.awt.Color;

import org.scijava.service.Service;

public interface MastodonLogService extends Service
{

	public static final Color NORMAL_COLOR = Color.BLACK;

	public static final Color ERROR_COLOR = new Color( 0.8f, 0, 0 );

	public static final Color GREEN_COLOR = new Color( 0, 0.6f, 0 );

	public static final Color BLUE_COLOR = new Color( 0, 0, 0.7f );

	/**
	 * Appends the message, with the specified color.
	 *
	 * @param message
	 *            the message to append.
	 * @param source
	 *            the source of the message.
	 * @param color
	 *            the color to use.
	 */
	public void log( String message, Object source, Color color );

	/**
	 * Sends the message to the error channel of this logger.
	 *
	 * @param message
	 *            the message to send.
	 * @param source
	 *            the source of the message.
	 */
	public default void error( final String message, final Object source )
	{
		log( message, source, ERROR_COLOR );
	}

	/**
	 * Appends the message.
	 *
	 * @param message
	 *            the message to append.
	 * @param source
	 *            the source of the message.
	 */

	public default void log( final String message, final Object source )
	{
		log( message, source, NORMAL_COLOR );
	}

	/**
	 * Sets the progress value of the current process. Values should be between
	 * 0 and 1, 1 meaning the process if finished.
	 *
	 * @param val
	 *            the progress value (double from 0 to 1).
	 * @param source
	 *            the source to display status of.
	 */
	public void setProgress( double val, Object source );

	/**
	 * Sets the status message to be displayed.
	 *
	 * @param status
	 *            the status to display.
	 * @param source
	 *            the source to display status of.
	 */
	public void setStatus( String status, Object source );

	/**
	 * Clears the status.
	 * 
	 * @param source
	 *            the source to display status of.
	 */
	public void clearStatus( Object source );

}

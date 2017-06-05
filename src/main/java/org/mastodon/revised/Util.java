package org.mastodon.revised;

import java.awt.geom.Line2D;

// TODO rename to something more specifice, e.g., "GeometryUtils"
// TODO move (to package ...trackscheme.util ?)
public class Util
{
	/**
	 * Computes the distance of a point {@code A0 (x0, y0)} to a segment
	 * defined by two points {@code A1 (x1, y1)} and
	 * {@code A2 (x2, y2)}. If the projection of {@code A0} on the
	 * segment does not lie between {@code A1} and {@code A2}, this
	 * method returns the distance to the closest segment end-point.
	 *
	 * @param x0
	 *            x coordinate of point A0.
	 * @param y0
	 *            y coordinate of point A0.
	 * @param x1
	 *            x coordinate of segment extremity A1.
	 * @param y1
	 *            y coordinate of segment extremity A1.
	 * @param x2
	 *            x coordinate of segment extremity A2.
	 * @param y2
	 *            y coordinate of segment extremity A2.
	 * @return the distance from a line to a segment.
	 */
	public static final double segmentDist( final double x0, final double y0, final double x1, final double y1, final double x2, final double y2 )
	{
		final double l12sq = ( x2 - x1 ) * ( x2 - x1 ) + ( y2 - y1 ) * ( y2 - y1 );

		final double x = ( ( x0 - x1 ) * ( x2 - x1 ) + ( y0 - y1 ) * ( y2 - y1 ) ) / l12sq;
		if ( x < 0 ) { return Math.sqrt( ( x0 - x1 ) * ( x0 - x1 ) + ( y0 - y1 ) * ( y0 - y1 ) ); }
		if ( x > 1 ) { return Math.sqrt( ( x0 - x2 ) * ( x0 - x2 ) + ( y0 - y2 ) * ( y0 - y2 ) ); }

		final double d = Math.abs(
				( y2 - y1 ) * x0 - ( x2 - x1 ) * y0 + x2 * y1 - y2 * x1
				) / Math.sqrt( l12sq );
		return d;
	}

	/**
	 * Tests if the line segment AB from (x1,y1) to (x2,y2) intersects the line
	 * segment CD from (x3,y3) to (x4,y4).
	 *
	 * @param x1
	 *            x coordinates of A
	 * @param y1
	 *            y coordinates of A
	 * @param x2
	 *            x coordinates of B
	 * @param y2
	 *            y coordinates of B
	 * @param x3
	 *            x coordinates of C
	 * @param y3
	 *            y coordinates of C
	 * @param x4
	 *            x coordinates of D
	 * @param y4
	 *            y coordinates of D
	 * @return <code>true</code> if AB intersects CD.
	 */
	public static final boolean lineSegmentIntersect( final double x1, final double y1, final double x2, final double y2, final double x3, final double y3, final double x4, final double y4 )
	{
		return Line2D.linesIntersect( x1, y1, x2, y2, x3, y3, x4, y4 );
	}

	private Util()
	{}

}

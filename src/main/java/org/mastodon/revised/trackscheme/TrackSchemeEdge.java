package org.mastodon.revised.trackscheme;

import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.PoolObject;
import org.mastodon.revised.Util;
import org.mastodon.revised.trackscheme.TrackSchemeEdge.TrackSchemeEdgeGeometry;
import org.mastodon.revised.trackscheme.TrackSchemeGraph.TrackSchemeEdgePool;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class TrackSchemeEdge extends AbstractEdge< TrackSchemeEdge, TrackSchemeVertex, TrackSchemeEdgePool, ByteMappedElement > implements Entry< TrackSchemeEdge, TrackSchemeEdgeGeometry >
{

	final ModelGraphWrapper< ?, ? >.ModelEdgeWrapper modelEdge;

	@Override
	public String toString()
	{
		return String.format( "Edge( %s -> %s )", getSource().getLabel(), getTarget().getLabel() );
	}

	TrackSchemeEdge( final TrackSchemeEdgePool pool )
	{
		super( pool );
		modelEdge = pool.modelGraphWrapper.createEdgeWrapper( this );
	}

	@Override
	protected void setToUninitializedState()
	{
		super.setToUninitializedState();
		setScreenEdgeIndex( -1 );
	}

	public TrackSchemeEdge init( final int modelEdgeId )
	{
		setModelEdgeId( modelEdgeId );
		return this;
	}

	/**
	 * Gets the ID of the associated model edge, as defined by a
	 * {@link GraphIdBimap}. For {@link PoolObject} model edges, the ID will be
	 * the internal pool index of the model edge.
	 *
	 * @return the ID of the associated model edge.
	 */
	public int getModelEdgeId()
	{
		return pool.origEdgeIndex.get( this );
	}

	protected void setModelEdgeId( final int id )
	{
		pool.origEdgeIndex.setQuiet( this, id );
	}

	public int getScreenEdgeIndex()
	{
		return pool.screenEdgeIndex.get( this );
	}

	public void setScreenEdgeIndex( final int screenEdgeIndex )
	{
		pool.screenEdgeIndex.setQuiet( this, screenEdgeIndex );
	}

	/*
	 * GEOMETRY INTERFACE FOR R-TREES
	 */

	private final TrackSchemeEdgeGeometry goemetry = new TrackSchemeEdgeGeometry();

	@Override
	public TrackSchemeEdge value()
	{
		return this;
	}

	@Override
	public TrackSchemeEdgeGeometry geometry()
	{
		return goemetry;
	}

	public final class TrackSchemeEdgeGeometry implements Geometry
	{

		private final TrackSchemeVertex ref = vertexPool.createRef();

		private double xt()
		{
			return getTarget( ref ).getLayoutX();
		}

		private double yt()
		{
			return getTarget( ref ).getTimepoint();
		}

		private double xs()
		{
			return getSource( ref ).getLayoutX();
		}

		private double ys()
		{
			return getSource( ref ).getTimepoint();
		}

		@Override
		public double distance( final Rectangle r )
		{
			double x1 = xs();
			double y1 = ys();
			double x2 = xt();
			double y2 = yt();
			if ( r.contains( x1, y1 ) || r.contains( x2, y2 ) )
			{
				return 0.;
			}
			else
			{
				double d1 = distance( r.x1(), r.y1(), r.x1(), r.y2() );
				if ( d1 == 0. )
					return 0;
				double d2 = distance( r.x1(), r.y2(), r.x2(), r.y2() );
				if ( d2 == 0. )
					return 0.;
				double d3 = distance( r.x2(), r.y2(), r.x2(), r.y1() );
				double d4 = distance( r.x2(), r.y1(), r.x1(), r.y1() );
				return Math.min( d1, Math.min( d2, Math.min( d3, d4 ) ) );
			}
		}

		private double distance( final float x3, final float y3, final float x4, final float y4 )
		{
			double x1 = xs();
			double y1 = ys();
			double x2 = xt();
			double y2 = yt();
			double d1 = Util.segmentDist( xs(), ys(), x3, y3, x4, y4 );
			double d2 = Util.segmentDist( xt(), yt(), x3, y3, x4, y4 );
			double d3 = Util.segmentDist( x3, y3, x1, y1, x2, y2 );
			if ( d3 == 0. )
				return 0.;
			double d4 = Util.segmentDist( x4, y4, x1, y1, x2, y2 );
			if ( d4 == 0. )
				return 0.;
			else
				return Math.min( d1, Math.min( d2, Math.min( d3, d4 ) ) );
		}

		@Override
		public boolean intersects( final Rectangle r )
		{
			if ( !mbr().intersects( r ) )
				return false;

			double x1 = xs();
			double x2 = xt();
			double y1 = ys();
			double y2 = yt();

			return Util.lineSegmentIntersect( x1, y1, x2, y2, r.x1(), r.y1(), r.x1(), r.y2() )
					|| Util.lineSegmentIntersect( x1, y1, x2, y2, r.x1(), r.y1(), r.x2(), r.y1() )
					|| Util.lineSegmentIntersect( x1, y1, x2, y2, r.x2(), r.y1(), r.x2(), r.y2() )
					|| Util.lineSegmentIntersect( x1, y1, x2, y2, r.x1(), r.y2(), r.x2(), r.y2() );
		}

		@Override
		public Rectangle mbr()
		{
			double x1 = xs();
			double x2 = xt();
			double y1 = ys();
			double y2 = yt();
			return Geometries.rectangle( Math.min( x1, x2 ), Math.min( y1, y2 ), Math.max( x1, x2 ),
					Math.max( y1, y2 ) );
		}

	}
}

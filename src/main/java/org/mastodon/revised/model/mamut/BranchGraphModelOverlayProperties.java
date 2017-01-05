package org.mastodon.revised.model.mamut;

import org.mastodon.revised.bdv.overlay.OverlayGraph;
import org.mastodon.revised.bdv.overlay.wrap.OverlayProperties;
import org.mastodon.revised.model.mamut.branchgraph.BranchEdge;
import org.mastodon.revised.model.mamut.branchgraph.BranchVertex;
import org.mastodon.revised.model.mamut.branchgraph.ModelBranchGraph;

/**
 * Provides branch vertex {@link OverlayProperties properties} for BDV
 * {@link OverlayGraph}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 * @author Jean-Yves Tinevez
 */
public class BranchGraphModelOverlayProperties implements OverlayProperties< BranchVertex, BranchEdge >
{
	private final ModelBranchGraph branchGraph;

	private final BoundingSphereRadiusStatistics radiusStats;

	private final ModelGraph graph;

	public BranchGraphModelOverlayProperties(
			final ModelBranchGraph branchGraph,
			final ModelGraph graph,
			final BoundingSphereRadiusStatistics radiusStats )
	{
		this.branchGraph = branchGraph;
		this.graph = graph;
		this.radiusStats = radiusStats;
	}

	@Override
	public void localize( final BranchVertex v, final double[] position )
	{
		v.localize( position );
	}

	@Override
	public double getDoublePosition( final BranchVertex v, final int d )
	{
		return v.getDoublePosition( d );
	}

	@Override
	public void getCovariance( final BranchVertex v, final double[][] mat )
	{
		final Spot ref = graph.vertexRef();
		branchGraph.getLinkedVertex( v, ref ).getCovariance( mat );
		graph.releaseRef( ref );
	}

	@Override
	public double getBoundingSphereRadiusSquared( final BranchVertex v )
	{
		final Spot ref = graph.vertexRef();
		final double r2 = branchGraph.getLinkedVertex( v, ref ).getBoundingSphereRadiusSquared();
		graph.releaseRef( ref );
		return r2;
	}

	@Override
	public int getTimepoint( final BranchVertex v )
	{
		return v.getTimepoint();
	}

	@Override
	public String getLabel( final BranchVertex v )
	{
		return v.getLabel();
	}

	@Override
	public void setLabel( final BranchVertex v, final String label )
	{
		v.setLabel( label );
	}

	@Override
	public double getMaxBoundingSphereRadiusSquared( final int timepoint )
	{
		radiusStats.readLock().lock();
		try
		{
			return radiusStats.getMaxBoundingSphereRadiusSquared( timepoint );
		}
		finally
		{
			radiusStats.readLock().unlock();
		}
	}

	@Override
	public void setPosition( final BranchVertex v, final double position, final int d )
	{}

	@Override
	public void setPosition( final BranchVertex v, final double[] position )
	{}

	@Override
	public void setCovariance( final BranchVertex v, final double[][] mat )
	{}

	@Override
	public BranchVertex addVertex( final int timepoint, final double[] position, final double radius, final BranchVertex ref )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public BranchVertex addVertex( final int timepoint, final double[] position, final double[][] covariance, final BranchVertex ref )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public BranchEdge addEdge( final BranchVertex source, final BranchVertex target, final BranchEdge ref )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public void removeEdge( final BranchEdge e )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public void removeVertex( final BranchVertex v )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public void notifyGraphChanged()
	{}
}

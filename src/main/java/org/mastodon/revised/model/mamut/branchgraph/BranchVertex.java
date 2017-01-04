package org.mastodon.revised.model.mamut.branchgraph;
import static org.mastodon.pool.ByteUtils.INT_SIZE;

import org.mastodon.RefPool;
import org.mastodon.graph.ref.AbstractListenableVertex;
import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.revised.model.HasLabel;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.spatial.HasTimepoint;

import net.imglib2.RealLocalizable;

public class BranchVertex
		extends AbstractListenableVertex< BranchVertex, BranchEdge, ByteMappedElement >
		implements HasTimepoint, HasLabel, RealLocalizable
{
	protected static final int LINKED_VERTEX_ID = AbstractVertex.SIZE_IN_BYTES;
	protected static final int SIZE_IN_BYTES = LINKED_VERTEX_ID + INT_SIZE;

	private final RefPool< Spot > vertexBimap;

	protected BranchVertex( final BranchVertexPool vertexPool, final RefPool< Spot > vertexBimap )
	{
		super( vertexPool );
		this.vertexBimap = vertexBimap;
	}

	@Override
	public String toString()
	{
		final Spot ref = vertexBimap.createRef();
		final String str = vertexBimap.getObject( getLinkedVertexId(), ref ).toString();
		vertexBimap.releaseRef( ref );
		return "bv(" + getInternalPoolIndex() + ") -> " + str;
	}

	protected int getLinkedVertexId()
	{
		return access.getIndex( LINKED_VERTEX_ID );
	}

	protected void setLinkedVertexId( final int id )
	{
		access.putIndex( id, LINKED_VERTEX_ID );
	}

	public BranchVertex init( final Spot spot )
	{
		setLinkedVertexId( vertexBimap.getId( spot ) );
		initDone();
		return this;
	}

	@Override
	protected void setToUninitializedState() throws IllegalStateException
	{
		super.setToUninitializedState();
		setLinkedVertexId( -1 );
	}

	@Override
	public int numDimensions()
	{
		final Spot ref = vertexBimap.createRef();
		final int n = vertexBimap.getObject( getLinkedVertexId(), ref ).numDimensions();
		vertexBimap.releaseRef( ref );
		return n;
	}

	@Override
	public void localize( final float[] position )
	{
		final Spot ref = vertexBimap.createRef();
		vertexBimap.getObject( getLinkedVertexId(), ref ).localize( position );
		vertexBimap.releaseRef( ref );
	}

	@Override
	public void localize( final double[] position )
	{
		final Spot ref = vertexBimap.createRef();
		vertexBimap.getObject( getLinkedVertexId(), ref ).localize( position );
		vertexBimap.releaseRef( ref );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		final Spot ref = vertexBimap.createRef();
		final float x = vertexBimap.getObject( getLinkedVertexId(), ref ).getFloatPosition( d );
		vertexBimap.releaseRef( ref );
		return x;
	}

	@Override
	public double getDoublePosition( final int d )
	{
		final Spot ref = vertexBimap.createRef();
		final double x = vertexBimap.getObject( getLinkedVertexId(), ref ).getDoublePosition( d );
		vertexBimap.releaseRef( ref );
		return x;
	}

	@Override
	public int getTimepoint()
	{
		final Spot ref = vertexBimap.createRef();
		final int t = vertexBimap.getObject( getLinkedVertexId(), ref ).getTimepoint();
		vertexBimap.releaseRef( ref );
		return t;
	}

	@Override
	public String getLabel()
	{
		final Spot ref = vertexBimap.createRef();
		final String label = vertexBimap.getObject( getLinkedVertexId(), ref ).getLabel();
		vertexBimap.releaseRef( ref );
		return label;
	}

	@Override
	public void setLabel( final String label )
	{
		final Spot ref = vertexBimap.createRef();
		vertexBimap.getObject( getLinkedVertexId(), ref ).setLabel( label );
		vertexBimap.releaseRef( ref );
	}

}
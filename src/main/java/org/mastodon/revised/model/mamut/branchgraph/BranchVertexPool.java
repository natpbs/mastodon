package org.mastodon.revised.model.mamut.branchgraph;

import org.mastodon.RefPool;
import org.mastodon.graph.ref.AbstractListenableVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.revised.model.mamut.Spot;

public class BranchVertexPool
		extends AbstractListenableVertexPool< BranchVertex, BranchEdge, ByteMappedElement >
{
	public BranchVertexPool( final RefPool< Spot > vertexBimap, final int initialCapacity )
	{
		this( vertexBimap, initialCapacity, new BranchVertexFactory() );
	}

	private BranchVertexPool( final RefPool< Spot > vertexBimap, final int initialCapacity, final BranchVertexFactory vertexFactory )
	{
		super( initialCapacity, vertexFactory );
		vertexFactory.vertexPool = this;
		vertexFactory.vertexBimap = vertexBimap;
	}

	private static class BranchVertexFactory
			implements PoolObject.Factory< BranchVertex, ByteMappedElement >
	{
		private RefPool< Spot > vertexBimap;

		private BranchVertexPool vertexPool;

		@Override
		public int getSizeInBytes()
		{
			return BranchVertex.SIZE_IN_BYTES;
		}

		@Override
		public BranchVertex createEmptyRef()
		{
			return new BranchVertex( vertexPool, vertexBimap );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< BranchVertex > getRefClass()
		{
			return BranchVertex.class;
		}
	};

}

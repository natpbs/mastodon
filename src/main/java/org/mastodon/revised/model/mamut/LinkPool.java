package org.mastodon.revised.model.mamut;

import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.BooleanAttribute;

public class LinkPool extends AbstractListenableEdgePool< Link, Spot, ByteMappedElement >
{

	public static class LinkLayout extends AbstractEdgeLayout
	{
		final BooleanField visibility = booleanField();
	}

	public static final LinkLayout layout = new LinkLayout();

	final BooleanAttribute< Link > visibility = new BooleanAttribute<>( layout.visibility, this );

	LinkPool( final int initialCapacity, final SpotPool vertexPool )
	{
		super( initialCapacity, layout, Link.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ), vertexPool );
	}

	@Override
	protected Link createEmptyRef()
	{
		return new Link( this );
	}

	public BooleanAttribute< Link > visibilityProperty()
	{
		return visibility;
	}
}

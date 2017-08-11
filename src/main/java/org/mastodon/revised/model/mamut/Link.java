package org.mastodon.revised.model.mamut;

import org.mastodon.graph.ref.AbstractListenableEdge;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.revised.model.HasVisibility;

public class Link extends AbstractListenableEdge< Link, Spot, LinkPool, ByteMappedElement > implements HasVisibility
{
	/**
	 * Initialize a new {@link Link}.
	 *
	 * @return this {@link Link}.
	 */
	public Link init()
	{
		super.initDone();
		return this;
	}

	@Override
	public String toString()
	{
		return String.format( "Link( %d -> %d )", getSource().getInternalPoolIndex(), getTarget().getInternalPoolIndex() );
	}

	Link( final LinkPool pool )
	{
		super( pool );
	}

	protected void notifyEdgeAdded()
	{
		super.initDone();
	}

	@Override
	public boolean getVisibility()
	{
		return pool.visibility.get( this );
	}

	@Override
	public void setVisibility( final boolean visible )
	{
		pool.visibility.set( this, visible );
	}

}

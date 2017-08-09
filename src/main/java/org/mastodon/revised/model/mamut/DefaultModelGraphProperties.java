package org.mastodon.revised.model.mamut;

import org.mastodon.revised.trackscheme.ModelGraphProperties;

public class DefaultModelGraphProperties
		implements ModelGraphProperties< Spot, Link >
{
	private final ModelGraph graph;

	public DefaultModelGraphProperties( final ModelGraph graph )
	{
		this.graph = graph;
	}

	@Override
	public int getTimepoint( final Spot Spot )
	{
		return Spot.getTimepoint();
	}

	@Override
	public String getLabel( final Spot Spot )
	{
		return Spot.getLabel();
	}

	@Override
	public void setLabel( final Spot Spot, final String label )
	{
		Spot.setLabel( label );
	}

	@Override
	public Link addEdge( final Spot source, final Spot target, final Link ref )
	{
		return graph.addEdge( source, target, ref ).init();
	}

	@Override
	public Spot addVertex( final Spot ref )
	{
		// Do nothing.
		return null;
	}

	@Override
	public void removeEdge( final Link edge )
	{
		graph.remove( edge );
	}

	@Override
	public void removeVertex( final Spot vertex )
	{
		graph.remove( vertex );
	}

	@Override
	public void notifyGraphChanged()
	{
		graph.notifyGraphChanged();
	}
}

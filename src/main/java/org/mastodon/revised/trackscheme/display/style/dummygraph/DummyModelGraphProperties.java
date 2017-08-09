package org.mastodon.revised.trackscheme.display.style.dummygraph;

import org.mastodon.revised.trackscheme.ModelGraphProperties;

public class DummyModelGraphProperties implements ModelGraphProperties< DummyVertex, DummyEdge >
{

	private final DummyGraph graph;

	public DummyModelGraphProperties( final DummyGraph graph )
	{
		this.graph = graph;
	}

	@Override
	public int getTimepoint( final DummyVertex v )
	{
		return v.getTimepoint();
	}

	@Override
	public String getLabel( final DummyVertex v )
	{
		return v.getLabel();
	}

	@Override
	public void setLabel( final DummyVertex v, final String label )
	{
		v.setLabel( label );
	}

	@Override
	public DummyEdge addEdge( final DummyVertex source, final DummyVertex target, final DummyEdge ref )
	{
		return graph.addEdge( source, target, ref );
	}

	@Override
	public void removeEdge( final DummyEdge edege )
	{
		graph.remove( edege );
	}

	@Override
	public void removeVertex( final DummyVertex vertex )
	{
		graph.remove( vertex );
	}

	@Override
	public DummyVertex addVertex( final DummyVertex ref )
	{
		return graph.addVertex( ref );
	}

	@Override
	public void notifyGraphChanged()
	{}

}

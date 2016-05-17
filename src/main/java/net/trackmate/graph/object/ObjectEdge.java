package net.trackmate.graph.object;

import net.trackmate.graph.zzgraphinterfaces.Edge;

public class ObjectEdge< K > implements Edge< ObjectVertex< K > >
{

	private final ObjectVertex< K > source;

	private final ObjectVertex< K > target;

	ObjectEdge( final ObjectVertex< K > source, final ObjectVertex< K > target )
	{
		this.source = source;
		this.target = target;
	}

	@Override
	public ObjectVertex< K > getSource()
	{
		return source;
	}

	@Override
	public ObjectVertex< K > getSource( final ObjectVertex< K > vertex )
	{
		return source;
	}

	@Override
	public int getSourceOutIndex()
	{
		int outIndex = 0;
		for ( final Object e : source.outgoingEdges() )
		{
			if ( e.equals( this ) )
				break;
			++outIndex;
		}
		return outIndex;
	}

	@Override
	public ObjectVertex< K > getTarget()
	{
		return target;
	}

	@Override
	public ObjectVertex< K > getTarget( final ObjectVertex< K > vertex )
	{
		return target;
	}

	@Override
	public int getTargetInIndex()
	{
		int inIndex = 0;
		for ( final Object e : target.incomingEdges() )
		{
			if ( e.equals( this ) )
				break;
			++inIndex;
		}
		return inIndex;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append( "e(" );
		sb.append( source.toString() );
		sb.append( " -> " );
		sb.append( target.toString() );
		sb.append( ")" );
		return sb.toString();
	}
}

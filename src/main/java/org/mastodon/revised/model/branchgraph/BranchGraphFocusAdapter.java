package org.mastodon.revised.model.branchgraph;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.revised.ui.selection.FocusListener;
import org.mastodon.revised.ui.selection.FocusModel;
import org.mastodon.spatial.HasTimepoint;

public class BranchGraphFocusAdapter< V extends Vertex< E > & HasTimepoint, E extends Edge< V > >
		implements FocusModel< BranchVertex, BranchEdge >
{

	private final BranchGraph< V, E > branchGraph;

	private final ReadOnlyGraph< V, E > graph;

	private final FocusModel< V, E > focus;

	public BranchGraphFocusAdapter(
			final BranchGraph< V, E > branchGraph,
			final ReadOnlyGraph< V, E > graph,
			final FocusModel< V, E > focus )
	{
		this.branchGraph = branchGraph;
		this.graph = graph;
		this.focus = focus;
	}

	@Override
	public boolean addFocusListener( final FocusListener l )
	{
		return focus.addFocusListener( l );
	}

	@Override
	public boolean removeFocusListener( final FocusListener l )
	{
		return focus.removeFocusListener( l );
	}

	@Override
	public void focusVertex( final BranchVertex vertex )
	{
		if ( null == vertex )
			focus.focusVertex( null );
		else
		{
			final V vRef = graph.vertexRef();
			final V v = branchGraph.getLinkedVertex( vertex, vRef );
			focus.focusVertex( v );
			graph.releaseRef( vRef );
		}
	}

	@Override
	public BranchVertex getFocusedVertex( final BranchVertex ref )
	{
		final V vref = graph.vertexRef();
		final V focused = focus.getFocusedVertex( vref );
		if ( focused == null )
		{
			graph.releaseRef( vref );
			return null;
		}

		final BranchVertex bv = branchGraph.getBranchVertex( focused, ref );
		graph.releaseRef( vref );
		return bv;
	}

}

package org.mastodon.revised.model.branchgraph;

import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.revised.model.HasLabel;
import org.mastodon.revised.trackscheme.ModelFocusProperties;
import org.mastodon.revised.ui.selection.FocusListener;
import org.mastodon.revised.ui.selection.FocusModel;
import org.mastodon.spatial.HasTimepoint;

public class DefaultBranchGraphFocusProperties<
		V extends Vertex< E > & HasTimepoint & HasLabel, 
		E extends Edge< V > >
	implements ModelFocusProperties
{

	private final BranchGraph< V, E > branchGraph;

	private final ReadOnlyGraph< V, E > graph;

	private final FocusModel< V, E > focus;

	private final GraphIdBimap< BranchVertex, BranchEdge > bgIdmap;

	public DefaultBranchGraphFocusProperties(
			final BranchGraph< V, E > branchGraph,
			final GraphIdBimap< BranchVertex, BranchEdge > bgIdmap,
			final ReadOnlyGraph< V, E > graph,
			final FocusModel< V, E > focus )
	{
		this.branchGraph = branchGraph;
		this.bgIdmap = bgIdmap;
		this.graph = graph;
		this.focus = focus;
	}

	@Override
	public int getFocusedVertexId()
	{
		final V ref = graph.vertexRef();
		final V focused = focus.getFocusedVertex( ref );
		final int id;
		if ( focused == null )
		{
			id = -1;
		}
		else
		{
			final BranchVertex bvRef = branchGraph.vertexRef();
			final BranchVertex bv = branchGraph.getBranchVertex( focused, bvRef );
			if ( null == bv )
			{
				final BranchEdge beRef = branchGraph.edgeRef();
				final BranchEdge be = branchGraph.getBranchEdge( focused, beRef );
				id = bgIdmap.getEdgeId( be );
				branchGraph.releaseRef( beRef );
			}
			else
			{
				id = bgIdmap.getVertexId( bv );
			}
			branchGraph.releaseRef( bvRef );
		}
		graph.releaseRef( ref );
		return id;
	}

	@Override
	public void focusVertex( final int id )
	{
		if ( id < 0 )
			focus.focusVertex( null );
		else
		{
			// id is the id of a branch vertex
			final V vRef = graph.vertexRef();
			final BranchVertex bvRef = branchGraph.vertexRef();

			final BranchVertex bv = bgIdmap.getVertex( id, bvRef );
			final V v = branchGraph.getLinkedVertex( bv, vRef );
			focus.focusVertex( v );

			branchGraph.releaseRef( bvRef );
			graph.releaseRef( vRef );
		}
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

}

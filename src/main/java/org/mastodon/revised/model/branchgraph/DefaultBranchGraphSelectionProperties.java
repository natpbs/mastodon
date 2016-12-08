package org.mastodon.revised.model.branchgraph;

import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.revised.trackscheme.ModelSelectionProperties;
import org.mastodon.revised.ui.selection.Selection;
import org.mastodon.revised.ui.selection.SelectionListener;
import org.mastodon.spatial.HasTimepoint;

public class DefaultBranchGraphSelectionProperties< 
	V extends Vertex< E > & HasTimepoint, 
	E extends Edge< V > > 
implements ModelSelectionProperties
{

	private final BranchGraph< V, E > branchGraph;

	private final ReadOnlyGraph< V, E > graph;

	private final Selection< V, E > selection;

	private final GraphIdBimap< BranchVertex, BranchEdge > idmap;

	public DefaultBranchGraphSelectionProperties(
			final BranchGraph< V, E > branchGraph,
			final GraphIdBimap< BranchVertex, BranchEdge > bgIdmap,
			final ReadOnlyGraph< V, E > graph,
			final Selection< V, E > selection )
	{
		this.branchGraph = branchGraph;
		this.idmap = bgIdmap;
		this.graph = graph;
		this.selection = selection;
	}

	@Override
	public void setVertexSelected( final int vertexId, final boolean selected )
	{
		// vertexId is the the id of a branch vertex.
		final BranchVertex bvRef = branchGraph.vertexRef();
		final V vRef = graph.vertexRef();

		final BranchVertex bv = idmap.getVertex( vertexId, bvRef );
		selection.setSelected( branchGraph.getLinkedVertex( bv, vRef ), selected );

		branchGraph.releaseRef( bvRef );
		graph.releaseRef( vRef );
	}

	@Override
	public void setEdgeSelected( final int edgeId, final boolean selected )
	{
		// edgeId is the the id of a branch edge.
		final BranchEdge beRef = branchGraph.edgeRef();
		final E eRef = graph.edgeRef();
		final V vRef = graph.vertexRef();
		
		selection.pauseListeners();

		final BranchEdge be = idmap.getEdge( edgeId, beRef );
		E edge = branchGraph.getLinkedEdge( be, eRef );
		selection.setSelected( edge, selected );
		V target = edge.getTarget( vRef );
		do
		{
			selection.setSelected( target, selected );
			/*
			 * The target vertex is still linked to the branch edge, so this
			 * means that it is still in the middle of the branch. This in turn
			 * means that it has only one outgoing edge.
			 */
			edge = target.outgoingEdges().get( 0, eRef );
			selection.setSelected( edge, selected );
			target = edge.getTarget( vRef );
		}
		while ( be.equals( branchGraph.getBranchEdge( target, beRef ) ) );

		selection.resumeListeners();
		
		branchGraph.releaseRef( beRef );
		graph.releaseRef( eRef );
		graph.releaseRef( vRef );
	}

	@Override
	public void toggleVertexSelected( final int vertexId )
	{
		setVertexSelected( vertexId, !isVertexSelected( vertexId ) );

	}

	@Override
	public void toggleEdgeSelected( final int edgeId )
	{
		setEdgeSelected( edgeId, isEdgeSelected( edgeId ) );
	}

	@Override
	public boolean isVertexSelected( final int vertexId )
	{
		final BranchVertex bvRef = branchGraph.vertexRef();
		final V vRef = graph.vertexRef();
		
		final BranchVertex bv = idmap.getVertex( vertexId, bvRef );
		final boolean selected = selection.isSelected( branchGraph.getLinkedVertex( bv, vRef ) );
		
		branchGraph.releaseRef( bvRef );
		graph.releaseRef( vRef );

		return selected;
	}

	@Override
	public boolean isEdgeSelected( final int edgeId )
	{
		boolean selected = true;

		final BranchEdge beRef = branchGraph.edgeRef();
		final E eRef = graph.edgeRef();
		final V vRef = graph.vertexRef();

		/*
		 * The branch edge is selected iff all the edges and vertices of the
		 * branch are selected.
		 */

		final BranchEdge be = idmap.getEdge( edgeId, beRef );
		E edge = branchGraph.getLinkedEdge( be, eRef );
		if ( !selection.isSelected( edge ) )
		{
			selected = false;
		}
		else
		{
			V target = edge.getTarget( vRef );
			if ( !selection.isSelected( target ) )
			{
				selected = false;
			}
			else
			{
				while ( branchGraph.getBranchEdge( target, beRef ).equals( be ) )
				{
					/*
					 * The target vertex is still linked to the branch edge, so
					 * this means that it is still in the middle of the branch.
					 * This in turn means that it has only one outgoing edge.
					 */
					edge = target.outgoingEdges().get( 0, eRef );
					target = edge.getTarget( vRef );
					if ( !selection.isSelected( target ) )
					{
						selected = false;
						break;
					}
				}
			}
		}

		branchGraph.releaseRef( beRef );
		graph.releaseRef( eRef );
		graph.releaseRef( vRef );

		return selected;
	}

	@Override
	public void clearSelection()
	{
		selection.clearSelection();
	}


	@Override
	public boolean addSelectionListener( final SelectionListener l )
	{
		return selection.addSelectionListener( l );
	}

	@Override
	public boolean removeSelectionListener( final SelectionListener l )
	{
		return selection.removeSelectionListener( l );
	}

	@Override
	public void resumeListeners()
	{
		selection.resumeListeners();
	}

	@Override
	public void pauseListeners()
	{
		selection.pauseListeners();
	}
}

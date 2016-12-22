package org.mastodon.revised.model.branchgraph;

import java.util.Collection;

import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefSet;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.revised.ui.selection.Selection;
import org.mastodon.revised.ui.selection.SelectionListener;
import org.mastodon.spatial.HasTimepoint;

public class BranchGraphSelectionAdapter< V extends Vertex< E > & HasTimepoint, E extends Edge< V > >
		implements Selection< BranchVertex, BranchEdge >
{

	private final BranchGraph< V, E > branchGraph;

	private final ReadOnlyGraph< V, E > graph;

	private final Selection< V, E > selection;

	public BranchGraphSelectionAdapter(
			final BranchGraph< V, E > branchGraph,
			final ReadOnlyGraph< V, E > graph,
			final Selection< V, E > selection )
	{
		this.branchGraph = branchGraph;
		this.graph = graph;
		this.selection = selection;
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

	@Override
	public boolean isSelected( final BranchVertex vertex )
	{
		final V vRef = graph.vertexRef();
		final boolean selected = selection.isSelected( branchGraph.getLinkedVertex( vertex, vRef ) );
		graph.releaseRef( vRef );

		return selected;
	}

	@Override
	public boolean isSelected( final BranchEdge edge )
	{
		boolean selected = true;

		final BranchEdge beRef = branchGraph.edgeRef();
		final E eRef = graph.edgeRef();
		final V vRef = graph.vertexRef();

		/*
		 * The branch edge is selected iff all the edges and vertices of the
		 * branch are selected.
		 */

		E e = branchGraph.getLinkedEdge( edge, eRef );
		if ( !selection.isSelected( e ) )
		{
			selected = false;
		}
		else
		{
			V target = e.getTarget( vRef );
			if ( !selection.isSelected( target ) )
			{
				selected = false;
			}
			else
			{
				while ( edge.equals( branchGraph.getBranchEdge( target, beRef ) ) )
				{
					/*
					 * The target vertex is still linked to the branch edge, so
					 * this means that it is still in the middle of the branch.
					 * This in turn means that it has only one outgoing edge.
					 */
					e = target.outgoingEdges().get( 0, eRef );
					target = e.getTarget( vRef );
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
	public void setSelected( final BranchVertex vertex, final boolean selected )
	{
		final V vRef = graph.vertexRef();
		selection.setSelected( branchGraph.getLinkedVertex( vertex, vRef ), selected );
		graph.releaseRef( vRef );
	}

	@Override
	public void setSelected( final BranchEdge edge, final boolean selected )
	{
		selection.pauseListeners();
		setEdgeSelected( edge, selected );
		selection.resumeListeners();
	}

	/**
	 * Determines whether this call changed the underlying selection model.
	 *
	 * @param edge
	 *            the branch edge to select.
	 * @param selected
	 *            whether to select the specified edge.
	 * @return <code>true</code> if the selection model has been changed by this
	 *         call.
	 */
	private boolean setEdgeSelected( final BranchEdge edge, final boolean selected )
	{
		boolean changed = false;
		final E eRef = graph.edgeRef();
		final V vRef = graph.vertexRef();
		final BranchEdge beRef = branchGraph.edgeRef();

		E e = branchGraph.getLinkedEdge( edge, eRef );
		if ( !changed && ( selection.isSelected( e ) != selected ) )
			changed = true;

		selection.setSelected( e, selected );
		V target = e.getTarget( vRef );
		do
		{
			if ( !changed && ( selection.isSelected( target ) != selected ) )
				changed = true;

			selection.setSelected( target, selected );
			/*
			 * The target vertex is still linked to the branch edge, so this
			 * means that it is still in the middle of the branch. This in turn
			 * means that it has only one outgoing edge.
			 */
			if ( target.outgoingEdges().isEmpty() )
				break;
			e = target.outgoingEdges().get( 0, eRef );
			if ( !changed && ( selection.isSelected( e ) != selected ) )
				changed = true;

			selection.setSelected( e, selected );
			target = e.getTarget( vRef );
		}
		while ( edge.equals( branchGraph.getBranchEdge( target, beRef ) ) );

		branchGraph.releaseRef( beRef );
		graph.releaseRef( eRef );
		graph.releaseRef( vRef );
		return changed;
	}

	@Override
	public void toggle( final BranchVertex vertex )
	{
		setSelected( vertex, !isSelected( vertex ) );
	}

	@Override
	public void toggle( final BranchEdge edge )
	{
		setSelected( edge, !isSelected( edge ) );
	}

	@Override
	public boolean setEdgesSelected( final Collection< BranchEdge > edges, final boolean selected )
	{
		boolean changed = false;
		selection.pauseListeners();

		for ( final BranchEdge edge : edges )
			changed = setEdgeSelected( edge, selected ) || changed;

		selection.resumeListeners();
		return changed;
	}

	@Override
	public boolean setVerticesSelected( final Collection< BranchVertex > vertices, final boolean selected )
	{
		boolean changed = false;
		selection.pauseListeners();

		final V vRef = graph.vertexRef();
		for ( final BranchVertex vertex : vertices )
		{
			final V v = branchGraph.getLinkedVertex( vertex, vRef );
			if ( !changed && ( selection.isSelected( v ) != selected ) )
				changed = true;
			selection.setSelected( v, selected );
		}

		graph.releaseRef( vRef );
		selection.resumeListeners();
		return changed;
	}

	@Override
	public RefSet< BranchEdge > getSelectedEdges()
	{
		final RefSet< BranchEdge > edges = RefCollections.createRefSet( branchGraph.edges() );

		// Find branch edges linked to selected edges.
		final RefSet< E > selectedEdges = selection.getSelectedEdges();
		final BranchEdge beRef = branchGraph.edgeRef();
		for ( final E e : selectedEdges )
		{
			final BranchEdge edge = branchGraph.getBranchEdge( e, beRef );
			// Test if it is selected according to branch edge selection.
			if ( isSelected( edge ) )
				edges.add( edge );
		}

		// Find branch edges linked to selected vertices.
		final RefSet< V > selectedVertices = selection.getSelectedVertices();
		for ( final V v : selectedVertices )
		{
			final BranchEdge edge = branchGraph.getBranchEdge( v, beRef );
			// Test if it is selected according to branch edge selection.
			if ( null != edge && isSelected( edge ) )
				edges.add( edge);
		}

		branchGraph.releaseRef( beRef );
		return edges;
	}

	@Override
	public RefSet< BranchVertex > getSelectedVertices()
	{
		final RefSet< V > selectedVertices = selection.getSelectedVertices();
		final RefSet< BranchVertex > vertices = RefCollections.createRefSet( branchGraph.vertices() );

		final BranchVertex bvRef = branchGraph.vertexRef();
		for ( final V v : selectedVertices )
		{
			final BranchVertex vertex = branchGraph.getBranchVertex( v, bvRef );
			if ( null != vertex )
				vertices.add( vertex );
		}

		branchGraph.releaseRef( bvRef );
		return vertices;
	}

	@Override
	public boolean clearSelection()
	{
		return selection.clearSelection();
	}
}

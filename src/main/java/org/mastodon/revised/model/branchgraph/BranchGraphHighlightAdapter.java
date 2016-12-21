package org.mastodon.revised.model.branchgraph;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.revised.ui.selection.HighlightListener;
import org.mastodon.revised.ui.selection.HighlightModel;
import org.mastodon.spatial.HasTimepoint;

public class BranchGraphHighlightAdapter< V extends Vertex< E > & HasTimepoint, E extends Edge< V > >
		implements HighlightModel< BranchVertex, BranchEdge >
{

	private final BranchGraph< V, E > branchGraph;

	private final ReadOnlyGraph< V, E > graph;

	private final HighlightModel< V, E > highlight;

	public BranchGraphHighlightAdapter(
			final BranchGraph< V, E > branchGraph,
			final ReadOnlyGraph< V, E > graph,
			final HighlightModel< V, E > highlight )
	{
		this.branchGraph = branchGraph;
		this.graph = graph;
		this.highlight = highlight;
	}

	@Override
	public void highlightVertex( final BranchVertex vertex )
	{
		if ( null == vertex )
		{
			highlight.highlightVertex( null );
			return;
		}

		final V vRef = graph.vertexRef();
		highlight.highlightVertex( branchGraph.getLinkedVertex( vertex, vRef ) );
		graph.releaseRef( vRef );
	}

	@Override
	public void highlightEdge( final BranchEdge edge )
	{
		if ( null == edge )
		{
			highlight.highlightVertex( null );
			return;
		}

		final E eRef = graph.edgeRef();
		highlight.highlightEdge( branchGraph.getLinkedEdge( edge, eRef ) );
		graph.releaseRef( eRef );
	}

	@Override
	public BranchVertex getHighlightedVertex( final BranchVertex ref )
	{
		final V vref = graph.vertexRef();
		final V highlighted = highlight.getHighlightedVertex( vref );
		if ( highlighted == null )
		{
			graph.releaseRef( vref );
			return null;
		}

		final BranchVertex bv = branchGraph.getBranchVertex( highlighted, ref );
		graph.releaseRef( vref );
		return bv;
	}


	@Override
	public BranchEdge getHighlightedEdge( final BranchEdge ref )
	{
		final E eRef = graph.edgeRef();
		final E highlightedEdge = highlight.getHighlightedEdge( eRef );
		if ( highlightedEdge == null )
		{
			// Check if a vertex is highlighted.
			final V vRef = graph.vertexRef();
			final V highlightedVertex = highlight.getHighlightedVertex( vRef );
			if ( null == highlightedVertex )
			{
				// No, nothing.
				graph.releaseRef( vRef );
				graph.releaseRef( eRef );
				return null;
			}

			// Highlight its linked branch edge, if any.
			final BranchEdge be = branchGraph.getBranchEdge( highlightedVertex, ref );
			graph.releaseRef( vRef );
			graph.releaseRef( eRef );
			return be;
		}

		graph.releaseRef( eRef );
		return branchGraph.getBranchEdge( highlightedEdge, ref );
	}

	@Override
	public void clearHighlight()
	{
		highlight.clearHighlight();
	}

	@Override
	public boolean addHighlightListener( final HighlightListener l )
	{
		return highlight.addHighlightListener( l );
	}

	@Override
	public boolean removeHighlightListener( final HighlightListener l )
	{
		return highlight.removeHighlightListener( l );
	}
}

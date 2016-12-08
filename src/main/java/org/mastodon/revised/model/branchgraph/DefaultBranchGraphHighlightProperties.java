package org.mastodon.revised.model.branchgraph;

import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.revised.model.HasLabel;
import org.mastodon.revised.trackscheme.ModelHighlightProperties;
import org.mastodon.revised.ui.selection.HighlightListener;
import org.mastodon.revised.ui.selection.HighlightModel;
import org.mastodon.spatial.HasTimepoint;

public class DefaultBranchGraphHighlightProperties<
		V extends Vertex< E > & HasTimepoint & HasLabel, 
		E extends Edge< V > >
	implements ModelHighlightProperties
{

	private final BranchGraph< V, E > branchGraph;

	private final ReadOnlyGraph< V, E > graph;

	private final HighlightModel< V, E > highlight;

	private final GraphIdBimap< BranchVertex, BranchEdge > bgIdmap;

	public DefaultBranchGraphHighlightProperties(
			final BranchGraph< V, E > branchGraph,
			final GraphIdBimap< BranchVertex, BranchEdge > bgIdmap,
			final ReadOnlyGraph< V, E > graph,
			final HighlightModel< V, E > highlightModel )
	{
		this.branchGraph = branchGraph;
		this.bgIdmap = bgIdmap;
		this.graph = graph;
		this.highlight = highlightModel;
	}

	@Override
	public int getHighlightedVertexId()
	{
		final V ref = graph.vertexRef();
		final BranchVertex bvRef = branchGraph.vertexRef();
		
		final V highlighted = highlight.getHighlightedVertex( ref );
		final int id;
		if ( highlighted == null )
		{
			id = -1;
		}
		else
		{
			final BranchVertex bv = branchGraph.getBranchVertex( highlighted, bvRef );
			id = null == bv ? -1 : bv.getInternalPoolIndex();
		}

		graph.releaseRef( ref );
		branchGraph.releaseRef( bvRef );
		return id;
	}

	@Override
	public int getHighlightedEdgeId()
	{
		final E eRef = graph.edgeRef();
		final BranchEdge beRef = branchGraph.edgeRef();

		final E highlightedEdge = highlight.getHighlightedEdge( eRef );
		final int id;
		if ( highlightedEdge == null )
		{
			// check if a vertex is highlighted.
			final V vRef = graph.vertexRef();
			final V highlightedVertex = highlight.getHighlightedVertex( vRef );
			if ( null == highlightedVertex )
			{
				id = -1;
			}
			else
			{
				// Highlight its linked branch edge, if any.
				final BranchEdge be = branchGraph.getBranchEdge( highlightedVertex, beRef );
				if ( null == be )
					id = -1;
				else
					id = be.getInternalPoolIndex();
			}
			graph.releaseRef( vRef );
		}
		else
		{
			final BranchEdge be = branchGraph.getBranchEdge( highlightedEdge, beRef );
			id = be.getInternalPoolIndex();
		}

		graph.releaseRef( eRef );
		branchGraph.releaseRef( beRef );
		return id;
	}

	@Override
	public void highlightVertex( final int id )
	{
		// id is the id of a branch vertex.
		final BranchVertex bvRef = branchGraph.vertexRef();
		final V vRef = graph.vertexRef();

		final BranchVertex bv = bgIdmap.getVertex( id, bvRef );
		highlight.highlightVertex( branchGraph.getLinkedVertex( bv, vRef ) );

		branchGraph.releaseRef( bvRef );
		graph.releaseRef( vRef );
	}

	@Override
	public void highlightEdge( final int id )
	{
		// id is the id of a branch edge.
		final BranchEdge beRef = branchGraph.edgeRef();
		final E eRef = graph.edgeRef();

		final BranchEdge bv = bgIdmap.getEdge( id, beRef );
		highlight.highlightEdge( branchGraph.getLinkedEdge( bv, eRef ) );

		branchGraph.releaseRef( beRef );
		graph.releaseRef( eRef );
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

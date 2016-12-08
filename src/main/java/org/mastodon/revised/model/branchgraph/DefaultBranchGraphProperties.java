package org.mastodon.revised.model.branchgraph;

import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.revised.model.HasLabel;
import org.mastodon.revised.trackscheme.ModelGraphProperties;
import org.mastodon.revised.ui.selection.Selection;
import org.mastodon.spatial.HasTimepoint;

public class DefaultBranchGraphProperties<
	V extends Vertex< E > & HasTimepoint & HasLabel,
		E extends Edge< V > > implements ModelGraphProperties
{

	private final BranchGraph< V, E > branchGraph;

	private final Selection< V, E > selection;

	private final GraphIdBimap< BranchVertex, BranchEdge > idmap;

	private final ReadOnlyGraph< V, E > graph;

	public DefaultBranchGraphProperties(
			final BranchGraph< V, E > branchGraph,
			final GraphIdBimap< BranchVertex, BranchEdge > bgIdBimap,
			final ReadOnlyGraph< V, E > graph,
			final Selection< V, E > selection )
	{
		this.branchGraph = branchGraph;
		this.idmap = bgIdBimap;
		this.graph = graph;
		this.selection = selection;
	}

	@Override
	public ModelVertexProperties createVertexProperties()
	{
		return new VertexProps< V >( branchGraph, graph, this.idmap );
	}

	@Override
	public ModelEdgeProperties createEdgeProperties()
	{
		return new EdgeProps< E, V >( branchGraph, graph, this.idmap, selection );
	}

	private static class VertexProps< V extends Vertex< ? > & HasTimepoint & HasLabel >
			implements ModelVertexProperties
	{
		private final GraphIdBimap< BranchVertex, ? > idmap;

		private final V v;

		private final BranchGraph< V, ? > branchGraph;

		private final BranchVertex bv;

		private VertexProps(
				final BranchGraph< V, ? > branchGraph,
				final ReadOnlyGraph< V, ? > graph,
				final GraphIdBimap< BranchVertex, ? > idmap )
		{
			this.branchGraph = branchGraph;
			this.idmap = idmap;
			this.v = graph.vertexRef();
			this.bv = branchGraph.vertexRef();
		}


		@Override
		public String getLabel( final int id )
		{
			idmap.getVertex( id, bv );
			final V linkedVertex = branchGraph.getLinkedVertex( bv, v );
			return linkedVertex.getLabel();
		}

		@Override
		public void setLabel( final int id, final String label )
		{}

		@Override
		public boolean isSelected( final int id )
		{
			return false;
		}
	}

	private static class EdgeProps< E extends Edge< V >, V extends Vertex< E > & HasTimepoint >
			implements ModelEdgeProperties
	{
		private final GraphIdBimap< BranchVertex, BranchEdge > idmap;

		private final Selection< V, E > selection;

		private final E eRef;

		private final BranchGraph< V, E > branchGraph;

		private final BranchEdge be;

		private final V vRef;

		private final BranchEdge beRef;

		private EdgeProps(
				final BranchGraph< V, E > branchGraph,
				final ReadOnlyGraph< V, E > graph,
				final GraphIdBimap< BranchVertex, BranchEdge > idmap,
				final Selection< V, E > selection )
		{
			this.branchGraph = branchGraph;
			this.idmap = idmap;
			this.selection = selection;
			this.eRef = graph.edgeRef();
			this.vRef = graph.vertexRef();
			this.be = branchGraph.edgeRef();
			this.beRef = branchGraph.edgeRef();
		}

		@Override
		public boolean isSelected( final int id )
		{
			/*
			 * The branch edge is selected iff all the edges and vertices of the
			 * branch are selected.
			 */
			idmap.getEdge( id, be );
			E edge = branchGraph.getLinkedEdge( be, eRef );
			if ( !selection.isSelected( edge ) )
				return false;

			V target = edge.getTarget( vRef );
			if ( !selection.isSelected( target ) )
				return false;
			while ( be.equals( branchGraph.getBranchEdge( target, beRef ) ) )
			{
				/*
				 * The target vertex is still linked to the branch edge, so this
				 * means that it is still in the middle of the branch. This in
				 * turn means that it has only one outgoing edge.
				 */
				edge = target.outgoingEdges().get( 0, eRef );
				target = edge.getTarget( vRef );
				if ( !selection.isSelected( target ) )
					return false;
			}
			return true;
		}
	}
}

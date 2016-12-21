package org.mastodon.revised.model.branchgraph;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.revised.model.HasLabel;
import org.mastodon.revised.trackscheme.wrap.ModelGraphProperties;
import org.mastodon.spatial.HasTimepoint;

public class DefaultBranchGraphProperties<
	V extends Vertex< E > & HasTimepoint & HasLabel,
	E extends Edge< V > > implements ModelGraphProperties< BranchVertex, BranchEdge >
{

	private final BranchGraph< V, E > branchGraph;

	private final ReadOnlyGraph< V, E > graph;

	public DefaultBranchGraphProperties(
			final BranchGraph< V, E > branchGraph,
			final ReadOnlyGraph< V, E > graph )
	{
		this.branchGraph = branchGraph;
		this.graph = graph;
	}

	@Override
	public int getTimepoint( final BranchVertex v )
	{
		return v.getTimepoint();
	}

	@Override
	public String getLabel( final BranchVertex v )
	{
		final V ref = graph.vertexRef();
		final String label = branchGraph.getLinkedVertex( v, ref ).getLabel();
		graph.releaseRef( ref );
		return label;
	}

	@Override
	public void setLabel( final BranchVertex v, final String label )
	{
		final V ref = graph.vertexRef();
		branchGraph.getLinkedVertex( v, ref ).setLabel( label );;
		graph.releaseRef( ref );
	}
}

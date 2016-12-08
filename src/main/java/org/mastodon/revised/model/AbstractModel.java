package org.mastodon.revised.model;

import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.ref.AbstractListenableEdge;

/**
 * Manages the model graph.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class AbstractModel<
		MG extends AbstractModelGraph< MG, ?, ?, V, E, ? >,
		V extends AbstractSpot< V, E, ?, MG >,
		E extends AbstractListenableEdge< E, V, ? > >
{
	/**
	 * Exposes the graph managed by this model.
	 *
	 * @return the graph.
	 */
	public MG getGraph()
	{
		return modelGraph;
	}

	/**
	 * Exposes the bidirectional map between vertices and their id, and between
	 * edges and their id.
	 *
	 * @return the bidirectional id map.
	 */
	public GraphIdBimap< V, E > getGraphIdBimap()
	{
		return modelGraph.idmap;
	}

	public BranchGraph< V, E > getBranchGraph()
	{
		return branchGraph;
	}

	protected final MG modelGraph;

	protected BranchGraph< V, E > branchGraph;

	protected AbstractModel( final MG modelGraph )
	{
		this.modelGraph = modelGraph;
		this.branchGraph = new BranchGraph<>( modelGraph );
	}
}

package org.mastodon.revised.model;

import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ref.AbstractListenableEdge;
import org.mastodon.revised.model.feature.DefaultFeatureModel;
import org.mastodon.revised.model.feature.FeatureModel;

/**
 * Manages the model graph.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class AbstractModel<
		MG extends AbstractModelGraph< MG, ?, ?, V, E, ? >,
		V extends AbstractSpot< V, E, ?, MG >,
		E extends AbstractListenableEdge< E, V, ? >>
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

	public FeatureModel< V, E > featureModel()
	{
		return featureModel;
	}

	protected final MG modelGraph;

	protected final FeatureModel< V, E > featureModel;

	protected AbstractModel( final MG modelGraph )
	{
		this.modelGraph = modelGraph;
		this.featureModel = createFeatureModel();
	}

	protected FeatureModel< V, E > createFeatureModel()
	{
		return new DefaultFeatureModel<>();
	}
}

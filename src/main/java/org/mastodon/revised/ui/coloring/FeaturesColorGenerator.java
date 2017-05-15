package org.mastodon.revised.ui.coloring;

import java.awt.Color;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.ui.coloring.ColorMode.EdgeColorMode;
import org.mastodon.revised.ui.coloring.ColorMode.VertexColorMode;

/**
 * Color generator for vertices and edges from a feature model, following hints
 * from a {@link ColorMode}.
 * <p>
 * This color generator can deal with vertex and edge features and attribute
 * them to vertices and edges. Interestingly, it can color a vertex using an
 * edge feature, and vice-versa, abiding to the different modes of
 * {@link VertexColorMode} and {@link EdgeColorMode}. This color generator does
 * not deal with the {@link VertexColorMode#BRANCH_VERTEX},
 * {@link VertexColorMode#BRANCH_EDGE}, {@link EdgeColorMode#BRANCH_VERTEX} and
 * {@link EdgeColorMode#BRANCH_EDGE} cases.
 *
 * @param <V>
 *            the type of vertices to color.
 * @param <E>
 *            the type of edges to color.
 * @author Jean-Yves Tinevez
 */
public class FeaturesColorGenerator< V extends Vertex< E >, E extends Edge< V > >
		implements VertexColorGenerator< V >, EdgeColorGenerator< E >
{

	protected final ReadOnlyGraph< V, E > graph;

	protected ColorMode colorMode;

	protected VertexColorGenerator< V > vertexColorGenerator;

	protected EdgeColorGenerator< E > edgeColorGenerator;

	private final FeatureModel< V, E > features;

	public FeaturesColorGenerator(
			final ColorMode colorMode,
			final ReadOnlyGraph< V, E > graph,
			final FeatureModel< V, E > features )
	{
		this.colorMode = colorMode;
		this.graph = graph;
		this.features = features;
		colorModeChanged();
	}

	public void colorModeChanged()
	{
		vertexColorGenerator = FeaturesColorGenerators.getVertexColorGenerator( colorMode, graph, features );
		edgeColorGenerator = FeaturesColorGenerators.getEdgeColorGenerator( colorMode, graph, features );
	}

	@Override
	public Color color( final E edge )
	{
		return edgeColorGenerator.color( edge );
	}

	@Override
	public Color color( final V vertex )
	{
		return vertexColorGenerator.color( vertex );
	}

	public void setColorMode( final ColorMode colorMode )
	{
		this.colorMode = colorMode;
		colorModeChanged();
	}
}

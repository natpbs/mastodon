package org.mastodon.revised.trackscheme.display.style;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.ui.ColorMode.EdgeColorMode;
import org.mastodon.revised.ui.ColorMode.VertexColorMode;

/**
 * A color generator for a branch graph based on a {@link TrackSchemeStyle} with
 * settings made for a graph.
 * <p>
 * This color generator simply catches style settings for
 * {@link VertexColorMode#BRANCH_VERTEX} and {@link EdgeColorMode#BRANCH_EDGE}
 * and generates color for the branch vertices and edges assuming they are plain
 * vertices and edges in the branch graph.
 * 
 * @author Jean-Yves Tinevez.
 *
 * @param <BV>
 *            the type of the vertices in the branch graph.
 * @param <BE>
 *            the type of the edges in the branch graph.
 */
public class BranchGraphFeaturesColorGenerator< BV extends Vertex< BE >, BE extends Edge< BV > > extends FeaturesColorGenerator< BV, BE >
{

	private final FeatureModel< BV, BE > branchFeatures;

	public BranchGraphFeaturesColorGenerator( final ReadOnlyGraph< BV, BE > branchGraph, final FeatureModel< BV, BE > branchFeatures )
	{
		super( branchGraph, branchFeatures );
		this.branchFeatures = branchFeatures;
	}

	@Override
	public void trackSchemeStyleChanged()
	{
		super.trackSchemeStyleChanged();
		switch ( style.colorVertexBy )
		{
		case BRANCH_VERTEX:
		{
			final FeatureProjection< BV > vfp = branchFeatures.getVertexProjection( style.vertexColorFeatureKey );
			vertexColorGenerator = new ThisVertexColorGenerator( vfp, style.vertexColorMap, style.minVertexColorRange, style.maxVertexColorRange );
			break;
		}
		default:
			break;
		}

		switch ( style.colorEdgeBy )
		{
		default:
			break;
		case BRANCH_EDGE:
		{
			final FeatureProjection< BE > efp = branchFeatures.getEdgeProjection( style.edgeColorFeatureKey );
			edgeColorGenerator = new ThisEdgeColorGenerator( efp, style.edgeColorMap, style.minEdgeColorRange, style.maxEdgeColorRange );
			break;
		}
		}
	}

}

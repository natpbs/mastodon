package org.mastodon.revised.model.branchgraph;

import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.revised.model.HasLabel;
import org.mastodon.revised.trackscheme.ModelNavigationListener;
import org.mastodon.revised.trackscheme.ModelNavigationProperties;
import org.mastodon.revised.ui.selection.NavigationHandler;
import org.mastodon.revised.ui.selection.NavigationListener;
import org.mastodon.spatial.HasTimepoint;

public class DefaultBranchGraphNavigationProperties<
		V extends Vertex< E > & HasTimepoint & HasLabel, 
		E extends Edge< V > >
		implements ModelNavigationProperties, NavigationListener< V, E >
{

	private final BranchGraph< V, E > branchGraph;

	private final ReadOnlyGraph< V, E > graph;

	private final NavigationHandler< V, E > navigation;

	private final GraphIdBimap< BranchVertex, BranchEdge > bgIdmap;

	private ModelNavigationListener modelNavigationListener;

	public DefaultBranchGraphNavigationProperties(
			final BranchGraph< V, E > branchGraph,
			final GraphIdBimap< BranchVertex, BranchEdge > bgIdmap,
			final ReadOnlyGraph< V, E > graph,
			final NavigationHandler< V, E > navigation )
	{
		this.branchGraph = branchGraph;
		this.bgIdmap = bgIdmap;
		this.graph = graph;
		this.navigation = navigation;
		navigation.addNavigationListener( this );
	}

	@Override
	public void notifyNavigateToVertex( final int vertexId )
	{
		// vertexId is the id of a branch vertex.
		final BranchVertex bvRef = branchGraph.vertexRef();
		final V vRef = graph.vertexRef();

		final BranchVertex bv = bgIdmap.getVertex( vertexId, bvRef );
		final V v = branchGraph.getLinkedVertex( bv, vRef );
		navigation.notifyNavigateToVertex( v );

		graph.releaseRef( vRef );
		branchGraph.releaseRef( bvRef );
	}

	@Override
	public void notifyNavigateToEdge( final int edgeId )
	{
		// edgeId is the id of a branch edge.
		final BranchEdge beRef = branchGraph.edgeRef();
		final E vRef = graph.edgeRef();

		final BranchEdge be = bgIdmap.getEdge( edgeId, beRef );
		final E e = branchGraph.getLinkedEdge( be, vRef );
		navigation.notifyNavigateToEdge( e );

		graph.releaseRef( vRef );
		branchGraph.releaseRef( beRef );
	}

	@Override
	public void forwardNavigationEventsTo( final ModelNavigationListener listener )
	{
		this.modelNavigationListener = listener;
	}


	@Override
	public void navigateToVertex( final V vertex )
	{
		if ( modelNavigationListener != null )
		{
			final BranchVertex bvRef = branchGraph.vertexRef();
			final BranchVertex bv = branchGraph.getBranchVertex( vertex, bvRef );
			/*
			 * Navigate to a vertex only if there is a branch vertex associated
			 * to it. Careful, we want to navigate to a branch vertex id.
			 */
			if ( null != bv )
			{
				modelNavigationListener.navigateToVertex( bgIdmap.getVertexId( bv ) );
			}
			else
			{
				// Otherwise navigate to its edge
				final BranchEdge beRef = branchGraph.edgeRef();
				final E eRef = graph.edgeRef();

				final BranchEdge be = branchGraph.getBranchEdge( vertex, beRef );
				modelNavigationListener.navigateToEdge( bgIdmap.getEdgeId( be ) );

				graph.releaseRef( eRef );
				branchGraph.releaseRef( beRef );
			}
			branchGraph.releaseRef( bvRef );
		}
	}

	@Override
	public void navigateToEdge( final E edge )
	{
		if ( modelNavigationListener != null )
		{
			// Navigate to the branch edge associated with this edge.
			final BranchEdge beRef = branchGraph.edgeRef();

			final BranchEdge be = branchGraph.getBranchEdge( edge, beRef );
			modelNavigationListener.navigateToEdge( bgIdmap.getEdgeId( be ) );

			branchGraph.releaseRef( beRef );
		}
	}
}

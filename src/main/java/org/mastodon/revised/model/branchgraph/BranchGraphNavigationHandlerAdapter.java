package org.mastodon.revised.model.branchgraph;

import java.util.HashMap;
import java.util.Map;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchEdge;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.graph.branch.BranchVertex;
import org.mastodon.revised.ui.selection.NavigationHandler;
import org.mastodon.revised.ui.selection.NavigationListener;
import org.mastodon.spatial.HasTimepoint;

public class BranchGraphNavigationHandlerAdapter< V extends Vertex< E > & HasTimepoint, E extends Edge< V > >
		implements NavigationHandler< BranchVertex, BranchEdge >
{

	private final BranchGraph< V, E > branchGraph;

	private final ReadOnlyGraph< V, E > graph;

	private final NavigationHandler< V, E > navigation;

	private final Map< NavigationListener< BranchVertex, BranchEdge >, NavigationListenerBranchTranslator > translators;

	public BranchGraphNavigationHandlerAdapter(
			final BranchGraph< V, E > branchGraph,
			final ReadOnlyGraph< V, E > graph,
			final NavigationHandler< V, E > navigation )
	{
		this.branchGraph = branchGraph;
		this.graph = graph;
		this.navigation = navigation;
		this.translators = new HashMap<>();
	}

	@Override
	public void notifyNavigateToVertex( final BranchVertex vertex )
	{
		final V vref = graph.vertexRef();
		final V v = branchGraph.getLinkedVertex( vertex, vref );
		navigation.notifyNavigateToVertex( v );
		graph.releaseRef( vref );
	}

	@Override
	public void notifyNavigateToEdge( final BranchEdge edge )
	{
		final E eref = graph.edgeRef();
		final E e = branchGraph.getLinkedEdge( edge, eref );
		navigation.notifyNavigateToEdge( e );
		graph.releaseRef( eref );
	}

	@Override
	public boolean addNavigationListener( final NavigationListener< BranchVertex, BranchEdge > listener )
	{
		final NavigationListenerBranchTranslator translator = new NavigationListenerBranchTranslator( listener );
		final NavigationListenerBranchTranslator old = translators.put( listener, translator );
		if ( null != old )
			navigation.removeNavigationListener( old );

		return navigation.addNavigationListener( translator );
	}

	@Override
	public boolean removeNavigationListener( final NavigationListener< BranchVertex, BranchEdge > listener )
	{
		return navigation.removeNavigationListener( translators.get( listener ) );
	}

	/**
	 * Translate a listener for the branch graph to a listener for the linked
	 * graph.
	 *
	 * @author Jean-Yves Tinevez
	 */
	private class NavigationListenerBranchTranslator implements NavigationListener< V, E >
	{

		private final NavigationListener< BranchVertex, BranchEdge > listener;

		public NavigationListenerBranchTranslator( final NavigationListener< BranchVertex, BranchEdge > listener )
		{
			this.listener = listener;
		}

		@Override
		public void navigateToVertex( final V vertex )
		{
			final BranchVertex bvref = branchGraph.vertexRef();
			final BranchVertex bv = branchGraph.getBranchVertex( vertex, bvref );
			if ( bv != null )
			{
				listener.navigateToVertex( bv );
			}
			else
			{
				final BranchEdge beref = branchGraph.edgeRef();
				final BranchEdge be = branchGraph.getBranchEdge( vertex, beref );
				listener.navigateToEdge( be );
				branchGraph.releaseRef( beref );
			}
			branchGraph.releaseRef( bvref );
		}

		@Override
		public void navigateToEdge( final E edge )
		{
			final BranchEdge beref = branchGraph.edgeRef();
			final BranchEdge be = branchGraph.getBranchEdge( edge, beref );
			listener.navigateToEdge( be );
			branchGraph.releaseRef( beref );
		}

	}

}

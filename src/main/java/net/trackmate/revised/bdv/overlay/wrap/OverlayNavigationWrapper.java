package net.trackmate.revised.bdv.overlay.wrap;

import bdv.viewer.ViewerPanel;
import bdv.viewer.animate.TranslationAnimator;
import net.imglib2.realtransform.AffineTransform3D;
import net.trackmate.graph.zzgraphinterfaces.Edge;
import net.trackmate.graph.zzgraphinterfaces.Vertex;
import net.trackmate.revised.bdv.overlay.OverlayNavigation;
import net.trackmate.revised.bdv.overlay.ScreenEdgeMath;
import net.trackmate.revised.bdv.overlay.ScreenVertexMath;
import net.trackmate.revised.ui.selection.NavigationEtiquette;
import net.trackmate.revised.ui.selection.NavigationHandler;
import net.trackmate.revised.ui.selection.NavigationListener;

/**
 * TODO: javadoc
 * TODO: explain: how is the NavigationListener, Handler, Wrapper, etc interrelated.
 * TODO: NavigationListener to what? see Constructor: navigation.addNavigationListener( this );
 *
 * @param <V>
 * @param <E>
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class OverlayNavigationWrapper< V extends Vertex< E >, E extends Edge< V > > implements
		OverlayNavigation< OverlayVertexWrapper< V, E >, OverlayEdgeWrapper< V, E > >,
		NavigationListener< V, E >
{
	private final ViewerPanel panel;

	private final OverlayGraphWrapper< V, E > graph;

	private final NavigationHandler< V, E > navigation;

	private NavigationEtiquette navigationEtiquette;

	private NavigationBehaviour< V, E > navigationBehaviour;

	public OverlayNavigationWrapper(
			final ViewerPanel panel,
			final OverlayGraphWrapper< V, E > graph,
			final NavigationHandler< V, E > navigation )
	{
		this.panel = panel;
		this.graph = graph;
		this.navigation = navigation;
		setNavigationEtiquette( NavigationEtiquette.MINIMAL );
		navigation.addNavigationListener( this );
	}

	public NavigationEtiquette getNavigationEtiquette()
	{
		return navigationEtiquette;
	}

	public void setNavigationEtiquette( final NavigationEtiquette navigationEtiquette )
	{
		this.navigationEtiquette = navigationEtiquette;

		switch( navigationEtiquette )
		{
		case MINIMAL:
			navigationBehaviour = new MinimalNavigationBehaviour( 100, 100 );
			break;
		case CENTER_IF_INVISIBLE:
			navigationBehaviour = new CenterIfInvisibleNavigationBehaviour();
			break;
		case CENTERING:
		default:
			navigationBehaviour = new CenteringNavigationBehaviour();
			break;
		}
	}

	/*
	 * NavigationListener< V >
	 */

	@Override
	public void navigateToVertex( final V vertex )
	{
		final OverlayVertexWrapper< V, E > ref = graph.vertexRef();
		ref.wv = vertex;
		navigateToOverlayVertex( ref );
		graph.releaseRef( ref );
	}

	@Override
	public void navigateToEdge( final E edge )
	{
		final OverlayEdgeWrapper< V, E > ref = graph.edgeRef();
		ref.we = edge;
		navigateToOverlayEdge( ref );
		graph.releaseRef( ref );
	}

	/*
	 * OverlayNavigation< OverlayVertexWrapper< V, E > >
	 */

//	@Override
	public void navigateToOverlayVertex( final OverlayVertexWrapper< V, E > vertex )
	{
		// Always move in T.
		final int tp = vertex.getTimepoint();
		panel.setTimepoint( tp );

		final AffineTransform3D currentTransform = panel.getDisplay().getTransformEventHandler().getTransform();
		final double[] target = navigationBehaviour.navigateToVertex( vertex, currentTransform );
		if ( target != null )
		{
			final TranslationAnimator animator = new TranslationAnimator( currentTransform, target, 300 );
			animator.setTime( System.currentTimeMillis() );
			panel.setTransformAnimator( animator );
		}

		panel.requestRepaint();
	}

	public void navigateToOverlayEdge( final OverlayEdgeWrapper< V, E > edge )
	{
		// Always move in T.
		final OverlayVertexWrapper< V, E > ref = graph.vertexRef();
		final int tp = edge.getTarget( ref ).getTimepoint();
		graph.releaseRef( ref );
		panel.setTimepoint( tp );

		final AffineTransform3D currentTransform = panel.getDisplay().getTransformEventHandler().getTransform();
		final double[] target = navigationBehaviour.navigateToEdge( edge, currentTransform );
		if ( target != null )
		{
			final TranslationAnimator animator = new TranslationAnimator( currentTransform, target, 300 );
			animator.setTime( System.currentTimeMillis() );
			panel.setTransformAnimator( animator );
		}

		panel.requestRepaint();
	}

	@Override
	public void notifyNavigateToVertex( final OverlayVertexWrapper< V, E > vertex )
	{
		navigation.notifyNavigateToVertex( vertex.wv );
	}

	@Override
	public void notifyNavigateToEdge( final OverlayEdgeWrapper< V, E > edge )
	{
		navigation.notifyNavigateToEdge( edge.we );
	}

	/*
	 * Navigation behaviours
	 */

	interface NavigationBehaviour< V extends Vertex< E >, E extends Edge< V > >
	{
		public double[] navigateToVertex( final OverlayVertexWrapper< V, E > vertex, final AffineTransform3D currentTransform );

		public double[] navigateToEdge( final OverlayEdgeWrapper< V, E > edge, final AffineTransform3D currentTransform );
	}

	private class CenteringNavigationBehaviour implements NavigationBehaviour< V, E >
	{
		private final ScreenVertexMath screenVertexMath = new ScreenVertexMath();

		@Override
		public double[] navigateToVertex( final OverlayVertexWrapper< V, E > vertex, final AffineTransform3D t )
		{
			final int width = panel.getWidth();
			final int height = panel.getHeight();

			screenVertexMath.init( vertex, t );
			final double[] vPos = screenVertexMath.getViewPos();
			final double dx = width / 2 - vPos[ 0 ] + t.get( 0, 3 );
			final double dy = height / 2 - vPos[ 1 ] + t.get( 1, 3 );
			final double dz = -vPos[ 2 ] + t.get( 2, 3 );

			return new double[] { dx, dy, dz };
		}

		@Override
		public double[] navigateToEdge( final OverlayEdgeWrapper< V, E > edge, final AffineTransform3D currentTransform )
		{
			// TODO Auto-generated method stub
			System.err.println( "not implemented: CenteringNavigationBehaviour.navigateToEdge()" );
			new Throwable().printStackTrace( System.out );
			return null;
		}
	}

	private class CenterIfInvisibleNavigationBehaviour implements NavigationBehaviour< V, E >
	{
		private final ScreenVertexMath screenVertexMath = new ScreenVertexMath();

		@Override
		public double[] navigateToVertex( final OverlayVertexWrapper< V, E > vertex, final AffineTransform3D t )
		{
			final int width = panel.getWidth();
			final int height = panel.getHeight();

			screenVertexMath.init( vertex, t );
			final double[] vPos = screenVertexMath.getViewPos();
			double dx = t.get( 0, 3 );
			if ( vPos[ 0 ] < 0 || vPos[ 0 ] > width )
				dx += width / 2 - vPos[ 0 ];
			double dy = t.get( 1, 3 );
			if ( vPos[ 1 ] < 0 || vPos[ 1 ] > height )
				dy += height / 2 - vPos[ 1 ];
			final double dz = -vPos[ 2 ] + t.get( 2, 3 );

			return new double[] { dx, dy, dz };
		}

		@Override
		public double[] navigateToEdge( final OverlayEdgeWrapper< V, E > edge, final AffineTransform3D currentTransform )
		{
			// TODO Auto-generated method stub
			System.err.println( "not implemented: CenterIfInvisibleNavigationBehaviour.navigateToEdge()" );
			new Throwable().printStackTrace( System.out );
			return null;
		}
	}

	private class MinimalNavigationBehaviour implements NavigationBehaviour< V, E >
	{
		private final ScreenVertexMath screenVertexMath;

		private final ScreenEdgeMath< OverlayVertexWrapper< V, E >, OverlayEdgeWrapper< V, E > > screenEdgeMath;

		private final int screenBorderX;

		private final int screenBorderY;

		public MinimalNavigationBehaviour( final int screenBorderX, final int screenBorderY )
		{
			this.screenBorderX = screenBorderX;
			this.screenBorderY = screenBorderY;
			screenVertexMath = new ScreenVertexMath();
			screenEdgeMath = new ScreenEdgeMath< >( graph.vertexRef(), graph.vertexRef() );
		}

		@Override
		public double[] navigateToVertex( final OverlayVertexWrapper< V, E > vertex, final AffineTransform3D t )
		{
			final int width = panel.getWidth();
			final int height = panel.getHeight();

			screenVertexMath.init( vertex, t );
			final double[] vPos = screenVertexMath.getViewPos();
			double dx = t.get( 0, 3 );
			if ( vPos[ 0 ] < screenBorderX )
				dx += screenBorderX - vPos[ 0 ];
			else if ( vPos[ 0 ] > width - screenBorderX )
				dx += width - screenBorderX - vPos[ 0 ];

			double dy = t.get( 1, 3 );
			if ( vPos[ 1 ] < screenBorderY )
				dy += screenBorderY - vPos[ 1 ];
			else if ( vPos[ 1 ] > height - screenBorderY )
				dy += height - screenBorderY - vPos[ 1 ];

			final double dz = -vPos[ 2 ] + t.get( 2, 3 );

			return new double[] { dx, dy, dz };
		}

		@Override
		public double[] navigateToEdge( final OverlayEdgeWrapper< V, E > edge, final AffineTransform3D t )
		{
			final int width = panel.getWidth();
			final int height = panel.getHeight();
			final int edgeMaxWidth = width - 2 * screenBorderX;
			final int edgeMaxHeight = height- 2 * screenBorderY;

			screenEdgeMath.init( edge, t );
			final double[] min = screenEdgeMath.getViewMin();
			final double[] max = screenEdgeMath.getViewMax();
			final double[] c = screenEdgeMath.getViewPos();

			double dx = t.get( 0, 3 );
			if ( max[ 0 ] - min[ 0 ] > edgeMaxWidth )
				dx += ( width / 2 ) - c[ 0 ];
			else if ( min[ 0 ] < screenBorderX )
				dx += screenBorderX - min[ 0 ];
			else if ( max[ 0 ] > width - screenBorderX )
				dx += width - screenBorderX - max[ 0 ];

			double dy = t.get( 1, 3 );
			if ( max[ 1 ] - min[ 1 ] > edgeMaxHeight )
				dy += ( height / 2 ) - c[ 1 ];
			else if ( min[ 1 ] < screenBorderY )
				dy += screenBorderY - min[ 1 ];
			else if ( max[ 1 ] > height - screenBorderY )
				dy += height - screenBorderY - max[ 1 ];

			final double dz = -c[ 2 ] + t.get( 2, 3 );

			return new double[] { dx, dy, dz };
		}
	}
}

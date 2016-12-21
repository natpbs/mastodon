/**
 *
 */
package org.mastodon.revised.trackscheme.action;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ImageIcon;

import org.mastodon.revised.trackscheme.ScreenTransform;
import org.mastodon.revised.trackscheme.display.ConstrainScreenTransform;
import org.mastodon.revised.trackscheme.display.InertialScreenTransformEventHandler;
import org.mastodon.revised.trackscheme.display.OffsetHeaders.OffsetHeadersListener;
import org.mastodon.revised.trackscheme.display.TrackSchemePanel;
import org.mastodon.revised.trackscheme.display.animate.InterpolateScreenTransformAnimator;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.behaviour.DragBehaviour;

import net.imglib2.ui.OverlayRenderer;
import net.imglib2.ui.TransformEventHandler;
import net.imglib2.ui.TransformListener;

/**
 * Drag behaviour that implements a zoom rectangle in TrackScheme.
 * <p>
 * This class depends on the {@link TransformEventHandler} of the TrackScheme
 * display to be an {@link InertialScreenTransformEventHandler}, to be able to
 * pass it a transform animator that will execute the zoom. If this is not the
 * case, the zoom rectangle will be painted but this behaviour will not trigger
 * any effect.
 *
 * @author Jean-Yves Tinevez
 */
@Plugin( type = TrackSchemeBehaviour.class, name = "zoom rectangle" )
public class TrackSchemeZoomBehaviour implements DragBehaviour, TrackSchemeBehaviour, OffsetHeadersListener, TransformListener< ScreenTransform >
{

	private static final ImageIcon ZOOM_ICON = new ImageIcon( TrackSchemeZoomBehaviour.class.getResource( "zoom.png" ) );

	public static final Color ZOOM_GRAPH_OVERLAY_COLOR = Color.BLUE.darker();

	@Parameter
	private TrackSchemeService service;

	private TrackSchemePanel panel;

	private InertialScreenTransformEventHandler transformEventHandler;

	private boolean editing;

	private int headerWidth;

	private int headerHeight;

	private ScreenTransform screenTransform;

	private ZoomOverlay overlay;

	@Override
	public void initialize()
	{
		this.editing = false;
		this.screenTransform = new ScreenTransform();
		this.overlay = new ZoomOverlay();
		this.panel = service.getFrame( this ).getTrackschemePanel();
		final TransformEventHandler< ScreenTransform > teh = panel.getDisplay().getTransformEventHandler();
		if ( teh instanceof InertialScreenTransformEventHandler )
			this.transformEventHandler = ( InertialScreenTransformEventHandler ) teh;
		else
			this.transformEventHandler = null;

		// Create and register overlay.
		transformChanged( panel.getDisplay().getTransformEventHandler().getTransform() );
		updateHeadersVisibility( panel.getOffsetDecorations().isVisibleX(), panel.getOffsetDecorations().getWidth(),
				panel.getOffsetDecorations().isVisibleY(), panel.getOffsetDecorations().getHeight() );
		// put the overlay first, so that is below the graph rendering.
		panel.getDisplay().addOverlayRenderer( overlay );
		panel.getDisplay().addTransformListener( this );
		panel.getOffsetDecorations().addOffsetHeadersListener( this );
	}

	private void zoomTo( final ScreenTransform tstart, final ScreenTransform tend )
	{
		ConstrainScreenTransform.removeJitter( tend, tstart );
		if ( !tend.equals( tstart ) && null != transformEventHandler )
		{
			final InterpolateScreenTransformAnimator animator = new InterpolateScreenTransformAnimator( tstart, tend, 200 );
			transformEventHandler.setAnimator( animator );
			transformEventHandler.runAnimation();
		}
	}

	@Override
	public void updateHeadersVisibility( final boolean isVisibleX, final int width, final boolean isVisibleY, final int height )
	{
		headerWidth = isVisibleX ? width : 0;
		headerHeight = isVisibleY ? height : 0;
	}

	@Override
	public void transformChanged( final ScreenTransform transform )
	{
		synchronized ( screenTransform )
		{
			screenTransform.set( transform );
		}
	}

	@Override
	public void init( final int x, final int y )
	{
		overlay.ox = x;
		overlay.oy = y;
		overlay.ex = x;
		overlay.ey = y;
		editing = true;
		overlay.paint = true;
	}

	@Override
	public void drag( final int x, final int y )
	{
		if ( editing )
		{
			overlay.ex = x;
			overlay.ey = y;
			panel.repaint();
		}
	}

	@Override
	public void end( final int x, final int y )
	{
		if ( editing && null != transformEventHandler )
		{
			editing = false;
			final int x1 = Math.min( overlay.ox, overlay.ex ) - headerWidth;
			final int x2 = Math.max( overlay.ox, overlay.ex ) - headerWidth;
			final int y1 = Math.min( overlay.oy, overlay.ey ) - headerHeight;
			final int y2 = Math.max( overlay.oy, overlay.ey ) - headerHeight;
			final double[] screen1 = new double[] { x1, y1 };
			final double[] screen2 = new double[] { x2, y2 };
			final double[] layout1 = new double[ 2 ];
			final double[] layout2 = new double[ 2 ];

			screenTransform.applyInverse( layout1, screen1 );
			screenTransform.applyInverse( layout2, screen2 );

			final ScreenTransform tstart = transformEventHandler.getTransform();
			final ScreenTransform tend = new ScreenTransform(
					layout1[ 0 ],
					layout2[ 0 ],
					layout1[ 1 ],
					layout2[ 1 ],
					tstart.getScreenWidth(), tstart.getScreenHeight() );
			zoomTo( tstart, tend );
		}
		overlay.paint = false;
	}

	private class ZoomOverlay implements OverlayRenderer
	{

		public int ey;

		public int ex;

		public int oy;

		public int ox;

		private boolean paint;

		public ZoomOverlay()
		{
			paint = false;
		}

		@Override
		public void drawOverlays( final Graphics g )
		{
			if ( !paint )
				return;

			final int x1 = Math.min( ox, ex );
			final int x2 = Math.max( ox, ex );
			final int y1 = Math.min( oy, ey );
			final int y2 = Math.max( oy, ey );

			g.setColor( ZOOM_GRAPH_OVERLAY_COLOR );
			g.drawRect( x1, y1, x2 - x1, y2 - y1 );
			g.drawImage( ZOOM_ICON.getImage(), x1 + 3, y1 + 3, null );
		}

		@Override
		public void setCanvasSize( final int width, final int height )
		{}
	}
}

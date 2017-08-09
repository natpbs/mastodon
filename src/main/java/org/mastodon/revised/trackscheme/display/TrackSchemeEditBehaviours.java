/**
 *
 */
package org.mastodon.revised.trackscheme.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.mastodon.revised.trackscheme.ScreenTransform;
import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.undo.UndoPointMarker;
import org.scijava.ui.behaviour.DragBehaviour;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;
import org.scijava.ui.behaviour.util.TriggerBehaviourBindings;

import net.imglib2.ui.OverlayRenderer;
import net.imglib2.ui.TransformListener;

/**
 * @author Jean-Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt;
 *
 */
public class TrackSchemeEditBehaviours< V, E > extends Behaviours
{

	private static final String TOGGLE_LINK = "toggle link";

	private static final String[] TOGGLE_LINK_KEYS = new String[] { "L" };

	public static final Color EDIT_GRAPH_OVERLAY_COLOR = Color.RED.darker();
	public static final BasicStroke EDIT_GRAPH_OVERLAY_STROKE = new BasicStroke( 2f );
	public static final BasicStroke EDIT_GRAPH_OVERLAY_GHOST_STROKE = new BasicStroke(
			1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
			1.0f, new float[] { 4f, 10f }, 0f );

	private final TrackSchemeGraph< ?, ? > graph;

	private final AbstractTrackSchemeOverlay renderer;

	private final UndoPointMarker undo;

	private final TrackSchemePanel panel;

	private final EditOverlay overlay;

	public static < V, E > void installActionBindings(
			final TriggerBehaviourBindings triggerBehaviourBindings,
			final InputTriggerConfig config,
			final TrackSchemePanel panel,
			final TrackSchemeGraph< ?, ? > graph,
			final AbstractTrackSchemeOverlay renderer,
			final UndoPointMarker undo )
	{
		new TrackSchemeEditBehaviours< V, E >( config, panel, graph, renderer, undo )
				.install( triggerBehaviourBindings, "graph-special" );
	}

	private TrackSchemeEditBehaviours(
			final InputTriggerConfig config,
			final TrackSchemePanel panel,
			final TrackSchemeGraph< ?, ? > graph,
			final AbstractTrackSchemeOverlay renderer,
			final UndoPointMarker undo )
	{
		super( config, new String[] { "ts" } );
		this.panel = panel;
		this.graph = graph;
		this.renderer = renderer;
		this.undo = undo;

		// Create and register overlay.
		overlay = new EditOverlay();
		overlay.transformChanged( panel.getDisplay().getTransformEventHandler().getTransform() );
		// put the overlay first, so that is below the graph rendering.
		renderer.addOverlayRenderer( overlay );
		panel.getDisplay().addTransformListener( overlay );

		// Behaviours.
		behaviour( new ToggleLink(), TOGGLE_LINK, TOGGLE_LINK_KEYS );
	}

	private class ToggleLink implements DragBehaviour
	{

		private final TrackSchemeVertex source;

		private final TrackSchemeVertex target;

		private final TrackSchemeVertex tmp;

		private final TrackSchemeEdge eref;

		private boolean editing;

		public ToggleLink()
		{
			source = graph.vertexRef();
			target = graph.vertexRef();
			tmp = graph.vertexRef();
			eref = graph.edgeRef();
			editing = false;
		}

		@Override
		public void init( final int x, final int y )
		{
			// Get vertex we clicked inside.
			if ( renderer.getVertexAt( x, y, source ) != null )
			{
				overlay.from[ 0 ] = source.getLayoutX();
				overlay.from[ 1 ] = source.getTimepoint();
				overlay.to[ 0 ] = overlay.from[0];
				overlay.to[ 1 ] = overlay.to[0];
				editing = true;
				overlay.paint = true;
			}
		}

		@Override
		public void drag( final int x, final int y )
		{
			if ( editing )
			{
				if ( renderer.getVertexAt( x, y, target ) != null )
				{
					overlay.to[ 0 ] = target.getLayoutX();
					overlay.to[ 1 ] = target.getTimepoint();
					overlay.strongEdge = true;
				}
				else
				{
					overlay.vTo[ 0 ] = x - panel.getOffsetDecorations().getWidth();
					overlay.vTo[ 1 ] = y - panel.getOffsetDecorations().getHeight();
					overlay.screenTransform.applyInverse( overlay.to, overlay.vTo );
					overlay.strongEdge = false;
					panel.repaint();
				}
			}
		}

		@Override
		public void end( final int x, final int y )
		{
			if ( editing )
			{
				editing = false;
				overlay.paint = false;

				if ( renderer.getVertexAt( x, y, target ) != null )
				{
					overlay.to[ 0 ] = target.getLayoutX();
					overlay.to[ 1 ] = target.getTimepoint();

					// Prevent the creation of links between vertices in the
					// same time-point.
					if ( source.getTimepoint() == target.getTimepoint() )
						return;

					/*
					 * Careful with directed graphs. We always check and create
					 * links forward in time.
					 */
					if ( source.getTimepoint() > target.getTimepoint() )
					{
						tmp.refTo( source );
						source.refTo( target );
						target.refTo( tmp );
					}

					final TrackSchemeEdge edge = graph.getEdge( source, target, eref );
					if ( null == edge )
						graph.addEdge( source, target, eref );
					else
						graph.remove( edge );

					undo.setUndoPoint();
				}
			}
		}
	}

	private class EditOverlay implements OverlayRenderer, TransformListener< ScreenTransform >
	{

		public boolean strongEdge;

		/** The global coordinates to paint the link from. */
		private final double[] from;

		/** The global coordinates to paint the link to. */
		private final double[] to;

		/** The viewer coordinates to paint the link from. */
		private final double[] vFrom;

		/** The viewer coordinates to paint the link to. */
		private final double[] vTo;

		private final ScreenTransform screenTransform;

		private boolean paint;

		public EditOverlay()
		{
			from = new double[ 2 ];
			vFrom = new double[ 2 ];
			to = new double[ 2 ];
			vTo = new double[ 2 ];
			screenTransform = new ScreenTransform();
			paint = false;
		}

		@Override
		public void drawOverlays( final Graphics g )
		{
			if ( !paint )
				return;

			final Graphics2D graphics = ( Graphics2D ) g;
			g.setColor( EDIT_GRAPH_OVERLAY_COLOR );
			if ( strongEdge )
				graphics.setStroke( EDIT_GRAPH_OVERLAY_STROKE );
			screenTransform.apply( from, vFrom );
			screenTransform.apply( to, vTo );
			g.drawLine(
					( int ) vFrom[ 0 ] + panel.getOffsetDecorations().getWidth(),
					( int ) vFrom[ 1 ] + panel.getOffsetDecorations().getHeight(),
					( int ) vTo[ 0 ] + panel.getOffsetDecorations().getWidth(),
					( int ) vTo[ 1 ] + panel.getOffsetDecorations().getHeight() );
		}

		@Override
		public void setCanvasSize( final int width, final int height )
		{}

		@Override
		public void transformChanged( final ScreenTransform transform )
		{
			synchronized ( screenTransform )
			{
				screenTransform.set( transform );
			}
		}
	}
}

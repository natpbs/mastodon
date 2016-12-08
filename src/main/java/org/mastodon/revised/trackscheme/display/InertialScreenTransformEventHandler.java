package org.mastodon.revised.trackscheme.display;

import java.util.Timer;
import java.util.TimerTask;

import org.mastodon.revised.trackscheme.LineageTreeLayout;
import org.mastodon.revised.trackscheme.LineageTreeLayout.LayoutListener;
import org.mastodon.revised.trackscheme.ScreenTransform;
import org.mastodon.revised.trackscheme.display.OffsetHeaders.OffsetHeadersListener;
import org.mastodon.revised.trackscheme.display.animate.AbstractTransformAnimator;
import org.mastodon.revised.trackscheme.display.animate.InertialScreenTransformAnimator;
import org.mastodon.revised.trackscheme.display.animate.InterpolateScreenTransformAnimator;
import org.scijava.ui.behaviour.DragBehaviour;
import org.scijava.ui.behaviour.ScrollBehaviour;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;
import org.scijava.ui.behaviour.util.TriggerBehaviourBindings;

import bdv.BehaviourTransformEventHandler;
import net.imglib2.ui.TransformEventHandler;
import net.imglib2.ui.TransformEventHandlerFactory;
import net.imglib2.ui.TransformListener;

public class InertialScreenTransformEventHandler
	implements
		BehaviourTransformEventHandler< ScreenTransform >,
		TransformEventHandler< ScreenTransform >,
		LayoutListener,
		OffsetHeadersListener
{
	/*
	 * Configuration options
	 */

	private final double zoomScrollSensitivity = 0.05;

	private final boolean enableInertialZoom = false;

	private final boolean enableIntertialTranslate = true;



	/**
	 * The delay in ms between inertial movements updates.
	 */
	private static final long INERTIAL_ANIMATION_PERIOD = 20;

	/**
	 * Sets the maximal zoom level in X.
	 */
	private static final double MIN_SIBLINGS_ON_CANVAS = 4;

	/**
	 * Sets the maximal zoom level in Y.
	 */
	private static final double MIN_TIMEPOINTS_ON_CANVAS = 4;

	private static final double EPSILON = 0.0000000001;

	// ...JY settings...
//	private static final double borderRatioX = 0.5;
//	private static final double borderRatioY = 0.5;
//	private static final double maxSizeFactorX = 8;
//	private static final double maxSizeFactorY = 8;
//	private static final double boundXLayoutBorder = 0;
//	private static final double boundYLayoutBorder = 0;

	// ...TP settings...
	private static final double borderRatioX = 0;
	private static final double borderRatioY = 0;
	private static final double maxSizeFactorX = 1;
	private static final double maxSizeFactorY = 1;
	private static final double boundXLayoutBorder = 1;
	private static final double boundYLayoutBorder = 1;

	// ...still something else...
//	private static final double borderRatioX = 0.1;
//	private static final double borderRatioY = 0.1;
//	private static final double maxSizeFactorX = 1 / 0.8;
//	private static final double maxSizeFactorY = 1 / 0.8;
//	private static final double boundXLayoutBorder = 0;
//	private static final double boundYLayoutBorder = 0;

	public static TransformEventHandlerFactory< ScreenTransform > factory( final InputTriggerConfig config )
	{
		return new TransformEventHandlerFactory< ScreenTransform >()
		{
			@Override
			public TransformEventHandler< ScreenTransform > create( final TransformListener< ScreenTransform > transformListener )
			{
				return new InertialScreenTransformEventHandler( transformListener, config );
			}
		};
	}

	/**
	 * Current boundaries to enforce for the transform.
	 *
	 * See {@link #layoutChanged(LineageTreeLayout)} and
	 * {@link #setLayoutRangeY(double, double)} for computation of the X and Y
	 * range, respectively.
	 *
	 * See
	 * {@link ConstrainScreenTransform#constrainTransform(ScreenTransform, double, double, double, double, double, double, double, double, double, double)}
	 * for details on enforcing transform boundaries.
	 */
	private double boundXMin, boundXMax, boundYMin, boundYMax;

	/**
	 * Current maximum size to enforce for the transform.
	 */
	private double maxSizeX, maxSizeY;

	/**
	 * current canvas width.
	 */
	private int canvasWidth;

	/**
	 * current canvas height.
	 */
	private int canvasHeight;

	/**
	 * current width of vertical header.
	 */
	private int headerWidth;

	/**
	 * current height of horizontal header.
	 */
	private int headerHeight;

	/**
	 * Whether the transform should stay fully zoomed out in X when the
	 * {@link #layoutChanged(LineageTreeLayout) layout changes}.
	 */
	private boolean stayFullyZoomedOut;

	/**
	 * Current source to screen transform.
	 */
	final private ScreenTransform transform = new ScreenTransform( -10000, 10000, -10000, 10000, 800, 600 );

	/**
	 * Whom to notify when the current transform is changed.
	 */
	private TransformListener< ScreenTransform > listener;

	/**
	 * Timer that runs {@link #currentTimerTask}.
	 */
	private final Timer timer;

	/**
	 * The task running the current animation.
	 */
	private TimerTask currentTimerTask;

	private AbstractTransformAnimator< ScreenTransform > animator;

	private final Behaviours behaviours;

	public InertialScreenTransformEventHandler( final TransformListener< ScreenTransform > listener, final InputTriggerConfig config )
	{
		this.listener = listener;

		timer = new Timer( "TrackScheme transform animation", true );
		currentTimerTask = null;

		final String DRAG_TRANSLATE = "drag translate";
		final String SCROLL_TRANSLATE = "scroll translate";
		final String ZOOM_X = "zoom horizontal";
		final String ZOOM_Y = "zoom vertical";
		final String ZOOM_XY = "zoom";

		behaviours = new Behaviours( config, "bdv" );
		behaviours.behaviour( new TranslateDragBehaviour(), DRAG_TRANSLATE, "button2", "button3" );
		behaviours.behaviour( new TranslateScrollBehaviour(), SCROLL_TRANSLATE, "scroll" );
		behaviours.behaviour( new ZoomScrollBehaviour( ScrollAxis.X ), ZOOM_X, "shift scroll" );
		behaviours.behaviour( new ZoomScrollBehaviour( ScrollAxis.Y ), ZOOM_Y, "ctrl scroll", "alt scroll" );
		behaviours.behaviour( new ZoomScrollBehaviour( ScrollAxis.XY ), ZOOM_XY, "meta scroll", "ctrl shift scroll" );
	}

	@Override
	public ScreenTransform getTransform()
	{
		synchronized ( transform )
		{
			return transform.copy();
		}
	}

	@Override
	public void setTransform( final ScreenTransform t )
	{
		synchronized ( transform )
		{
			transform.set( t );
		}
	}

	@Override
	public void setCanvasSize( final int width, final int height, final boolean updateTransform )
	{
		canvasWidth = width;
		canvasHeight = height;
		updateTransformScreenSize();
	}

	@Override
	public void updateHeadersVisibility( final boolean isVisibleX, final int width, final boolean isVisibleY, final int height )
	{
		headerWidth = isVisibleX ? width : 0;
		headerHeight = isVisibleY ? height : 0;
		updateTransformScreenSize();
	}

	private void updateTransformScreenSize()
	{
		synchronized ( transform )
		{
			transform.setScreenSize(
					Math.max( canvasWidth - headerWidth, 1 ),
					Math.max( canvasHeight - headerHeight, 1 ) );
			notifyListeners();
		}
	}

	@Override
	public void setTransformListener( final TransformListener< ScreenTransform > transformListener )
	{
		listener = transformListener;
	}

	@Override
	public String getHelpString()
	{
		return null;
	}

	@Override
	public void install( final TriggerBehaviourBindings bindings )
	{
		behaviours.install( bindings, "transform" );
	}

	@Override
	public void layoutChanged( final LineageTreeLayout layout )
	{
		synchronized ( transform )
		{
			boundXMin = layout.getCurrentLayoutMinX() - boundXLayoutBorder;
			boundXMax = layout.getCurrentLayoutMaxX() + boundXLayoutBorder;

			if ( boundXMax - boundXMin < MIN_SIBLINGS_ON_CANVAS )
			{
				final double c = (boundXMax + boundXMin) / 2;
				boundXMin = c - MIN_SIBLINGS_ON_CANVAS / 2;
				boundXMax = c + MIN_SIBLINGS_ON_CANVAS / 2;
			}

			maxSizeX = ( boundXMax - boundXMin ) * maxSizeFactorX;

			if ( stayFullyZoomedOut )
				zoomOutFullyX( transform );
			constrainTransform( transform );
			notifyListeners();
		}
	}

	public void setLayoutRangeY( final double layoutMinY, final double layoutMaxY )
	{
		boundYMin = layoutMinY - boundYLayoutBorder;
		boundYMax = layoutMaxY + boundYLayoutBorder;

		if ( boundYMax - boundYMin < MIN_TIMEPOINTS_ON_CANVAS )
		{
			final double c = (boundYMax + boundYMin) / 2;
			boundYMin = c - MIN_TIMEPOINTS_ON_CANVAS / 2;
			boundYMax = c + MIN_TIMEPOINTS_ON_CANVAS / 2;
		}

		maxSizeY = ( boundYMax - boundYMin ) * maxSizeFactorY;
	}

	/**
	 * notifies {@link #listener} that the current transform changed.
	 */
	private void notifyListeners()
	{
		if ( listener != null )
			listener.transformChanged( transform );
	}

	private void constrainTransform( final ScreenTransform transform )
	{
		ConstrainScreenTransform.constrainTransform(
				transform,
				MIN_SIBLINGS_ON_CANVAS, MIN_TIMEPOINTS_ON_CANVAS,
				maxSizeX, maxSizeY,
				boundXMin, boundXMax, boundYMin, boundYMax,
				borderRatioX, borderRatioY );
	}

	private void zoomOutFullyX( final ScreenTransform transform )
	{
		ConstrainScreenTransform.zoomOutFullyX(
				transform,
				maxSizeX,
				boundXMin, boundXMax,
				borderRatioX );
	}

	private boolean hasMinSizeX( final ScreenTransform transform )
	{
		return ConstrainScreenTransform.hasMinSizeX( transform, MIN_SIBLINGS_ON_CANVAS + EPSILON );
	}

	private boolean hasMinSizeY( final ScreenTransform transform )
	{
		return ConstrainScreenTransform.hasMinSizeY( transform, MIN_TIMEPOINTS_ON_CANVAS + EPSILON );
	}

	private boolean hasMaxSizeX( final ScreenTransform transform )
	{
		return ConstrainScreenTransform.hasMaxSizeX( transform, maxSizeX );
	}

	private boolean hasMaxSizeY( final ScreenTransform transform )
	{
		return ConstrainScreenTransform.hasMaxSizeY( transform, maxSizeY );
	}

	private class TranslateDragBehaviour implements DragBehaviour
	{
		/**
		 * Coordinates where mouse dragging started.
		 */
		private int oX, oY;

		final private ScreenTransform previousTransform = new ScreenTransform();

		private long previousTime = 0;

		private long dt = 0;

		@Override
		public void init( final int x, final int y )
		{
			final long t = System.currentTimeMillis();
			synchronized ( transform )
			{
				oX = x;
				oY = y;
				previousTransform.set( transform );
				previousTime = t;
				dt = 0;
			}
		}

		@Override
		public void drag( final int x, final int y )
		{
			final long t = System.currentTimeMillis();
			synchronized ( transform )
			{
				if ( t > previousTime )
				{
					previousTransform.set( transform );
					dt = t - previousTime;
					previousTime = t;
				}

				final int dX = oX - x;
				final int dY = oY - y;
				oX = x;
				oY = y;
				transform.shift( dX, dY );
				constrainTransform( transform );
				notifyListeners();
			}
		}

		@Override
		public void end( final int x, final int y )
		{
			if ( enableIntertialTranslate && dt > 0 )
			{
				synchronized ( transform )
				{
					animator = new InertialScreenTransformAnimator( previousTransform, transform, dt, 400 );
				}
				runAnimation();
			}
		}
	}

	private static enum ScrollAxis
	{
		X, Y, XY
	}

	private class ZoomScrollBehaviour implements ScrollBehaviour
	{
		private final ScrollAxis axis;

		private final ScreenTransform previousTransform = new ScreenTransform();

		public ZoomScrollBehaviour( final ScrollAxis axis )
		{
			this.axis = axis;
		}

		@Override
		public void scroll( final double wheelRotation, final boolean isHorizontal, final int wx, final int wy )
		{
			if ( isHorizontal )
				return;

			final int x = wx - headerWidth;
			final int y = wy - headerHeight;

			final boolean zoomIn = wheelRotation < 0;
			double dScale = 1.0 + Math.abs( wheelRotation ) * zoomScrollSensitivity;
			if ( zoomIn )
				dScale = 1.0 / dScale;

			synchronized ( transform )
			{
				previousTransform.set( transform );

				if ( axis == ScrollAxis.X || axis == ScrollAxis.XY )
				{
					if ( zoomIn )
					{
						if ( !hasMinSizeX( transform ) )
							transform.zoomX( dScale, x );
					}
					else
					{
						if ( !hasMaxSizeX( transform ) )
							transform.zoomX( dScale, x );
					}
				}

				if ( axis == ScrollAxis.Y || axis == ScrollAxis.XY )
				{
					if ( zoomIn )
					{
						if ( !hasMinSizeY( transform ) )
							transform.zoomY( dScale, y );
					}
					else
					{
						if ( !hasMaxSizeY( transform ) )
							transform.zoomY( dScale, y );
					}
				}

				stayFullyZoomedOut = hasMaxSizeX( transform );

				constrainTransform( transform );
				ConstrainScreenTransform.removeJitter( transform, previousTransform );
				if ( !transform.equals( previousTransform ) )
				{
					if ( enableInertialZoom )
						animator = new InertialScreenTransformAnimator( previousTransform, transform, 50, 400 );
					notifyListeners();
				}
			}

			if ( enableInertialZoom )
				runAnimation();
		}
	}

	private class TranslateScrollBehaviour implements ScrollBehaviour
	{
		private final ScreenTransform previousTransform = new ScreenTransform();

		@Override
		public void scroll( final double wheelRotation, final boolean isHorizontal, final int x, final int y )
		{
			synchronized ( transform )
			{
				previousTransform.set( transform );
				final double d = wheelRotation * 15;
				if ( isHorizontal )
					transform.shiftX( d );
				else
					transform.shiftY( d );
				constrainTransform( transform );

				ConstrainScreenTransform.removeJitter( transform, previousTransform );
				if ( !transform.equals( previousTransform ) )
					notifyListeners();
			}
		}
	}

	private final ScreenTransform tstart = new ScreenTransform();

	private final ScreenTransform tend = new ScreenTransform();

	public void centerOn( final double lx, final double ly )
	{
		synchronized( transform )
		{
			tstart.set( transform );
		}

		final double minX = tstart.getMinX();
		final double maxX = tstart.getMaxX();
		final double cx = ( maxX + minX ) / 2;
		final double dx = lx - cx;

		final double minY = tstart.getMinY();
		final double maxY = tstart.getMaxY();
		final double cy = ( maxY + minY ) / 2;
		final double dy = ly - cy;

		tend.set( tstart );
		tend.shiftLayoutX( dx );
		tend.shiftLayoutY( dy );
		constrainTransform( tend );

		ConstrainScreenTransform.removeJitter( tend, tstart );
		if ( !tend.equals( tstart ) )
		{
			animator = new InterpolateScreenTransformAnimator( tstart, tend, 200 );
			runAnimation();
		}
	}

	private void animate()
	{
		final long t = System.currentTimeMillis();
		final ScreenTransform c = animator.getCurrent( t );
		if ( stayFullyZoomedOut )
			zoomOutFullyX( c );
		constrainTransform( c );
		synchronized ( transform )
		{
			transform.set( c );
			notifyListeners();
		}
	}

	private synchronized void runAnimation()
	{
		if ( currentTimerTask != null )
			currentTimerTask.cancel();
		timer.purge();
		currentTimerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				if ( null == animator || animator.isComplete() )
				{
					cancel();
					currentTimerTask = null;
				}
				else
				{
					animate();
				}
			}
		};
		timer.schedule( currentTimerTask, 0, INERTIAL_ANIMATION_PERIOD );
	}
}
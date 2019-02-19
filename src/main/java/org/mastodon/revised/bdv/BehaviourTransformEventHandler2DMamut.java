package org.mastodon.revised.bdv;

import org.scijava.ui.behaviour.ClickBehaviour;
import org.scijava.ui.behaviour.DragBehaviour;
import org.scijava.ui.behaviour.ScrollBehaviour;
import org.scijava.ui.behaviour.util.Behaviours;

import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.ui.TransformListener;

/**
 * Adapted from BehaviourTransformEventHandlerPlanar in BDV-vistools, by Tobias
 * Pietzsch.
 */
public class BehaviourTransformEventHandler2DMamut implements BehaviourTransformEventHandlerMamut
{

	private static final String DRAG_TRANSLATE = "2d drag translate";
	private static final String ZOOM_NORMAL = "2d scroll zoom";
	private static final String DRAG_ROTATE = "2d drag rotate";
	private static final String SCROLL_ROTATE = "2d scroll rotate";
	private static final String SCROLL_TRANSLATE = "2d scroll translate";
	private static final String ROTATE_LEFT = "2d rotate left";
	private static final String ROTATE_RIGHT = "2d rotate right";
	private static final String KEY_ZOOM_IN = "2d zoom in";
	private static final String KEY_ZOOM_OUT = "2d zoom out";
	
	private static final String[] DRAG_TRANSLATE_KEYS = new String[] { "button2", "button3" };
	private static final String[] ZOOM_NORMAL_KEYS = new String[] { "meta scroll", "ctrl shift scroll" };
	private static final String[] DRAG_ROTATE_KEYS = new String[] { "button1" };
	private static final String[] SCROLL_TRANSLATE_KEYS = new String[] {"not mapped"};
	private static final String[] ROTATE_LEFT_KEYS = new String[] { "LEFT" };
	private static final String[] ROTATE_RIGHT_KEYS = new String[] { "RIGHT" };
	private static final String[] ROTATE_LEFT_FAST_KEYS = new String[] { "shift LEFT" };
	private static final String[] ROTATE_RIGHT_FAST_KEYS = new String[] { "shift RIGHT" };
	private static final String[] ROTATE_LEFT_SLOW_KEYS = new String[] { "ctrl LEFT" };
	private static final String[] ROTATE_RIGHT_SLOW_KEYS = new String[] { "ctrl RIGHT" };
	
	private static final String[] KEY_ZOOM_IN_KEYS = new String[] { "UP" };
	private static final String[] KEY_ZOOM_OUT_KEYS = new String[] { "DOWN" };
	private static final String[] KEY_ZOOM_IN_FAST_KEYS = new String[] { "shift UP" };
	private static final String[] KEY_ZOOM_OUT_FAST_KEYS = new String[] { "shift DOWN" };
	private static final String[] KEY_ZOOM_IN_SLOW_KEYS = new String[] { "ctrl UP" };
	private static final String[] KEY_ZOOM_OUT_SLOW_KEYS = new String[] { "ctrl DOWN" };

	private static final double[] SPEED = { 1.0, 10.0, 0.1 };
	private static final String[] SPEED_NAME = { "", " fast", " slow" };
	private static final String[] SPEED_MOD = { "", "shift ", "ctrl " };


	private final DragTranslate dragTranslate;

	private final Zoom zoom;

	private final ScrollTranslate scrollTranslate;

	private final DragRotate dragRotate;

	private final ScrollRotate scrollRotateNormal;

	private final ScrollRotate scrollRotateFast;

	private final ScrollRotate scrollRotateSlow;

	private final KeyRotate keyRotateLeftNormal;

	private final KeyRotate keyRotateLeftFast;

	private final KeyRotate keyRotateLeftSlow;

	private final KeyRotate keyRotateRightNormal;

	private final KeyRotate keyRotateRightFast;

	private final KeyRotate keyRotateRightSlow;

	private final KeyZoom keyZoomInNormal;

	private final KeyZoom keyZoomInFast;

	private final KeyZoom keyZoomInSlow;

	private final KeyZoom keyZoomOutNormal;

	private final KeyZoom keyZoomOutFast;

	private final KeyZoom keyZoomOutSlow;

	/**
	 * Current source to screen transform.
	 */
	private final AffineTransform3D affine = new AffineTransform3D();

	/**
	 * Whom to notify when the {@link #affine current transform} is changed.
	 */
	private TransformListener< AffineTransform3D > listener;

	/**
	 * Copy of {@link #affine current transform} when mouse dragging started.
	 */
	private final AffineTransform3D affineDragStart = new AffineTransform3D();

	/**
	 * Coordinates where mouse dragging started.
	 */
	private double oX;

	private double oY;

	/**
	 * The screen size of the canvas (the component displaying the image and
	 * generating mouse events).
	 */
	private int canvasW = 1;

	private int canvasH = 1;

	/**
	 * Screen coordinates to keep centered while zooming or rotating with the
	 * keyboard. These are set to <em>(canvasW/2, canvasH/2)</em>
	 */
	private int centerX = 0;

	private int centerY = 0;

	public BehaviourTransformEventHandler2DMamut( final TransformListener< AffineTransform3D > listener )
	{
		this.listener = listener;

		dragTranslate = new DragTranslate();
		zoom = new Zoom();
		scrollTranslate = new ScrollTranslate();
		dragRotate = new DragRotate();

		scrollRotateNormal = new ScrollRotate( 2 * SPEED[ 0 ] );
		scrollRotateFast = new ScrollRotate( 2 * SPEED[ 1 ] );
		scrollRotateSlow = new ScrollRotate( 2 * SPEED[ 2 ] );

		keyRotateLeftNormal = new KeyRotate( SPEED[ 0 ] );
		keyRotateLeftFast = new KeyRotate( SPEED[ 1 ] );
		keyRotateLeftSlow = new KeyRotate( SPEED[ 2 ] );
		keyRotateRightNormal = new KeyRotate( -SPEED[ 0 ] );
		keyRotateRightFast = new KeyRotate( -SPEED[ 1 ] );
		keyRotateRightSlow = new KeyRotate( -SPEED[ 2 ] );

		keyZoomInNormal = new KeyZoom( SPEED[ 0 ] );
		keyZoomInFast = new KeyZoom( SPEED[ 1 ] );
		keyZoomInSlow = new KeyZoom( SPEED[ 2 ] );
		keyZoomOutNormal = new KeyZoom( SPEED[ 0 ] );
		keyZoomOutFast = new KeyZoom( SPEED[ 1 ] );
		keyZoomOutSlow = new KeyZoom( SPEED[ 2 ] );
	}

	@Override
	public void install( final Behaviours behaviours )
	{
		behaviours.behaviour( dragTranslate, DRAG_TRANSLATE, DRAG_TRANSLATE_KEYS );
		behaviours.behaviour( zoom, ZOOM_NORMAL, ZOOM_NORMAL_KEYS );
		behaviours.behaviour( zoom, ZOOM_NORMAL, ZOOM_NORMAL_KEYS );

		behaviours.namedBehaviour( selectRotationAxisXBehaviour, SELECT_AXIS_X_KEYS );
		behaviours.namedBehaviour( selectRotationAxisYBehaviour, SELECT_AXIS_Y_KEYS );
		behaviours.namedBehaviour( selectRotationAxisZBehaviour, SELECT_AXIS_Z_KEYS );
		behaviours.namedBehaviour( dragRotateBehaviour, DRAG_ROTATE_KEYS );
		behaviours.namedBehaviour( dragRotateFastBehaviour, DRAG_ROTATE_FAST_KEYS );
		behaviours.namedBehaviour( dragRotateSlowBehaviour, DRAG_ROTATE_SLOW_KEYS );
		behaviours.namedBehaviour( translateZBehaviour, SCROLL_Z_KEYS );
		behaviours.namedBehaviour( translateZFastBehaviour, SCROLL_Z_FAST_KEYS );
		behaviours.namedBehaviour( translateZSlowBehaviour, SCROLL_Z_SLOW_KEYS );
		behaviours.namedBehaviour( rotateLeftBehaviour, ROTATE_LEFT_KEYS );
		behaviours.namedBehaviour( rotateLeftFastBehaviour, ROTATE_LEFT_FAST_KEYS );
		behaviours.namedBehaviour( rotateLeftSlowBehaviour, ROTATE_LEFT_SLOW_KEYS );
		behaviours.namedBehaviour( rotateRightBehaviour, ROTATE_RIGHT_KEYS );
		behaviours.namedBehaviour( rotateRightFastBehaviour, ROTATE_RIGHT_FAST_KEYS );
		behaviours.namedBehaviour( rotateRightSlowBehaviour, ROTATE_RIGHT_SLOW_KEYS );
		behaviours.namedBehaviour( keyZoomInBehaviour, KEY_ZOOM_IN_KEYS );
		behaviours.namedBehaviour( keyZoomInFastBehaviour, KEY_ZOOM_IN_FAST_KEYS );
		behaviours.namedBehaviour( keyZoomInSlowBehaviour, KEY_ZOOM_IN_SLOW_KEYS );
		behaviours.namedBehaviour( keyZoomOutBehaviour, KEY_ZOOM_OUT_KEYS );
		behaviours.namedBehaviour( keyZoomOutFastBehaviour, KEY_ZOOM_OUT_FAST_KEYS );
		behaviours.namedBehaviour( keyZoomOutSlowBehaviour, KEY_ZOOM_OUT_SLOW_KEYS );
		behaviours.namedBehaviour( keyForwardZBehaviour, KEY_FORWARD_Z_KEYS );
		behaviours.namedBehaviour( keyForwardZFastBehaviour, KEY_FORWARD_Z_SLOW_KEYS );
		behaviours.namedBehaviour( keyForwardZSlowBehaviour, KEY_FORWARD_Z_FAST_KEYS );
		behaviours.namedBehaviour( keyBackwardZBehaviour, KEY_BACKWARD_Z_KEYS );
		behaviours.namedBehaviour( keyBackwardZFastBehaviour, KEY_BACKWARD_Z_FAST_KEYS );
		behaviours.namedBehaviour( keyBackwardZSlowBehaviour, KEY_BACKWARD_Z_SLOW_KEYS );
	}


	@Override
	public AffineTransform3D getTransform()
	{
		synchronized ( affine )
		{
			return affine.copy();
		}
	}

	@Override
	public void setTransform( final AffineTransform3D transform )
	{
		synchronized ( affine )
		{
			affine.set( transform );
		}
	}

	@Override
	public void setCanvasSize( final int width, final int height, final boolean updateTransform )
	{
		if ( updateTransform )
		{
			synchronized ( affine )
			{
				affine.set( affine.get( 0, 3 ) - canvasW / 2, 0, 3 );
				affine.set( affine.get( 1, 3 ) - canvasH / 2, 1, 3 );
				affine.scale( ( double ) width / canvasW );
				affine.set( affine.get( 0, 3 ) + width / 2, 0, 3 );
				affine.set( affine.get( 1, 3 ) + height / 2, 1, 3 );
				notifyListener();
			}
		}
		canvasW = width;
		canvasH = height;
		centerX = width / 2;
		centerY = height / 2;
	}

	@Override
	public void setTransformListener( final TransformListener< AffineTransform3D > transformListener )
	{
		listener = transformListener;
	}

	@Override
	public String getHelpString()
	{
		return null;
	}

	/**
	 * notifies {@link #listener} that the current transform changed.
	 */
	private void notifyListener()
	{
		if ( listener != null )
			listener.transformChanged( affine );
	}

	/**
	 * One step of rotation (radian).
	 */
	final private static double step = Math.PI / 180;

	private void scale( final double s, final double x, final double y )
	{
		// center shift
		affine.set( affine.get( 0, 3 ) - x, 0, 3 );
		affine.set( affine.get( 1, 3 ) - y, 1, 3 );

		// scale
		affine.scale( s );

		// center un-shift
		affine.set( affine.get( 0, 3 ) + x, 0, 3 );
		affine.set( affine.get( 1, 3 ) + y, 1, 3 );
	}

	/**
	 * Rotate by d radians around axis. Keep screen coordinates (
	 * {@link #centerX}, {@link #centerY}) fixed.
	 */
	private void rotate( final int axis, final double d )
	{
		// center shift
		affine.set( affine.get( 0, 3 ) - centerX, 0, 3 );
		affine.set( affine.get( 1, 3 ) - centerY, 1, 3 );

		// rotate
		affine.rotate( axis, d );

		// center un-shift
		affine.set( affine.get( 0, 3 ) + centerX, 0, 3 );
		affine.set( affine.get( 1, 3 ) + centerY, 1, 3 );
	}

	private class DragRotate implements DragBehaviour
	{
		@Override
		public void init( final int x, final int y )
		{
			synchronized ( affine )
			{
				oX = x;
				oY = y;
				affineDragStart.set( affine );
			}
		}

		@Override
		public void drag( final int x, final int y )
		{
			synchronized ( affine )
			{
				final double dX = x - centerX;
				final double dY = y - centerY;
				final double odX = oX - centerX;
				final double odY = oY - centerY;
				final double theta = Math.atan2( dY, dX ) - Math.atan2( odY, odX );

				affine.set( affineDragStart );
				rotate( 2, theta );
				notifyListener();
			}
		}

		@Override
		public void end( final int x, final int y )
		{}
	}

	private class ScrollRotate implements ScrollBehaviour
	{
		private final double speed;

		public ScrollRotate( final double speed )
		{
			this.speed = speed;
		}

		@Override
		public void scroll( final double wheelRotation, final boolean isHorizontal, final int x, final int y )
		{
			synchronized ( affine )
			{
				final double theta = speed * wheelRotation * Math.PI / 180.0;

				// center shift
				affine.set( affine.get( 0, 3 ) - x, 0, 3 );
				affine.set( affine.get( 1, 3 ) - y, 1, 3 );

				affine.rotate( 2, theta );

				// center un-shift
				affine.set( affine.get( 0, 3 ) + x, 0, 3 );
				affine.set( affine.get( 1, 3 ) + y, 1, 3 );

				notifyListener();
			}
		}
	}

	private class DragTranslate implements DragBehaviour
	{
		@Override
		public void init( final int x, final int y )
		{
			synchronized ( affine )
			{
				oX = x;
				oY = y;
				affineDragStart.set( affine );
			}
		}

		@Override
		public void drag( final int x, final int y )
		{
			synchronized ( affine )
			{
				final double dX = oX - x;
				final double dY = oY - y;

				affine.set( affineDragStart );
				affine.set( affine.get( 0, 3 ) - dX, 0, 3 );
				affine.set( affine.get( 1, 3 ) - dY, 1, 3 );
				notifyListener();
			}
		}

		@Override
		public void end( final int x, final int y )
		{}
	}

	private class ScrollTranslate implements ScrollBehaviour
	{
		@Override
		public void scroll( final double wheelRotation, final boolean isHorizontal, final int x, final int y )
		{
			synchronized ( affine )
			{
				final double d = -wheelRotation * 10;
				if ( isHorizontal )
					affine.translate( d, 0, 0 );
				else
					affine.translate( 0, d, 0 );
				notifyListener();
			}
		}
	}

	private class Zoom implements ScrollBehaviour
	{

		private final double speed = 1.0;

		@Override
		public void scroll( final double wheelRotation, final boolean isHorizontal, final int x, final int y )
		{
			synchronized ( affine )
			{
				final double s = speed * wheelRotation;
				final double dScale = 1.0 + 0.05;
				if ( s > 0 )
					scale( 1.0 / dScale, x, y );
				else
					scale( dScale, x, y );
				notifyListener();
			}
		}
	}

	private class KeyRotate implements ClickBehaviour
	{
		private final double speed;

		public KeyRotate( final double speed )
		{
			this.speed = speed;
		}

		@Override
		public void click( final int x, final int y )
		{
			synchronized ( affine )
			{
				rotate( 2, step * speed );
				notifyListener();
			}
		}
	}

	private class KeyZoom implements ClickBehaviour
	{
		private final double dScale;

		public KeyZoom( final double speed )
		{
			if ( speed > 0 )
				dScale = 1.0 + 0.1 * speed;
			else
				dScale = 1.0 / ( 1.0 - 0.1 * speed );
		}

		@Override
		public void click( final int x, final int y )
		{
			synchronized ( affine )
			{
				scale( dScale, centerX, centerY );
				notifyListener();
			}
		}
	}
}

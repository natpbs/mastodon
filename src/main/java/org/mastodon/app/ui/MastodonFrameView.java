package org.mastodon.app.ui;

import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jdom2.Element;
import org.mastodon.app.MastodonAppModel;
import org.mastodon.app.MastodonView;
import org.mastodon.app.ViewGraph;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.ref.AbstractListenableEdge;
import org.mastodon.revised.model.AbstractSpot;
import org.mastodon.revised.ui.keymap.Keymap;
import org.mastodon.revised.ui.keymap.Keymap.UpdateListener;
import org.scijava.ui.behaviour.util.Actions;
import org.scijava.ui.behaviour.util.Behaviours;
import org.scijava.ui.behaviour.util.WrappedActionMap;
import org.scijava.ui.behaviour.util.WrappedInputMap;

/**
 * A {@link MastodonView} that is displayed in a {@link ViewFrame} (instead of
 * just a panel, for instance).
 *
 * @param <M>
 * @param <VG>
 * @param <MV>
 *            model vertex type
 * @param <ME>
 *            model edge type
 * @param <V>
 *            view vertex type
 * @param <E>
 *            view edge type
 *
 * @author Tobias Pietzsch
 */
public class MastodonFrameView<
		M extends MastodonAppModel< ?, MV, ME >,
		VG extends ViewGraph< MV, ME, V, E >,
		MV extends AbstractSpot< MV, ME, ?, ?, ? >,
		ME extends AbstractListenableEdge< ME, MV, ?, ? >,
		V extends Vertex< E >,
		E extends Edge< V > >
	extends MastodonView< M, VG, MV, ME, V, E >
{

	public static final String VIEW_TYPE_TAG = "Type";
	public static final String MASTODON_FRAME_VIEW_TAG = "MastodonFrameView";
	private static final String FRAME_POSITION_TAG = "FramePosition";
	private static final String X_ATTRIBUTE = "x";
	private static final String Y_ATTRIBUTE = "y";
	private static final String WIDTH_ATTRIBUTE = "width";
	private static final String HEIGHT_ATTRIBUTE = "height";

	protected ViewFrame frame;

	protected final String[] keyConfigContexts;

	protected Actions viewActions;

	protected Behaviours viewBehaviours;

	public MastodonFrameView(
			final M appModel,
			final VG viewGraph,
			final String[] keyConfigContexts )
	{
		super( appModel, viewGraph );

		final Set< String > c = new LinkedHashSet<>( Arrays.asList( appModel.getKeyConfigContexts() ) );
		c.addAll( Arrays.asList( keyConfigContexts ) );
		this.keyConfigContexts = c.toArray( new String[] {} );
	}

	public ViewFrame getFrame()
	{
		return frame;
	}

	protected void setFrame( final ViewFrame frame )
	{
		frame.addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final WindowEvent e )
			{
				close();
			}
		} );
		this.frame = frame;

		final Actions globalActions = appModel.getGlobalActions();
		if ( globalActions != null )
		{
			frame.keybindings.addActionMap( "global", new WrappedActionMap( globalActions.getActionMap() ) );
			frame.keybindings.addInputMap( "global", new WrappedInputMap( globalActions.getInputMap() ) );
		}

		final Actions pluginActions = appModel.getPlugins().getPluginActions();
		if ( pluginActions != null )
		{
			frame.keybindings.addActionMap( "plugin", new WrappedActionMap( pluginActions.getActionMap() ) );
			frame.keybindings.addInputMap( "plugin", new WrappedInputMap( pluginActions.getInputMap() ) );
		}

		final Actions appActions = appModel.getAppActions();
		frame.keybindings.addActionMap( "app", new WrappedActionMap( appActions.getActionMap() ) );
		frame.keybindings.addInputMap( "app", new WrappedInputMap( appActions.getInputMap() ) );

		final Keymap keymap = appModel.getKeymap();

		viewActions = new Actions( keymap.getConfig(), getKeyConfigContexts() );
		viewActions.install( frame.keybindings, "view" );

		viewBehaviours = new Behaviours( keymap.getConfig(), getKeyConfigContexts() );
		viewBehaviours.install( frame.triggerbindings, "view" );

		final UpdateListener updateListener = () -> {
			viewBehaviours.updateKeyConfig( keymap.getConfig() );
			viewActions.updateKeyConfig( keymap.getConfig() );
		};
		keymap.updateListeners().add( updateListener );
		onClose( () -> keymap.updateListeners().remove( updateListener ) );
	}

	Keymap getKeymap()
	{
		return appModel.getKeymap();
	}

	M getAppModel()
	{
		return appModel;
	}

	String[] getKeyConfigContexts()
	{
		return keyConfigContexts;
	}

	/**
	 * Stores the frame position in a new XML element.
	 * 
	 * @return the element.
	 */
	public Element toXml()
	{
		final Element element = new Element( MASTODON_FRAME_VIEW_TAG );
		final Rectangle bounds = getFrame().getBounds();
		final Element position = new Element( FRAME_POSITION_TAG);
		position.setAttribute( X_ATTRIBUTE, Integer.toString( ( int ) bounds.getMinX() ) );
		position.setAttribute( Y_ATTRIBUTE, Integer.toString( ( int ) bounds.getMinY() ) );
		position.setAttribute( WIDTH_ATTRIBUTE, Integer.toString( ( int ) bounds.getWidth() ) );
		position.setAttribute( HEIGHT_ATTRIBUTE, Integer.toString( ( int ) bounds.getHeight() ) );
		element.addContent( position );
		return element;
	}

	/**
	 * Restores the frame position from the specified element.
	 * 
	 * @param element
	 *            the element.
	 */
	public void restoreFromXml( final Element element )
	{
		final Element position = element.getChild( FRAME_POSITION_TAG );
		if ( null == position )
			return;
		final ViewFrame frame = getFrame();
		if ( null == frame )
			return;
		try
		{
			final int x = Integer.parseInt( position.getAttributeValue( X_ATTRIBUTE ) );
			final int y = Integer.parseInt( position.getAttributeValue( Y_ATTRIBUTE ) );
			final int width = Integer.parseInt( position.getAttributeValue( WIDTH_ATTRIBUTE ) );
			final int height = Integer.parseInt( position.getAttributeValue( HEIGHT_ATTRIBUTE ) );
			frame.setBounds( new Rectangle( x, y, width, height ) );
		}
		catch ( final NumberFormatException nfe )
		{}
	}
}

package org.mastodon.revised.mamut;

import static org.mastodon.app.ui.ViewMenuBuilder.item;
import static org.mastodon.app.ui.ViewMenuBuilder.separator;
import static org.mastodon.revised.mamut.MamutMenuBuilder.colorMenu;
import static org.mastodon.revised.mamut.MamutMenuBuilder.editMenu;
import static org.mastodon.revised.mamut.MamutMenuBuilder.viewMenu;

import javax.swing.ActionMap;

import org.jdom2.Element;
import org.mastodon.app.ui.MastodonFrameViewActions;
import org.mastodon.app.ui.ViewMenu;
import org.mastodon.app.ui.ViewMenuBuilder.JMenuHandle;
import org.mastodon.model.AutoNavigateFocusModel;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.ModelGraphTrackSchemeProperties;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.model.tag.TagSetModel;
import org.mastodon.revised.model.tag.TagSetStructure.TagSet;
import org.mastodon.revised.trackscheme.ScreenTransform;
import org.mastodon.revised.trackscheme.TrackSchemeContextListener;
import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.trackscheme.display.EditFocusVertexLabelAction;
import org.mastodon.revised.trackscheme.display.ToggleLinkBehaviour;
import org.mastodon.revised.trackscheme.display.TrackSchemeFrame;
import org.mastodon.revised.trackscheme.display.TrackSchemeNavigationActions;
import org.mastodon.revised.trackscheme.display.TrackSchemeOptions;
import org.mastodon.revised.trackscheme.display.TrackSchemePanel;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle;
import org.mastodon.revised.ui.EditTagActions;
import org.mastodon.revised.ui.FocusActions;
import org.mastodon.revised.ui.HighlightBehaviours;
import org.mastodon.revised.ui.SelectionActions;
import org.mastodon.revised.ui.coloring.ColoringMenu;
import org.mastodon.revised.ui.coloring.ColoringModel;
import org.mastodon.revised.ui.coloring.GraphColorGeneratorAdapter;
import org.mastodon.revised.ui.coloring.TagSetGraphColorGenerator;
import org.mastodon.views.context.ContextChooser;
import org.scijava.ui.behaviour.KeyPressedManager;

import mpicbg.spim.data.XmlHelpers;

public class MamutViewTrackScheme extends MamutView< TrackSchemeGraph< Spot, Link >, TrackSchemeVertex, TrackSchemeEdge >
{

	public static final String TRACKSCHEME_TYPE_VALUE = "TrackScheme";
	private static final String VIEWER_TRANSFORM_TAG = "ViewerTransform";
	private static final String MIN_X_TAG = "MinX";
	private static final String MAX_X_TAG = "MaxX";
	private static final String MIN_Y_TAG = "MinY";
	private static final String MAX_Y_TAG = "MaxY";
	private static final String SCREEN_WIDTH_TAG = "ScreenWidth";
	private static final String SCREEN_HEIGHT_TAG = "ScreenHeight";
	private static final String COLORING_TAG = "Coloring";
	private static final String NO_COLORING_TAG = "NoColoring";
	private static final String TAG_SET_TAG = "TagSet";

	private final ContextChooser< Spot > contextChooser;

	private final TrackSchemePanel trackschemePanel;

	private final ColoringModel coloringModel;

	public MamutViewTrackScheme( final MamutAppModel appModel )
	{
		super( appModel,
				new TrackSchemeGraph<>(
						appModel.getModel().getGraph(),
						appModel.getModel().getGraphIdBimap(),
						new ModelGraphTrackSchemeProperties( appModel.getModel().getGraph() ),
						appModel.getModel().getGraph().getLock() ),
				new String[] { KeyConfigContexts.TRACKSCHEME } );

		/*
		 * TrackScheme ContextChooser
		 */
		final TrackSchemeContextListener< Spot > contextListener = new TrackSchemeContextListener<>( viewGraph );
		contextChooser = new ContextChooser<>( contextListener );

		final KeyPressedManager keyPressedManager = appModel.getKeyPressedManager();
		final Model model = appModel.getModel();

		/*
		 * show TrackSchemeFrame
		 */
		final TrackSchemeStyle forwardDefaultStyle = appModel.getTrackSchemeStyleManager().getForwardDefaultStyle();
		final GraphColorGeneratorAdapter< Spot, Link, TrackSchemeVertex, TrackSchemeEdge > coloring = new GraphColorGeneratorAdapter<>( viewGraph.getVertexMap(), viewGraph.getEdgeMap() );
		final TrackSchemeOptions options = TrackSchemeOptions.options()
				.shareKeyPressedEvents( keyPressedManager )
				.style( forwardDefaultStyle )
				.graphColorGenerator( coloring );
		final AutoNavigateFocusModel< TrackSchemeVertex, TrackSchemeEdge > navigateFocusModel = new AutoNavigateFocusModel<>( focusModel, navigationHandler );
		final TrackSchemeFrame frame = new TrackSchemeFrame(
				viewGraph,
				highlightModel,
				navigateFocusModel,
				timepointModel,
				selectionModel,
				navigationHandler,
				model,
				groupHandle,
				contextChooser,
				options );

		this.trackschemePanel = frame.getTrackschemePanel();

		trackschemePanel.setTimepointRange( appModel.getMinTimepoint(), appModel.getMaxTimepoint() );
		trackschemePanel.graphChanged();
		contextListener.setContextListener( trackschemePanel );

		final TrackSchemeStyle.UpdateListener updateListener = () -> trackschemePanel.repaint();
		forwardDefaultStyle.updateListeners().add( updateListener );
		onClose( () -> forwardDefaultStyle.updateListeners().remove( updateListener ) );

		setFrame( frame );
		frame.setLocationByPlatform( true );
		frame.setVisible( true );

		MastodonFrameViewActions.install( viewActions, this );
		HighlightBehaviours.install( viewBehaviours, viewGraph, viewGraph.getLock(), viewGraph, highlightModel, model );
		ToggleLinkBehaviour.install( viewBehaviours, trackschemePanel,	viewGraph, viewGraph.getLock(),	viewGraph, model );
		EditFocusVertexLabelAction.install( viewActions, trackschemePanel, focusModel, model );
		FocusActions.install( viewActions, viewGraph, viewGraph.getLock(), navigateFocusModel, selectionModel );
		EditTagActions.install( viewActions, frame.getKeybindings(), frame.getTriggerbindings(), model.getTagSetModel(), appModel.getSelectionModel(), trackschemePanel, trackschemePanel.getDisplay(), model );
		viewActions.runnableAction( () -> System.out.println( model.getTagSetModel() ), "output tags", "U" ); // DEBUG TODO: REMOVE

		// TODO Let the user choose between the two selection/focus modes.
		trackschemePanel.getNavigationActions().install( viewActions, TrackSchemeNavigationActions.NavigatorEtiquette.FINDER_LIKE );
		trackschemePanel.getNavigationBehaviours().install( viewBehaviours );
		trackschemePanel.getTransformEventHandler().install( viewBehaviours );

		final ViewMenu menu = new ViewMenu( this );
		final ActionMap actionMap = frame.getKeybindings().getConcatenatedActionMap();

		final JMenuHandle tagSetColoringMenuHandle = new JMenuHandle();

		MainWindow.addMenus( menu, actionMap );
		MamutMenuBuilder.build( menu, actionMap,
				viewMenu(
						colorMenu( tagSetColoringMenuHandle ),
						separator(),
						item( MastodonFrameViewActions.TOGGLE_SETTINGS_PANEL )
				),
				editMenu(
						item( UndoActions.UNDO ),
						item( UndoActions.REDO ),
						separator(),
						item( SelectionActions.DELETE_SELECTION ),
						item( SelectionActions.SELECT_WHOLE_TRACK ),
						item( SelectionActions.SELECT_TRACK_DOWNWARD ),
						item( SelectionActions.SELECT_TRACK_UPWARD ),
						separator(),
						item( TrackSchemeNavigationActions.SELECT_NAVIGATE_CHILD ),
						item( TrackSchemeNavigationActions.SELECT_NAVIGATE_PARENT ),
						item( TrackSchemeNavigationActions.SELECT_NAVIGATE_LEFT ),
						item( TrackSchemeNavigationActions.SELECT_NAVIGATE_RIGHT ),
						separator(),
						item( TrackSchemeNavigationActions.NAVIGATE_CHILD ),
						item( TrackSchemeNavigationActions.NAVIGATE_PARENT ),
						item( TrackSchemeNavigationActions.NAVIGATE_LEFT ),
						item( TrackSchemeNavigationActions.NAVIGATE_RIGHT ),
						separator(),
						item( EditFocusVertexLabelAction.EDIT_FOCUS_LABEL )
				)
		);
		appModel.getPlugins().addMenus( menu );

		final TagSetModel< Spot, Link > tagSetModel = appModel.getModel().getTagSetModel();
		this.coloringModel = new ColoringModel( tagSetModel );
		tagSetModel.listeners().add( coloringModel );
		onClose( () -> tagSetModel.listeners().remove( coloringModel ) );

		final ColoringMenu coloringMenu = new ColoringMenu( tagSetColoringMenuHandle.getMenu(), coloringModel );
		tagSetModel.listeners().add( coloringMenu );
		onClose( () -> tagSetModel.listeners().remove( coloringMenu ) );

		final ColoringModel.ColoringChangedListener coloringChangedListener = () ->
		{
			if ( coloringModel.noColoring() )
				coloring.setColorGenerator( null );
			else if ( coloringModel.getTagSet() != null)
				coloring.setColorGenerator( new TagSetGraphColorGenerator<>( tagSetModel, coloringModel.getTagSet() ) );
			trackschemePanel.entitiesAttributesChanged();
		};
		coloringModel.listeners().add( coloringChangedListener );

		trackschemePanel.repaint();
	}

	public ContextChooser< Spot > getContextChooser()
	{
		return contextChooser;
	}

	@Override
	public Element toXml()
	{
		final Element element = super.toXml();
		element.setAttribute( VIEW_TYPE_TAG, TRACKSCHEME_TYPE_VALUE );

		// View transform.
		final ScreenTransform t = trackschemePanel.getTransformEventHandler().getTransform();
		final double minX = t.getMinX();
		final double maxX = t.getMaxX();
		final double minY = t.getMinY();
		final double maxY = t.getMaxY();
		final int screenWidth = t.getScreenWidth();
		final int screenHeight = t.getScreenHeight();
		final Element screenTransformEl = new Element( VIEWER_TRANSFORM_TAG );
		screenTransformEl.addContent( XmlHelpers.doubleElement( MIN_X_TAG, minX ) );
		screenTransformEl.addContent( XmlHelpers.doubleElement( MAX_X_TAG, maxX ) );
		screenTransformEl.addContent( XmlHelpers.doubleElement( MIN_Y_TAG, minY ) );
		screenTransformEl.addContent( XmlHelpers.doubleElement( MAX_Y_TAG, maxY ) );
		screenTransformEl.addContent( XmlHelpers.intElement( SCREEN_WIDTH_TAG, screenWidth ) );
		screenTransformEl.addContent( XmlHelpers.intElement( SCREEN_HEIGHT_TAG, screenHeight ) );
		element.addContent( screenTransformEl );

		// Coloring.
		final Element coloringEl = new Element( COLORING_TAG );
		final boolean noColoring = coloringModel.noColoring();
		coloringEl.addContent( XmlHelpers.booleanElement( NO_COLORING_TAG, noColoring ) );
		if ( !noColoring )
			if ( coloringModel.getTagSet() != null )
				coloringEl.addContent( XmlHelpers.textElement( TAG_SET_TAG, coloringModel.getTagSet().getName() ) );
		element.addContent( coloringEl );

		return element;
	}

	@Override
	public void restoreFromXml( final Element element )
	{
		super.restoreFromXml( element );

		// Screen transform.
		final Element screenTransformEl = element.getChild( VIEWER_TRANSFORM_TAG );
		if ( null != screenTransformEl )
		{
			try
			{
				final double minX = XmlHelpers.getDouble( screenTransformEl, MIN_X_TAG, Double.NaN );
				final double maxX = XmlHelpers.getDouble( screenTransformEl, MAX_X_TAG, Double.NaN );
				final double minY = XmlHelpers.getDouble( screenTransformEl, MIN_Y_TAG, Double.NaN );
				final double maxY = XmlHelpers.getDouble( screenTransformEl, MAX_Y_TAG, Double.NaN );
				final int screenWidth = XmlHelpers.getInt( screenTransformEl, SCREEN_WIDTH_TAG, -1 );
				final int screenHeight = XmlHelpers.getInt( screenTransformEl, SCREEN_HEIGHT_TAG, -1 );
				final boolean invalid =
						Double.isNaN( minX ) || Double.isNaN( maxX )
								|| Double.isNaN( minY ) || Double.isNaN( maxY )
								|| screenWidth < 0 || screenHeight < 0
								|| maxX < minX || maxY < minY;
				if ( !invalid )
				{
					final ScreenTransform t = new ScreenTransform( minX, maxX, minY, maxY, screenWidth, screenHeight );
					trackschemePanel.getTransformEventHandler().setTransform( t );
				}
			}
			catch ( final NumberFormatException nfe )
			{}
		}

		// Coloring.
		final Element coloringEl = element.getChild( COLORING_TAG );
		if ( null != coloringEl )
		{
			final boolean noColoring = XmlHelpers.getBoolean( coloringEl, NO_COLORING_TAG );
			if ( noColoring )
				coloringModel.colorByNone();
			else
			{
				final String tagSetName = XmlHelpers.getText( coloringEl, TAG_SET_TAG );
				if ( null != tagSetName )
				{
					for ( final TagSet tagSet : coloringModel.getTagSetStructure().getTagSets() )
					{
						if ( tagSet.getName().equals( tagSetName ) )
						{
							coloringModel.colorByTagSet( tagSet );
							break;
						}
					}
				}
			}
		}
	}
}

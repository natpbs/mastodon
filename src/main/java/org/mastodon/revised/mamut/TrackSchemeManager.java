package org.mastodon.revised.mamut;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.mastodon.adapter.FeatureModelAdapter;
import org.mastodon.adapter.FocusAdapter;
import org.mastodon.adapter.HighlightAdapter;
import org.mastodon.adapter.NavigationHandlerAdapter;
import org.mastodon.adapter.RefBimap;
import org.mastodon.adapter.SelectionAdapter;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ListenableReadOnlyGraph;
import org.mastodon.revised.context.ContextChooser;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.trackscheme.TrackSchemeContextListener;
import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeEdgeBimap;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.trackscheme.TrackSchemeVertexBimap;
import org.mastodon.revised.trackscheme.display.TrackSchemeEditBehaviours;
import org.mastodon.revised.trackscheme.display.TrackSchemeFrame;
import org.mastodon.revised.trackscheme.display.TrackSchemeOptions;
import org.mastodon.revised.trackscheme.display.TrackSchemePanel;
import org.mastodon.revised.trackscheme.display.style.DefaultTrackSchemeOverlay;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleManager;
import org.mastodon.revised.trackscheme.wrap.DefaultModelGraphProperties;
import org.mastodon.revised.trackscheme.wrap.ModelGraphProperties;
import org.mastodon.revised.ui.HighlightBehaviours;
import org.mastodon.revised.ui.SelectionActions;
import org.mastodon.revised.ui.coloring.FeaturesColorGenerator;
import org.mastodon.revised.ui.grouping.GroupHandle;
import org.mastodon.revised.ui.selection.FocusModel;
import org.mastodon.revised.ui.selection.HighlightModel;
import org.mastodon.revised.ui.selection.NavigationHandler;
import org.mastodon.revised.ui.selection.NavigationHandlerImp;
import org.mastodon.revised.ui.selection.Selection;
import org.scijava.ui.behaviour.util.AbstractNamedAction;

public class TrackSchemeManager
{

	private final MamutAppModel mamutAppModel;

	private final MamutWindowModel mamutWindowModel;

	final TrackSchemeStyleManager trackSchemeStyleManager;

	public TrackSchemeManager( final MamutAppModel mamutAppModel, final MamutWindowModel mamutWindowModel )
	{
		this.mamutAppModel = mamutAppModel;
		this.mamutWindowModel = mamutWindowModel;
		this.trackSchemeStyleManager = new TrackSchemeStyleManager();
	}

	public TrackSchemeFrame createTrackScheme()
	{
		final ListenableReadOnlyGraph< Spot, Link > graph = mamutAppModel.model.getGraph();
		final GraphIdBimap< Spot, Link > idmap = mamutAppModel.model.getGraphIdBimap();

		/*
		 * TrackSchemeGraph listening to model
		 */
		final ModelGraphProperties< Spot, Link > properties = new DefaultModelGraphProperties<>();
		final TrackSchemeGraph< Spot, Link > trackSchemeGraph = new TrackSchemeGraph<>( graph, idmap, properties );
		final RefBimap< Spot, TrackSchemeVertex > vertexMap = new TrackSchemeVertexBimap<>( idmap, trackSchemeGraph );
		final RefBimap< Link, TrackSchemeEdge > edgeMap = new TrackSchemeEdgeBimap<>( idmap, trackSchemeGraph );

		/*
		 * TrackSchemeHighlight wrapping HighlightModel
		 */
		final HighlightModel< TrackSchemeVertex, TrackSchemeEdge > trackSchemeHighlight =
				new HighlightAdapter<>( mamutAppModel.highlightModel, vertexMap, edgeMap );

		/*
		 * TrackScheme selection
		 */
		final Selection< TrackSchemeVertex, TrackSchemeEdge > trackSchemeSelection =
				new SelectionAdapter<>( mamutAppModel.selection, vertexMap, edgeMap );

		/*
		 * TrackScheme GroupHandle
		 */
		final GroupHandle groupHandle = mamutWindowModel.groupManager.createGroupHandle();

		/*
		 * TrackScheme navigation
		 */
		final NavigationHandler< Spot, Link > navigationHandler = new NavigationHandlerImp<>( groupHandle );
		final NavigationHandler< TrackSchemeVertex, TrackSchemeEdge > trackSchemeNavigation = new NavigationHandlerAdapter<>( navigationHandler, vertexMap, edgeMap );

		/*
		 * TrackScheme focus
		 */
		final FocusModel< TrackSchemeVertex, TrackSchemeEdge > trackSchemeFocus = new FocusAdapter<>( mamutAppModel.focusModel, vertexMap, edgeMap );

		/*
		 * TrackScheme ContextChooser
		 */
		final TrackSchemeContextListener< Spot > contextListener = new TrackSchemeContextListener<>(
				idmap,
				trackSchemeGraph );
		final ContextChooser< Spot > contextChooser = new ContextChooser<>( contextListener );

		/*
		 * Features for TrackScheme.
		 */
		final FeatureModel< Spot, Link > featureModel = mamutAppModel.model.getGraphFeatureModel();
		final FeatureModel< TrackSchemeVertex, TrackSchemeEdge > trackSchemeFeatures =
				new FeatureModelAdapter<>( featureModel, vertexMap, edgeMap );

		/*
		 * Tune TrackScheme options to use a feature and tag-based coloring
		 * scheme.
		 */

		final FeaturesColorGenerator< TrackSchemeVertex, TrackSchemeEdge > colorGenerator =
				new FeaturesColorGenerator<>( TrackSchemeStyle.defaultStyle(),
						trackSchemeGraph, trackSchemeFeatures );

		final TrackSchemeOptions options = TrackSchemeOptions.options()
				.inputTriggerConfig( mamutWindowModel.keyconf )
				.vertexColorGenerator( colorGenerator )
				.edgeColorGenerator( colorGenerator )
				.shareKeyPressedEvents( mamutWindowModel.keyPressedManager );

		/*
		 * Show TrackSchemeFrame.
		 */

		final TrackSchemeFrame frame = new TrackSchemeFrame(
				trackSchemeGraph,
				trackSchemeHighlight,
				trackSchemeFocus,
				trackSchemeSelection,
				trackSchemeNavigation,
				mamutAppModel.model,
				groupHandle,
				contextChooser,
				options );

		installTrackSchemeMenu( frame, colorGenerator, false );

		frame.getTrackschemePanel().setTimepointRange( mamutAppModel.minTimepoint, mamutAppModel.maxTimepoint );
		frame.getTrackschemePanel().graphChanged();
		contextListener.setContextListener( frame.getTrackschemePanel() );
		frame.setVisible( true );

		UndoActions.installActionBindings( frame.getKeybindings(), mamutAppModel.model, mamutWindowModel.keyconf );
		HighlightBehaviours.installActionBindings(
				frame.getTriggerbindings(),
				mamutWindowModel.keyconf,
				new String[] { "ts" },
				mamutAppModel.model.getGraph(),
				mamutAppModel.model.getGraph(),
				mamutAppModel.highlightModel,
				mamutAppModel.model );
		SelectionActions.installActionBindings(
				frame.getKeybindings(),
				mamutWindowModel.keyconf,
				new String[] { "ts" },
				mamutAppModel.model.getGraph(),
				mamutAppModel.model.getGraph(),
				mamutAppModel.selection,
				mamutAppModel.model );
		TrackSchemeEditBehaviours.installActionBindings(
				frame.getTriggerbindings(),
				mamutWindowModel.keyconf,
				frame.getTrackschemePanel(),
				trackSchemeGraph,
				frame.getTrackschemePanel().getGraphOverlay(),
				mamutAppModel.model.getGraph(),
				mamutAppModel.model.getGraph().getGraphIdBimap(),
				mamutAppModel.model );

		final TsWindow tsWindow = new TsWindow( frame, groupHandle, contextChooser );
		addTsWindow( tsWindow );
		frame.getTrackschemePanel().repaint();

		return frame;
	}

	private void installTrackSchemeMenu(
			final TrackSchemeFrame frame,
			final FeaturesColorGenerator< TrackSchemeVertex, TrackSchemeEdge > colorGenerator,
			final boolean isBranchGraph )
	{

		final JMenuBar menu;
		if ( frame.getJMenuBar() == null )
			menu = new JMenuBar();
		else
			menu = frame.getJMenuBar();

		// Styles auto-populated from TrackScheme style manager.
		if ( frame.getTrackschemePanel().getGraphOverlay() instanceof DefaultTrackSchemeOverlay )
		{
			final TrackSchemePanel panel = frame.getTrackschemePanel();
			final DefaultTrackSchemeOverlay overlay = ( DefaultTrackSchemeOverlay ) panel.getGraphOverlay();
			final TsStyleListener styleListener = new TsStyleListener( panel, overlay, colorGenerator, isBranchGraph );

			final JMenu styleMenu = new JMenu( "Styles" );
			// Populate menu on the fly when it is opened.
			styleMenu.addMenuListener( new MenuListener()
			{
				@Override
				public void menuSelected( final MenuEvent e )
				{
					styleMenu.removeAll();
					for ( final TrackSchemeStyle style : trackSchemeStyleManager.getStyles() )
					{
						// Branch graph cannot get styles not set to a branch
						// feature.
						if ( isBranchGraph )
						{
							switch ( style.getVertexColorMode() )
							{
							case BRANCH_EDGE:
							case BRANCH_VERTEX:
							case FIXED:
								break;
							default:
								continue;
							}
							switch ( style.getEdgeColorMode() )
							{
							case FIXED:
							case BRANCH_EDGE:
							case BRANCH_VERTEX:
								break;
							default:
								continue;
							}
						}
						styleMenu.add( new JMenuItem(
								new TsStyleAction( style, panel, overlay, colorGenerator, styleListener ) ) );
					}
				}

				@Override
				public void menuDeselected( final MenuEvent e )
				{}

				@Override
				public void menuCanceled( final MenuEvent e )
				{}
			} );
			/*
			 * De-register style listener upon window closing.
			 */
			frame.addWindowListener( new WindowAdapter()
			{
				@Override
				public void windowClosing( final WindowEvent e )
				{
					for ( final TrackSchemeStyle style : trackSchemeStyleManager.getStyles() )
						style.removeUpdateListener( styleListener );
				};
			} );

			menu.add( styleMenu );
		}
		frame.setJMenuBar( menu );
	}

	/**
	 * Notifies the ColorGenerator that the style have been updated and trigger
	 * a repaint of the TrackScheme panel.
	 */
	private static final class TsStyleListener implements TrackSchemeStyle.UpdateListener
	{

		private final TrackSchemePanel panel;

		private final FeaturesColorGenerator< ?, ? > colorGenerator;

		private final boolean isBranchGraph;

		private final DefaultTrackSchemeOverlay overlay;

		public TsStyleListener(
				final TrackSchemePanel panel,
				final DefaultTrackSchemeOverlay overlay,
				final FeaturesColorGenerator< ?, ? > colorGenerator,
				final boolean isBranchGraph )
		{
			this.panel = panel;
			this.overlay = overlay;
			this.colorGenerator = colorGenerator;
			this.isBranchGraph = isBranchGraph;
		}

		@Override
		public void trackSchemeStyleChanged()
		{
			/*
			 * If we are in a branch view, check that we can still manage this
			 * style.
			 */
			if ( isBranchGraph )
			{
				// Forbid styles that do not work for branch graph.
				final TrackSchemeStyle style = overlay.getStyle();
				switch ( style.getVertexColorMode() )
				{
				case BRANCH_EDGE:
				case BRANCH_VERTEX:
				case FIXED:
					break;
				default:
					overlay.setStyle( TrackSchemeStyle.defaultStyle() );
					colorGenerator.setColorMode( TrackSchemeStyle.defaultStyle() );
					break;
				}
				switch ( style.getEdgeColorMode() )
				{
				case BRANCH_VERTEX:
				case BRANCH_EDGE:
				case FIXED:
					break;
				default:
					overlay.setStyle( TrackSchemeStyle.defaultStyle() );
					colorGenerator.setColorMode( TrackSchemeStyle.defaultStyle() );
					break;
				}
			}

			colorGenerator.colorModeChanged();
			panel.graphChanged();
		}
	}

	/**
	 * Action in charge of changing the style used to paint a TrackScheme.
	 */
	private class TsStyleAction extends AbstractNamedAction
	{

		private static final long serialVersionUID = 1L;

		private final TrackSchemeStyle style;

		private final TsStyleListener updater;

		private final DefaultTrackSchemeOverlay overlay;

		private final FeaturesColorGenerator< ?, ? > colorGenerator;

		private final TrackSchemePanel panel;

		public TsStyleAction(
				final TrackSchemeStyle style,
				final TrackSchemePanel panel,
				final DefaultTrackSchemeOverlay overlay,
				final FeaturesColorGenerator< ?, ? > colorGenerator,
				final TsStyleListener updater )
		{
			super( style.getName() );
			this.style = style;
			this.overlay = overlay;
			this.colorGenerator = colorGenerator;
			this.panel = panel;
			this.updater = updater;
		}

		@Override
		public void actionPerformed( final ActionEvent e )
		{
			overlay.getStyle().removeUpdateListener( updater );
			overlay.setStyle( style );
			colorGenerator.setColorMode( style );
			style.addUpdateListener( updater );
			panel.graphChanged();
		}
	}

	private synchronized void addTsWindow( final TsWindow w )
	{
		w.getTrackSchemeFrame().addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final WindowEvent e )
			{
				removeTsWindow( w );
			}
		} );
		mamutWindowModel.tsWindows.add( w );
		w.getContextChooser().updateContextProviders( mamutWindowModel.contextProviders );
	}

	private synchronized void removeTsWindow( final TsWindow w )
	{
		mamutWindowModel.tsWindows.remove( w );
		w.getContextChooser().updateContextProviders( new ArrayList<>() );
	}

	/**
	 * Information for one TrackScheme window.
	 */
	static class TsWindow
	{
		private final TrackSchemeFrame trackSchemeFrame;

		private final GroupHandle groupHandle;

		private final ContextChooser< Spot > contextChooser;

		public TsWindow(
				final TrackSchemeFrame trackSchemeFrame,
				final GroupHandle groupHandle,
				final ContextChooser< Spot > contextChooser )
		{
			this.trackSchemeFrame = trackSchemeFrame;
			this.groupHandle = groupHandle;
			this.contextChooser = contextChooser;
		}

		public TrackSchemeFrame getTrackSchemeFrame()
		{
			return trackSchemeFrame;
		}

		public GroupHandle getGroupHandle()
		{
			return groupHandle;
		}

		public ContextChooser< Spot > getContextChooser()
		{
			return contextChooser;
		}
	}

}

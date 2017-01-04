package org.mastodon.revised.trackscheme.display.style;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.mastodon.revised.model.feature.FeatureKeys;
import org.mastodon.revised.model.feature.FeatureRangeCalculator;
import org.mastodon.revised.model.feature.FeatureTarget;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle.ColorEdgeBy;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle.ColorVertexBy;
import org.mastodon.revised.ui.util.CategoryJComboBox;
import org.mastodon.revised.ui.util.ColorMap;

public class TrackSchemeStyleEditorPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final JColorChooser colorChooser;

	private final CategoryJComboBox< ColorVertexBy, FeatureKeyWrapper > colorVertexChoices;

	private final CategoryJComboBox< ColorEdgeBy, FeatureKeyWrapper > colorEdgeChoices;

	private final List< JComponent > vertexStuffToUnmute;

	private final List< JComponent > edgeStuffToUnmute;

	private final List< JComponent > edgeColorButtonToMute;

	private final List< JComponent > vertexColorButtonToMute;

	public TrackSchemeStyleEditorPanel(
			final TrackSchemeStyle style,
			final FeatureKeys featureKeys,
			final FeatureRangeCalculator featureRangeCalculator,
			final FeatureKeys branchGraphFeatureKeys,
			final FeatureRangeCalculator branchGraphFeatureRangeCalculator )
	{
		super( new GridBagLayout() );
		colorChooser = new JColorChooser();

		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets( 0, 5, 0, 5 );
		c.ipadx = 0;
		c.ipady = 0;
		c.gridy = 0;

		/*
		 * Color vertices by choices.
		 */

		final ColorMap currentCMap1 = style.vertexColorMap;

		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		final JLabel lbl1 = new JLabel( "Color vertices by" );
		lbl1.setFont( getFont().deriveFont( Font.BOLD ) );
		add( lbl1, c );

		c.gridx++;
		c.gridwidth = 3;
		this.colorVertexChoices = vertexColorBy( style, featureKeys, branchGraphFeatureKeys );
		add( colorVertexChoices, c );

		// Colormap and ranges.
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy++;
		final JLabel lbl3 = new JLabel( "ColorMap" );
		lbl3.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl3, c );

		c.gridx = 1;
		c.gridwidth = 1;
		final JComboBox< String > cmap1 = new JComboBox<>(
				ColorMap.getColorMapNames().toArray( new String[] {} ) );
		cmap1.setSelectedItem( currentCMap1.getName() );
		add( cmap1, c );

		c.gridx = 2;
		c.gridwidth = 2;
		final ColorMapPainter cmp1 = new ColorMapPainter( cmap1 );
		add( cmp1, c );

		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy++;
		final JLabel lbl5 = new JLabel( "Min/Max" );
		lbl5.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl5, c );

		c.gridx = 1;
		c.gridwidth = 3;
		final JPanel scalePanel1 = new JPanel();
		final BoxLayout boxLayout1 = new BoxLayout( scalePanel1, BoxLayout.LINE_AXIS );
		scalePanel1.setLayout( boxLayout1 );

		final JFormattedTextField min1 = new JFormattedTextField( Double.valueOf( style.minVertexColorRange ) );
		min1.setHorizontalAlignment( SwingConstants.CENTER );
		min1.setMaximumSize( new Dimension( 90, 20 ) );
		scalePanel1.add( min1 );
		final JFormattedTextField max1 = new JFormattedTextField( Double.valueOf( style.maxVertexColorRange ) );
		max1.setHorizontalAlignment( SwingConstants.CENTER );
		max1.setMaximumSize( new Dimension( 90, 20 ) );
		scalePanel1.add( max1 );
		final JButton autoscale1 = new JButton( "Autoscale" );
		scalePanel1.add( autoscale1 );

		add( scalePanel1, c );

		this.vertexStuffToUnmute = new ArrayList<>();
		vertexStuffToUnmute.add( lbl3 );
		vertexStuffToUnmute.add( cmap1 );
		vertexStuffToUnmute.add( cmp1 );
		vertexStuffToUnmute.add( lbl5 );
		vertexStuffToUnmute.add( min1 );
		vertexStuffToUnmute.add( max1 );
		vertexStuffToUnmute.add( autoscale1 );

		cmap1.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				final ColorMap cm = ColorMap.getColorMap( ( String ) cmap1.getSelectedItem() );
				style.vertexColorMap( cm );
				cmp1.repaint();
			}
		} );

		min1.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				style.minVertexColorRange( ( double ) min1.getValue() );
			}
		} );
		min1.addFocusListener( new FocusListener()
		{
			@Override
			public void focusLost( final FocusEvent e )
			{
				style.minVertexColorRange( ( double ) min1.getValue() );
			}

			@Override
			public void focusGained( final FocusEvent e )
			{}
		} );
		max1.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				style.maxVertexColorRange( ( double ) max1.getValue() );
			}
		} );
		max1.addFocusListener( new FocusListener()
		{
			@Override
			public void focusLost( final FocusEvent e )
			{
				style.maxVertexColorRange( ( double ) max1.getValue() );
			}

			@Override
			public void focusGained( final FocusEvent e )
			{}
		} );

		autoscale1.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( final ActionEvent e )
			{
				min1.setEnabled( false );
				max1.setEnabled( false );
				autoscale1.setEnabled( false );
				new Thread()
				{
					@Override
					public void run()
					{
						try
						{
							final ColorVertexBy category = colorVertexChoices.getSelectedCategory();
							final double[] range;
							switch ( category )
							{
							case BRANCH_EDGE:
							case BRANCH_VERTEX:
								range = branchGraphFeatureRangeCalculator.getRange( style.vertexColorFeatureKey );
								break;
							default:
								range = featureRangeCalculator.getRange( style.vertexColorFeatureKey );
								break;
							}
							style.minVertexColorRange = range[ 0 ];
							style.maxVertexColorRange( range[ 1 ] );
							min1.setValue( Double.valueOf( range[ 0 ] ) );
							max1.setValue( Double.valueOf( range[ 1 ] ) );
						}
						finally
						{
							min1.setEnabled( true );
							max1.setEnabled( true );
							autoscale1.setEnabled( true );
						}
					};
				}.start();

			}
		} );

		/*
		 * Color EDGES by choices.
		 */

		final ColorMap currentCMap2 = style.edgeColorMap;

		c.gridy++;
		add( Box.createVerticalStrut( 5 ), c );

		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 1;
		final JLabel lbl2 = new JLabel( "Color edges by" );
		lbl2.setFont( getFont().deriveFont( Font.BOLD ) );
		add( lbl2, c );

		c.gridx++;
		c.gridwidth = 3;
		this.colorEdgeChoices = edgeColorBy( style, featureKeys, branchGraphFeatureKeys );
		add( colorEdgeChoices, c );

		// Colormap and ranges.
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy++;
		final JLabel lbl4 = new JLabel( "ColorMap" );
		lbl4.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl4, c );

		c.gridx = 1;
		c.gridwidth = 1;
		final JComboBox< String > cmap2 = new JComboBox<>( ColorMap.getColorMapNames().toArray( new String[] {} ) );

		cmap2.setSelectedItem( currentCMap2.getName() );
		add( cmap2, c );

		c.gridx = 2;
		c.gridwidth = 2;
		final ColorMapPainter cmp2 = new ColorMapPainter( cmap2 );
		add( cmp2, c );

		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy++;
		final JLabel lbl6 = new JLabel( "Min/Max" );
		lbl6.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl6, c );

		c.gridx = 1;
		c.gridwidth = 3;
		final JPanel scalePanel2 = new JPanel();
		final BoxLayout boxLayout2 = new BoxLayout( scalePanel2, BoxLayout.LINE_AXIS );
		scalePanel2.setLayout( boxLayout2 );

		final JFormattedTextField min2 = new JFormattedTextField( Double.valueOf( style.minEdgeColorRange ) );
		min2.setHorizontalAlignment( SwingConstants.CENTER );
		min2.setMaximumSize( new Dimension( 90, 20 ) );
		scalePanel2.add( min2 );
		final JFormattedTextField max2 = new JFormattedTextField( Double.valueOf( style.maxEdgeColorRange ) );
		max2.setHorizontalAlignment( SwingConstants.CENTER );
		max2.setMaximumSize( new Dimension( 90, 20 ) );
		scalePanel2.add( max2 );
		final JButton autoscale2 = new JButton( "Autoscale" );
		scalePanel2.add( autoscale2 );

		add( scalePanel2, c );

		this.edgeStuffToUnmute = new ArrayList<>();
		edgeStuffToUnmute.add( lbl4 );
		edgeStuffToUnmute.add( cmap2 );
		edgeStuffToUnmute.add( cmp2 );
		edgeStuffToUnmute.add( lbl6 );
		edgeStuffToUnmute.add( min2 );
		edgeStuffToUnmute.add( max2 );
		edgeStuffToUnmute.add( autoscale2 );

		cmap2.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				final ColorMap cm = ColorMap.getColorMap( ( String ) cmap2.getSelectedItem() );
				style.edgeColorMap( cm );
				cmp2.repaint();
			}
		} );

		min2.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				style.minEdgeColorRange( ( double ) min2.getValue() );
			}
		} );
		min2.addFocusListener( new FocusListener()
		{
			@Override
			public void focusLost( final FocusEvent e )
			{
				style.minEdgeColorRange( ( double ) min2.getValue() );
			}

			@Override
			public void focusGained( final FocusEvent e )
			{}
		} );
		max2.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				style.maxEdgeColorRange( ( double ) max2.getValue() );
			}
		} );
		max2.addFocusListener( new FocusListener()
		{
			@Override
			public void focusLost( final FocusEvent e )
			{
				style.maxEdgeColorRange( ( double ) max2.getValue() );
			}

			@Override
			public void focusGained( final FocusEvent e )
			{}
		} );

		autoscale2.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( final ActionEvent e )
			{
				min2.setEnabled( false );
				max2.setEnabled( false );
				autoscale2.setEnabled( false );
				new Thread()
				{
					@Override
					public void run()
					{
						try
						{
							final ColorEdgeBy category = colorEdgeChoices.getSelectedCategory();
							final double[] range;
							switch ( category )
							{
							case BRANCH_EDGE:
							case BRANCH_VERTEX:
								range = branchGraphFeatureRangeCalculator.getRange( style.edgeColorFeatureKey );
								break;
							default:
								range = featureRangeCalculator.getRange( style.edgeColorFeatureKey );
								break;
							}
							style.minEdgeColorRange = range[ 0 ];
							style.maxEdgeColorRange( range[ 1 ] );
							min2.setValue( Double.valueOf( range[ 0 ] ) );
							max2.setValue( Double.valueOf( range[ 1 ] ) );
						}
						finally
						{
							min2.setEnabled( true );
							max2.setEnabled( true );
							autoscale2.setEnabled( true );
						}
					};
				}.start();
			}
		} );

		/*
		 * Fixed color setters.
		 */

		final List< ColorSetter > styleColors = styleColors( style );
		final List< BooleanSetter > styleBooleans = styleBooleans( style );

		this.edgeColorButtonToMute = new ArrayList<>();
		this.vertexColorButtonToMute = new ArrayList<>();

		c.gridx = 0;
		c.gridy++;
		add( Box.createVerticalStrut( 5 ), c );
		c.gridy++;
		c.gridwidth = 2;
		final int columnStart = c.gridy;
		for ( final ColorSetter colorSetter : styleColors )
		{
			final JButton button = new JButton( colorSetter.getLabel(), new ColorIcon( colorSetter.getColor() ) );
			button.setOpaque( false );
			button.setContentAreaFilled( false );
			button.setBorderPainted( false );
			button.setMargin( new Insets( 0, 0, 0, 0 ) );
			button.setBorder( new EmptyBorder( 2, 2, 2, 2 ) );
			button.setHorizontalAlignment( SwingConstants.LEFT );
			button.addActionListener( new ActionListener()
			{
				@Override
				public void actionPerformed( final ActionEvent e )
				{
					colorChooser.setColor( colorSetter.getColor() );
					final JDialog d = JColorChooser.createDialog( button, "Choose a color", true, colorChooser, new ActionListener()
					{
						@Override
						public void actionPerformed( final ActionEvent arg0 )
						{
							final Color c = colorChooser.getColor();
							if ( c != null )
							{
								button.setIcon( new ColorIcon( c ) );
								colorSetter.setColor( c );
							}
						}
					}, null );
					d.setVisible( true );
				}
			} );
			c.anchor = GridBagConstraints.LINE_END;
			add( button, c );
			c.gridy++;

			if ( c.gridy - columnStart > 8 )
			{
				c.gridy = columnStart;
				c.gridx = 2;
			}

			if ( colorSetter.skip > 0 )
			{
				add( Box.createVerticalStrut( 5 ), c );
				c.gridy++;
			}

			switch ( colorSetter.getLabel() )
			{
			case "edgeColor":
				edgeColorButtonToMute.add( button );
				break;
			case "vertexFillColor":
			case "simplifiedVertexFillColor":
				vertexColorButtonToMute.add( button );
				break;
			}
		}

		c.gridy = columnStart + 9;
		c.gridx = 0;
		add( Box.createVerticalStrut( 5 ), c );
		c.gridy++;

		// On 2 columns
		final int startLine = c.gridy;
		boolean firstCol = true;
		for ( int i = 0; i < styleBooleans.size(); i++ )
		{
			if ( firstCol && i >= styleBooleans.size() / 2 )
			{
				firstCol = false;
				c.gridy = startLine;
				c.gridx = c.gridx + 2;
			}
			final BooleanSetter booleanSetter = styleBooleans.get( i );
			final JCheckBox checkbox = new JCheckBox( booleanSetter.getLabel(), booleanSetter.get() );
			checkbox.addActionListener( new ActionListener()
			{
				@Override public void actionPerformed( final ActionEvent e ) { booleanSetter.set( checkbox.isSelected() );}
			} );
			add( checkbox, c );
			c.gridy++;
		}

		/*
		 * Listeners to category changes.
		 */

		colorEdgeChoices.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent event )
			{
				final FeatureKeyWrapper item = colorEdgeChoices.getSelectedItem();
				final ColorEdgeBy colorEdgeBy = colorEdgeChoices.getSelectedCategory();
				for ( final JComponent button : edgeColorButtonToMute )
					button.setEnabled( colorEdgeBy == ColorEdgeBy.FIXED );
				for ( final JComponent jc : edgeStuffToUnmute )
					jc.setEnabled( colorEdgeBy != ColorEdgeBy.FIXED );

				style.edgeColorFeatureKey = item.featureKey;
				// Notify listeners only once.
				style.colorEdgeBy( colorEdgeBy );
			}
		} );
		// Fire action listener once.
		colorEdgeChoices.setSelectedIndex( colorEdgeChoices.getSelectedIndex() );

		colorVertexChoices.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent event )
			{
				final FeatureKeyWrapper item = colorVertexChoices.getSelectedItem();
				final ColorVertexBy colorVertexBy = colorVertexChoices.getSelectedCategory();
				for ( final JComponent button : vertexColorButtonToMute )
					button.setEnabled( colorVertexBy == ColorVertexBy.FIXED );
				for ( final JComponent jc : vertexStuffToUnmute )
					jc.setEnabled( colorVertexBy != ColorVertexBy.FIXED );

				style.vertexColorFeatureKey = item.featureKey;
				// Notify listeners only once.
				style.colorVertexBy( colorVertexBy );
			}
		} );
		// Fire action listener once.
		colorVertexChoices.setSelectedIndex( colorVertexChoices.getSelectedIndex() );
	}

	@Override
	public void setEnabled( final boolean enabled )
	{
		super.setEnabled( enabled );
		final Component[] comps = getComponents();
		for ( final Component comp : comps )
			comp.setEnabled( enabled );

		// Specially don't enable stuff to mute.
		if ( enabled )
		{
			final ColorVertexBy colorVertexBy = colorVertexChoices.getSelectedCategory();
			for ( final JComponent button : vertexColorButtonToMute )
				button.setEnabled( colorVertexBy == ColorVertexBy.FIXED );
			for ( final JComponent jc : vertexStuffToUnmute )
				jc.setEnabled( colorVertexBy != ColorVertexBy.FIXED );

			final ColorEdgeBy colorEdgeBy = colorEdgeChoices.getSelectedCategory();
			for ( final JComponent button : edgeColorButtonToMute )
				button.setEnabled( colorEdgeBy == ColorEdgeBy.FIXED );
			for ( final JComponent jc : edgeStuffToUnmute )
				jc.setEnabled( colorEdgeBy != ColorEdgeBy.FIXED );
		}
	}

	private static abstract class ColorSetter
	{
		private final String label;

		private final int skip;

		public ColorSetter( final String label )
		{
			this( label, 0 );
		}

		public ColorSetter( final String label, final boolean skip )
		{
			this( label, skip ? 15 : 0 );
		}

		public ColorSetter( final String label, final int skip )
		{
			this.label = label;
			this.skip = skip;
		}

		public String getLabel()
		{
			return label;
		}

		public abstract Color getColor();

		public abstract void setColor( Color c );
	}

	private static abstract class BooleanSetter
	{
		private final String label;

		public BooleanSetter( final String label )
		{
			this.label = label;
		}

		public String getLabel()
		{
			return label;
		}

		public abstract boolean get();

		public abstract void set( boolean b );
	}

	private static final class FeatureKeyWrapper
	{
		private final String featureKey;

		private final String name;

		public FeatureKeyWrapper( final String featureKey, final String name )
		{
			this.featureKey = featureKey;
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	private static CategoryJComboBox< ColorEdgeBy, FeatureKeyWrapper > edgeColorBy(
			final TrackSchemeStyle style,
			final FeatureKeys featureKeys,
			final FeatureKeys branchGraphFeatureKeys )
	{
		/*
		 * Harvest possible choices.
		 */
		final Map< ColorEdgeBy, Collection< FeatureKeyWrapper > > items = new LinkedHashMap<>();
		final Map< ColorEdgeBy, String > categoryNames = new HashMap<>();

		// Fixed color.
		final FeatureKeyWrapper fixedColor = new FeatureKeyWrapper( "Fixed color", "Fixed color" );
		items.put( ColorEdgeBy.FIXED, Collections.singleton( fixedColor ) );
		categoryNames.put( ColorEdgeBy.FIXED, "Fixed" );

		// This edge.
		final Collection< FeatureKeyWrapper > edgeProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.EDGE ) )
			edgeProjections.add( new FeatureKeyWrapper( projectionKey, projectionKey ) );
		if ( !edgeProjections.isEmpty() )
		{
			items.put( ColorEdgeBy.EDGE, edgeProjections );
			categoryNames.put( ColorEdgeBy.EDGE, "Edge feature" );
		}

		// Source and target vertex.
		final Collection< FeatureKeyWrapper > sourceVertexProjections = new ArrayList<>();
		final Collection< FeatureKeyWrapper > targetVertexProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.VERTEX ) )
		{
			sourceVertexProjections.add( new FeatureKeyWrapper( projectionKey, projectionKey ) );
			targetVertexProjections.add( new FeatureKeyWrapper( projectionKey, projectionKey ) );
		}
		if ( !sourceVertexProjections.isEmpty() )
		{
			items.put( ColorEdgeBy.SOURCE_VERTEX, sourceVertexProjections );
			items.put( ColorEdgeBy.TARGET_VERTEX, targetVertexProjections );
			categoryNames.put( ColorEdgeBy.SOURCE_VERTEX, "Source vertex feature" );
			categoryNames.put( ColorEdgeBy.TARGET_VERTEX, "Target vertex feature" );
		}

		// Branch edge.
		final Collection< FeatureKeyWrapper > branchEdgeProjections = new ArrayList<>();
		for ( final String projectionKey : branchGraphFeatureKeys.getProjectionKeys( FeatureTarget.EDGE ) )
			branchEdgeProjections.add( new FeatureKeyWrapper( projectionKey, projectionKey ) );
		if ( !branchEdgeProjections.isEmpty() )
		{
			items.put( ColorEdgeBy.BRANCH_EDGE, branchEdgeProjections );
			categoryNames.put( ColorEdgeBy.BRANCH_EDGE, "Branch edge" );
		}

		// Branch vertex.
		final Collection< FeatureKeyWrapper > branchVertexProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.VERTEX ) )
			branchVertexProjections.add( new FeatureKeyWrapper( projectionKey, projectionKey ) );
		if ( !branchVertexProjections.isEmpty() )
		{
			items.put( ColorEdgeBy.BRANCH_VERTEX, branchVertexProjections );
			categoryNames.put( ColorEdgeBy.BRANCH_VERTEX, "Branch vertex" );
		}

		final Map< FeatureKeyWrapper, String > itemNames = null;
		final CategoryJComboBox< ColorEdgeBy, FeatureKeyWrapper > comboBox =
				new CategoryJComboBox<>( items, itemNames, categoryNames );

		/*
		 * Set selected item according to provided style.
		 */

		FeatureKeyWrapper fkw = fixedColor;
		if ( null != items.get( style.colorEdgeBy ) )
			for ( final FeatureKeyWrapper key : items.get( style.colorEdgeBy ) )
			{
				if ( style.edgeColorFeatureKey.equals( key.featureKey ) )
				{
					fkw = key;
					break;
				}
			}
		comboBox.setSelectedItem( fkw );
		return comboBox;
	}

	private static CategoryJComboBox< ColorVertexBy, FeatureKeyWrapper > vertexColorBy(
			final TrackSchemeStyle style,
			final FeatureKeys featureKeys,
			final FeatureKeys branchGraphFeatureKeys )
	{
		/*
		 * Harvest possible choices.
		 */

		final Map< ColorVertexBy, Collection< FeatureKeyWrapper > > items = new LinkedHashMap<>();
		final Map< ColorVertexBy, String > categoryNames = new HashMap<>();

		// Fixed.
		final FeatureKeyWrapper fixedColor = new FeatureKeyWrapper( "Fixed color", "Fixed color" );
		items.put( ColorVertexBy.FIXED, Collections.singleton( fixedColor ) );
		categoryNames.put( ColorVertexBy.FIXED, "Fixed" );

		// This vertex.
		final Collection< FeatureKeyWrapper > vertexProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.VERTEX ) )
			vertexProjections.add( new FeatureKeyWrapper( projectionKey, projectionKey ) );
		if ( !vertexProjections.isEmpty() )
		{
			items.put( ColorVertexBy.VERTEX, vertexProjections );
			categoryNames.put( ColorVertexBy.VERTEX, "Vertex feature" );
		}

		// Incoming and outgoing edges.
		final Collection< FeatureKeyWrapper > incomingEdgeProjections = new ArrayList<>();
		final Collection< FeatureKeyWrapper > outgoingEdgeProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.EDGE ) )
		{
			incomingEdgeProjections.add( new FeatureKeyWrapper( projectionKey, projectionKey ) );
			outgoingEdgeProjections.add( new FeatureKeyWrapper( projectionKey, projectionKey ) );
		}
		if ( !incomingEdgeProjections.isEmpty() )
		{
			items.put( ColorVertexBy.INCOMING_EDGE, incomingEdgeProjections );
			items.put( ColorVertexBy.OUTGOING_EDGE, outgoingEdgeProjections );
			categoryNames.put( ColorVertexBy.INCOMING_EDGE, "Incoming edge feature" );
			categoryNames.put( ColorVertexBy.OUTGOING_EDGE, "Outgoing edge feature" );
		}

		// Branch vertex.
		final Collection< FeatureKeyWrapper > branchVertexProjections = new ArrayList<>();
		for ( final String projectionKey : branchGraphFeatureKeys.getProjectionKeys( FeatureTarget.VERTEX ) )
			branchVertexProjections.add( new FeatureKeyWrapper( projectionKey, projectionKey ) );
		if ( !branchVertexProjections.isEmpty() )
		{
			items.put( ColorVertexBy.BRANCH_VERTEX, branchVertexProjections );
			categoryNames.put( ColorVertexBy.BRANCH_VERTEX, "Branch vertex" );
		}

		// Branch edge.
		final Collection< FeatureKeyWrapper > branchEdgeProjections = new ArrayList<>();
		for ( final String projectionKey : branchGraphFeatureKeys.getProjectionKeys( FeatureTarget.EDGE ) )
			branchEdgeProjections.add( new FeatureKeyWrapper( projectionKey, projectionKey ) );
		if ( !branchEdgeProjections.isEmpty() )
		{
			items.put( ColorVertexBy.BRANCH_EDGE, branchEdgeProjections );
			categoryNames.put( ColorVertexBy.BRANCH_EDGE, "Branch edge" );
		}

		final Map< FeatureKeyWrapper, String > itemNames = null;
		final CategoryJComboBox< ColorVertexBy, FeatureKeyWrapper > comboBox =
				new CategoryJComboBox<>( items, itemNames, categoryNames );

		/*
		 * Set selected item according to provided style.
		 */
		FeatureKeyWrapper fkw = fixedColor;
		if ( null != items.get( style.colorVertexBy ) )
			for ( final FeatureKeyWrapper key : items.get( style.colorVertexBy ) )
			{
				if ( style.vertexColorFeatureKey.equals( key.featureKey ) )
				{
					fkw = key;
					break;
				}
			}

		comboBox.setSelectedItem( fkw );
		return comboBox;
	}

	private static List< ColorSetter > styleColors( final TrackSchemeStyle style )
	{
		return Arrays.asList( new ColorSetter[] {
				new ColorSetter( "edge color" )
				{
					@Override public Color getColor() { return style.edgeColor; }
					@Override public void setColor( final Color c ) { style.edgeColor( c ); }
				},
				new ColorSetter( "vertex fill color" )
				{
					@Override public Color getColor() { return style.vertexFillColor; }
					@Override public void setColor( final Color c ) { style.vertexFillColor( c ); }
				},
				new ColorSetter( "vertex draw color" )
				{
					@Override public Color getColor() { return style.vertexDrawColor; }
					@Override public void setColor( final Color c ) { style.vertexDrawColor( c ); }
				},
				new ColorSetter( "simplified vertex fill color", true )
				{
					@Override public Color getColor() { return style.simplifiedVertexFillColor; }
					@Override public void setColor( final Color c ) { style.simplifiedVertexFillColor( c ); }
				},
				new ColorSetter( "selected edge color" )
				{
					@Override public Color getColor() { return style.selectedEdgeColor; }
					@Override public void setColor( final Color c ) { style.selectedEdgeColor( c ); }
				},
				new ColorSetter( "selected vertex draw color" )
				{
					@Override public Color getColor() { return style.selectedVertexDrawColor; }
					@Override public void setColor( final Color c ) { style.selectedVertexDrawColor( c ); }
				},
				new ColorSetter( "selected vertex fill color" ) {
					@Override public Color getColor() { return style.selectedVertexFillColor; }
					@Override public void setColor( final Color c ) { style.selectedVertexFillColor( c ); }
				},
				// Column break
				new ColorSetter( "selected simplified vertex fill color", false )
				{
					@Override public Color getColor() { return style.selectedSimplifiedVertexFillColor; }
					@Override public void setColor( final Color c ) { style.selectedSimplifiedVertexFillColor( c ); }
				},
				new ColorSetter( "background color" ) {
					@Override public Color getColor() { return style.backgroundColor; }
					@Override public void setColor( final Color c ) { style.backgroundColor( c ); }
				},
				new ColorSetter( "current timepoint color" ) {
					@Override public Color getColor() { return style.currentTimepointColor; }
					@Override public void setColor( final Color c ) { style.currentTimepointColor( c ); }
				},
				new ColorSetter( "decoration color" ) {
					@Override public Color getColor() { return style.decorationColor; }
					@Override public void setColor( final Color c ) { style.decorationColor( c ); }
				},
				new ColorSetter( "vertex range color", true ) {
					@Override public Color getColor() { return style.vertexRangeColor; }
					@Override public void setColor( final Color c ) { style.vertexRangeColor( c ); }
				},
				new ColorSetter( "header background color" ) {
					@Override public Color getColor() { return style.headerBackgroundColor; }
					@Override public void setColor( final Color c ) { style.headerBackgroundColor( c ); }
				},
				new ColorSetter( "header decoration color" ) {
					@Override public Color getColor() { return style.headerDecorationColor; }
					@Override public void setColor( final Color c ) { style.headerDecorationColor( c ); }
				},
				new ColorSetter( "header current timepoint color" ) {
					@Override public Color getColor() { return style.headerCurrentTimepointColor; }
					@Override public void setColor( final Color c ) { style.headerCurrentTimepointColor( c ); }
				}
		} );
	}

	private static List< BooleanSetter > styleBooleans( final TrackSchemeStyle style )
	{
		return Arrays.asList( new BooleanSetter[] {
				new BooleanSetter( "paint rows ") {
					@Override public boolean get() { return style.paintRows; }
					@Override public void set( final boolean b ) { style.paintRows( b ); }
				},
				new BooleanSetter( "paint columns ") {
					@Override public boolean get() { return style.paintColumns; }
					@Override public void set( final boolean b ) { style.paintColumns( b ); }
				},
				new BooleanSetter( "highlight current timepoint ") {
					@Override public boolean get() { return style.highlightCurrentTimepoint; }
					@Override public void set( final boolean b ) { style.highlightCurrentTimepoint( b ); }
				},
				new BooleanSetter( "paint header shadow ") {
					@Override public boolean get() { return style.paintHeaderShadow; }
					@Override public void set( final boolean b ) { style.paintHeaderShadow( b ); }
				}
		} );
	}

	/**
	 * Adapted from http://stackoverflow.com/a/3072979/230513
	 */
	private static class ColorIcon implements Icon
	{
		private final int size = 16;

		private final Color color;

		public ColorIcon( final Color color )
		{
			this.color = color;
		}

		@Override
		public void paintIcon( final Component c, final Graphics g, final int x, final int y )
		{
			final Graphics2D g2d = ( Graphics2D ) g;
			g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g2d.setColor( color );
			// g2d.fillOval( x, y, size, size );
			g2d.fill( new RoundRectangle2D.Float( x, y, size, size, 5, 5 ) );
		}

		@Override
		public int getIconWidth()
		{
			return size;
		}

		@Override
		public int getIconHeight()
		{
			return size;
		}
	}

	private static final class ColorMapPainter extends JComponent
	{

		private static final long serialVersionUID = 1L;

		private final JComboBox< String > choices;

		public ColorMapPainter( final JComboBox< String > choices )
		{
			this.choices = choices;
		}

		@Override
		protected void paintComponent( final Graphics g )
		{
			super.paintComponent( g );
			if ( !isEnabled() )
				return;

			final String cname = ( String ) choices.getSelectedItem();
			final ColorMap cmap = ColorMap.getColorMap( cname );
			final int w = getWidth();
			final int h = getHeight();
			final int lw = ( int ) ( 0.8 * w );
			for ( int i = 0; i < lw; i++ )
			{
				g.setColor( cmap.get( ( double ) i / lw ) );
				g.drawLine( i, 0, i, h );
			}

			// NaN.
			g.setColor( cmap.get( Double.NaN ) );
			g.fillRect( ( int ) ( 0.83 * w ), 0, ( int ) ( 0.07 * w ), h );

			// Missing color.
			g.setColor( cmap.getMissingColor() );
			g.fillRect( ( int ) ( 0.93 * w ), 0, ( int ) ( 0.07 * w ), h );
		}

		@Override
		public Dimension getPreferredSize()
		{
			final Dimension dimension = super.getPreferredSize();
			dimension.height = 20;
			return dimension;
		}

		@Override
		public Dimension getMinimumSize()
		{
			return getPreferredSize();
		}
	}
}
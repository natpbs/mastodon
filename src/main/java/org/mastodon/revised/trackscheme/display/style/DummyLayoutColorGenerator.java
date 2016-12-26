package org.mastodon.revised.trackscheme.display.style;

import java.awt.Color;

import javax.swing.MutableComboBoxModel;

import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle.UpdateListener;
import org.mastodon.revised.ui.EdgeColorGenerator;
import org.mastodon.revised.ui.VertexColorGenerator;
import org.mastodon.revised.ui.util.ColorMap;

public class DummyLayoutColorGenerator implements UpdateListener, VertexColorGenerator< TrackSchemeVertex >, EdgeColorGenerator< TrackSchemeEdge >
{

	private final MutableComboBoxModel< TrackSchemeStyle > model;

	private final TrackSchemeGraph< ?, ? > graph;

	private ColorMap edgeColorMap;

	private ColorMap vertexColorMap;


	public DummyLayoutColorGenerator( final TrackSchemeGraph< ?, ? > graph, final MutableComboBoxModel< TrackSchemeStyle > model )
	{
		this.graph = graph;
		this.model = model;
		trackSchemeStyleChanged();
	}

	@Override
	public Color color( final TrackSchemeEdge edge )
	{
		final double val = ( double ) edge.getInternalPoolIndex() / graph.edges().size();
		return edgeColorMap.get( val );
	}

	@Override
	public Color color( final TrackSchemeVertex vertex )
	{
		final double val = ( double ) vertex.getInternalPoolIndex() / graph.vertices().size();
		return vertexColorMap.get( val );
	}

	@Override
	public void trackSchemeStyleChanged()
	{
		final TrackSchemeStyle style = ( TrackSchemeStyle ) model.getSelectedItem();
		edgeColorMap = style.edgeColorMap;
		vertexColorMap = style.vertexColorMap;
	}

}

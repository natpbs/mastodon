package org.mastodon.revised.trackscheme.display.style;

import java.awt.Color;

import org.mastodon.graph.Vertex;

public interface VertexColorGenerator< V extends Vertex< ? > >
{
	public Color color( V vertex );
}
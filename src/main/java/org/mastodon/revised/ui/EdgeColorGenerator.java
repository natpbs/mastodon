package org.mastodon.revised.ui;

import java.awt.Color;

import org.mastodon.graph.Edge;

public interface EdgeColorGenerator< E extends Edge< ? > >
{
	public Color color( E edge );
}
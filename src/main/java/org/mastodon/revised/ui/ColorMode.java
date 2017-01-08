package org.mastodon.revised.ui;

import org.mastodon.revised.ui.util.ColorMap;

public interface ColorMode
{

	public enum EdgeColorMode
	{
		FIXED, EDGE, SOURCE_VERTEX, TARGET_VERTEX, BRANCH_EDGE, BRANCH_VERTEX;
	}

	public enum VertexColorMode
	{
		FIXED, VERTEX, INCOMING_EDGE, OUTGOING_EDGE, BRANCH_EDGE, BRANCH_VERTEX;
	}

	/*
	 * Getters.
	 */

	public VertexColorMode getVertexColorMode();

	public String getVertexFeatureKey();

	public ColorMap getVertexColorMap();

	public double getMinVertexColorRange();

	public double getMaxVertexColorRange();

	public EdgeColorMode getEdgeColorMode();

	public String getEdgeFeatureKey();

	public ColorMap getEdgeColorMap();

	public double getMinEdgeColorRange();

	public double getMaxEdgeColorRange();

	/*
	 * Setters.
	 */

	public ColorMode edgeColorMode( final EdgeColorMode edgeColorMode );

	public ColorMode vertexColorMode( final VertexColorMode vertexColorMode );

	public ColorMode edgeColorMap( final ColorMap colorMap );

	public ColorMode vertexColorMap( final ColorMap colorMap );

	public ColorMode edgeColorFeatureKey( final String key );

	public ColorMode vertexColorFeatureKey( final String key );

	public ColorMode minEdgeColorRange( final double val );

	public ColorMode maxEdgeColorRange( final double val );

	public ColorMode minVertexColorRange( final double val );

	public ColorMode maxVertexColorRange( final double val );

	/*
	 * Listeners.
	 */

	public void notifyListeners();

}

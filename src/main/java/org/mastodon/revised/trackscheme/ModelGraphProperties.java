package org.mastodon.revised.trackscheme;

/**
 * Interface for accessing model graph properties.
 * <p>
 * To make {@link TrackSchemeGraph} adaptable to various model graph type
 * without requiring the graph to implement specific interfaces, we access
 * properties of model vertices and edges (for example the label of a vertex)
 * through {@link ModelGraphProperties}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface ModelGraphProperties< V, E >
{
	public int getTimepoint( V v );

	public String getLabel( V v );

	public void setLabel( V v, String label );

	public E addEdge( V source, V target, E ref );

	public V addVertex( V ref );

	public void removeEdge( E e );

	public void removeVertex( V v );

	public void notifyGraphChanged();


}

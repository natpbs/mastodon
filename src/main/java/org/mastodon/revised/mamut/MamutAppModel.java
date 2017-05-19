package org.mastodon.revised.mamut;

import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ListenableReadOnlyGraph;
import org.mastodon.revised.bdv.SharedBigDataViewerData;
import org.mastodon.revised.model.mamut.BoundingSphereRadiusStatistics;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.ui.selection.FocusModel;
import org.mastodon.revised.ui.selection.FocusModelImp;
import org.mastodon.revised.ui.selection.HighlightModel;
import org.mastodon.revised.ui.selection.HighlightModelImp;
import org.mastodon.revised.ui.selection.Selection;
import org.mastodon.revised.ui.selection.SelectionImp;

/**
 * Data class that stores the data model and the application model of the MaMuT
 * application.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class MamutAppModel
{
	final Model model;

	final Selection< Spot, Link > selection;

	final HighlightModel< Spot, Link > highlightModel;

	final BoundingSphereRadiusStatistics radiusStats;

	final FocusModel< Spot, Link > focusModel;

	final SharedBigDataViewerData sharedBdvData;

	final int minTimepoint;

	final int maxTimepoint;


	public MamutAppModel(
			final Model model,
			final SharedBigDataViewerData sharedBdvData )
	{
		this.model = model;
		final ListenableReadOnlyGraph< Spot, Link > graph = model.getGraph();
		final GraphIdBimap< Spot, Link > idmap = model.getGraphIdBimap();
		this.selection = new SelectionImp<>( graph, idmap );
		this.highlightModel = new HighlightModelImp<>( idmap );
		this.radiusStats = new BoundingSphereRadiusStatistics( model );
		this.focusModel = new FocusModelImp<>( idmap );
		this.sharedBdvData = sharedBdvData;
		this.minTimepoint = 0;
		this.maxTimepoint = sharedBdvData.getNumTimepoints() - 1;
	}
}

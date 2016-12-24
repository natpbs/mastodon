package org.mastodon.revised.model.mamut.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mastodon.graph.algorithm.TopologicalSort;
import org.mastodon.graph.object.ObjectEdge;
import org.mastodon.graph.object.ObjectGraph;
import org.mastodon.graph.object.ObjectVertex;
import org.mastodon.revised.model.mamut.Model;
import org.scijava.InstantiableException;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.PluginService;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

@Plugin( type = Service.class )
public class DefaultMamutFeatureService extends AbstractService implements MamutFeatureService
{

	@Parameter
	private PluginService pluginService;

	@Parameter
	private LogService logService;

	/**
	 * Feature computers of any type.
	 */
	private final Map< String, FeatureComputer > availableFeatureComputers = new HashMap<>();

	/**
	 * Available spot feature computers.
	 */
	private Set< String > availableSpotFeatureComputers;

	/**
	 * Available link feature computers.
	 */
	private Set< String > availableLinkFeatureComputers;

	public DefaultMamutFeatureService()
	{}

	@Override
	public void initialize()
	{
		super.initialize();
		initializeAvailableSpotFeatureComputers();
		initializeAvailableLinkFeatureComputers();
	}

	public final Set< String > getAvailableSpotFeatureComputers()
	{
		return availableSpotFeatureComputers;
	}

	public Set< String > getAvailableLinkFeatureComputers()
	{
		return availableLinkFeatureComputers;
	}

	public boolean compute( final Model model, final Set< String > computerNames )
	{
		final ObjectGraph< FeatureComputer > dependencyGraph = getDependencyGraph( computerNames );
		final TopologicalSort< ObjectVertex< FeatureComputer >, ObjectEdge< FeatureComputer > > sorter
			= new TopologicalSort<>( dependencyGraph );

		if (sorter.hasFailed())
		{
			logService.error( "Could computer features using  " + computerNames +
					" as they have a circular dependency." );
			return false;
		}

		model.featureModel().clearFeatures();
		for ( final ObjectVertex< FeatureComputer > v : sorter.get() )
		{
			final FeatureComputer computer = v.getContent();
			model.featureModel().declareFeature( computer.getFeature(), computer.getTarget() );
			computer.compute( model );
		}

		return true;
	}

	/*
	 * DEPENDENCY GRAPH.
	 */

	private ObjectGraph< FeatureComputer > getDependencyGraph( final Set< String > computerNames )
	{
		final ObjectGraph< FeatureComputer > computerGraph = new ObjectGraph<>();
		final ObjectVertex< FeatureComputer > ref = computerGraph.vertexRef();

		final Set< FeatureComputer > requestedFeatureComputers = new HashSet<>();
		for ( final String cName : computerNames )
		{
			// Build a list of feature computers.
			requestedFeatureComputers.add( availableFeatureComputers.get( cName ) );

			// Add them in the dependency graph.
			addDepVertex( cName, computerGraph, ref );
		}

		computerGraph.releaseRef( ref );
		prune( computerGraph, requestedFeatureComputers );
		return computerGraph;
	}

	/**
	 * Removes uncalled for dependencies.
	 * <p>
	 * When a computer has missing dependencies, it is removed from the
	 * dependency graph. But its dependencies that are available are still
	 * present in the graph after its removal. This method removes them if their
	 * calculation were not requested by the user.
	 *
	 * @param computerGraph
	 * @param requestedFeatureComputers
	 */
	private void prune( final ObjectGraph< FeatureComputer > computerGraph, final Set< FeatureComputer > requestedFeatureComputers )
	{
		for ( final ObjectVertex< FeatureComputer > v : new ArrayList<>( computerGraph.vertices() ) )
			if ( v.incomingEdges().isEmpty() && !requestedFeatureComputers.contains( v.getContent() ) )
				computerGraph.remove( v );
	}

	/**
	 * Called recursively.
	 *
	 * @param depName
	 * @param computerGraph
	 * @param ref
	 * @return
	 */
	private final ObjectVertex< FeatureComputer > addDepVertex( final String depName, final ObjectGraph< FeatureComputer > computerGraph, final ObjectVertex< FeatureComputer > ref )
	{
		final FeatureComputer fc = availableFeatureComputers.get( depName );
		if ( null == fc )
		{
			logService.error( "Cannot add feature computer named " + depName + " as it is not registered." );
			return null;
		}

		for ( final ObjectVertex< FeatureComputer > v : computerGraph.vertices() )
		{
			if ( v.getContent().equals( fc ) )
				return v;
		}

		final ObjectVertex< FeatureComputer > source = computerGraph.addVertex( ref ).init( fc );
		final Set< String > deps = fc.getDependencies();

		final ObjectVertex< FeatureComputer > vref2 = computerGraph.vertexRef();
		final ObjectEdge< FeatureComputer > eref = computerGraph.edgeRef();

		for ( final String dep : deps )
		{
			final ObjectVertex< FeatureComputer > target = addDepVertex( dep, computerGraph, vref2 );
			if ( null == target )
			{
				logService.error( "Removing feature computer named " + depName + " as some of its dependencies could not be resolved." );
				computerGraph.remove( source );
				break;
			}
			computerGraph.addEdge( source, target, eref );
		}

		computerGraph.releaseRef( vref2 );
		computerGraph.releaseRef( eref );

		return source;
	}


	/*
	 * PRIVATE METHODS.
	 */

	private void initializeAvailableSpotFeatureComputers()
	{
		this.availableSpotFeatureComputers =
				initializeFeatureComputers( SpotFeatureComputer.class );
	}

	private void initializeAvailableLinkFeatureComputers()
	{
		this.availableLinkFeatureComputers =
				initializeFeatureComputers( LinkFeatureComputer.class );
	}

	private < K extends FeatureComputer > Set< String > initializeFeatureComputers( final Class< K > cl )
	{
		final List< PluginInfo< K > > infos = pluginService.getPluginsOfType( cl );
		final Set< String > names = new HashSet<>( infos.size() );
		for ( final PluginInfo< K > info : infos )
		{
			final String name = info.getName();
			if ( availableFeatureComputers.keySet().contains( name ) )
			{
				logService.error( "Cannot register feature computer with name " + name + " of class " + cl +
						". There is already a feature computer registered with this name." );
				continue;
			}

			try
			{
				final K computer = info.createInstance();
				availableFeatureComputers.put( name, computer );
				names.add( name );
			}
			catch ( final InstantiableException e )
			{
				logService.error( "Could not instantiate computer  with name " + name + " of class " + cl +
						":\n" + e.getMessage() );
				e.printStackTrace();
			}
		}
		return Collections.unmodifiableSet( names );
	}

}
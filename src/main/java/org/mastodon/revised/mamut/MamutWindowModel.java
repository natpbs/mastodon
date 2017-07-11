package org.mastodon.revised.mamut;

import java.util.ArrayList;
import java.util.List;

import org.mastodon.revised.context.ContextProvider;
import org.mastodon.revised.mamut.BdvManager.BdvWindow;
import org.mastodon.revised.mamut.TrackSchemeManager.TsWindow;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleManager;
import org.mastodon.revised.ui.grouping.GroupManager;
import org.scijava.ui.behaviour.KeyPressedManager;
import org.scijava.ui.behaviour.io.InputTriggerConfig;

public class MamutWindowModel
{

	final KeyPressedManager keyPressedManager;

	final TrackSchemeStyleManager trackSchemeStyleManager;

	final InputTriggerConfig keyconf;

	final GroupManager groupManager;

	/**
	 * All currently open TrackScheme windows.
	 */
	final List< TsWindow > tsWindows = new ArrayList<>();

	/**
	 * All currently open BigDataViewer windows.
	 */
	final List< BdvWindow > bdvWindows = new ArrayList<>();

	/**
	 * The {@link ContextProvider}s of all currently open BigDataViewer windows.
	 */
	final List< ContextProvider< Spot > > contextProviders = new ArrayList<>();

	public MamutWindowModel( final InputTriggerConfig keyconf )
	{
		this.keyPressedManager = new KeyPressedManager();
		this.trackSchemeStyleManager = new TrackSchemeStyleManager();
		this.keyconf = keyconf;
		this.groupManager = new GroupManager();
	}

	public List< BdvWindow > getBdvWindows()
	{
		return bdvWindows;
	}

	public List< TsWindow > getTrackSchemeWindows()
	{
		return tsWindows;
	}

	public InputTriggerConfig getInputTriggerConfig()
	{
		return keyconf;
	}

}

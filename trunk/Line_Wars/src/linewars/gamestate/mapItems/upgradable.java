package linewars.gamestate.mapItems;

import linewars.configfilehandler.ConfigData;

public interface upgradable {

	public ConfigData getParser();
	
	public void forceReloadConfigData();
}

package linewars.gamestate.tech;

import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;

/**
 * 
 * @author John George, Taylor Bergquist
 *
 */
public interface Upgradable {

	public ConfigData getParser();
	
	public void forceReloadConfigData() throws FileNotFoundException, InvalidConfigFileException;
}

package linewars.gamestate.tech;

import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;

/**
 * Indicates that an object is Upgradable.
 * 
 * @author John George, Taylor Bergquist
 *
 */
public interface Upgradable {

	/**
	 * Returns the ConfigData that specifies the object.
	 * @return
	 */
	public ConfigData getParser();
	
	/**
	 * Forces the object to reload itself from its ConfigData object
	 * @throws FileNotFoundException
	 * @throws InvalidConfigFileException
	 */
	public void forceReloadConfigData() throws FileNotFoundException, InvalidConfigFileException;
}

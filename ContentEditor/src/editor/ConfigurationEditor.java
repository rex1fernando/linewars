package editor;

import javax.swing.JPanel;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

public interface ConfigurationEditor {

	/**
	 * Attempts to load the config data object cd. Throws an exception
	 * if the data is corrupted and cannot be loaded.
	 * 
	 * @param cd
	 */
	public void setData(ConfigData cd);
	
	/**
	 * Forces the editor to load the config data object even if it
	 * is corrupted. The editor will ignore corrupted data and
	 * only load valid data.
	 * 
	 * @param cd
	 */
	public void forceSetData(ConfigData cd);
	
	/**
	 * Resets the editor to an empty configuration
	 */
	public void reset();
	
	/**
	 * 
	 * @return	the ConfigData object associated with the data in the editor
	 */
	public ConfigData getData();
	
	/**
	 * 
	 * @return	true if the data in the editor is valid for the configData being specified,
	 * false otherwise
	 */
	public boolean isValid();
	
	/**
	 * 
	 * @return	the parserKey that represents the configData object configured
	 * in this editor
	 */
	public ParserKeys getType();
	
	/**
	 * 
	 * @return	The JPanel that shows all the configuration data for the panel
	 */
	public JPanel getPanel();
	
}

package editor;

import javax.swing.JPanel;

import configuration.Configuration;
import editor.BigFrameworkGuy.ConfigType;

public interface ConfigurationEditor {

	/**
	 * Attempts to load the config object cd. Throws an exception
	 * if the data is corrupted and cannot be loaded.
	 * 
	 * @param cd
	 */
	public void setData(Configuration cd);
	
	/**
	 * Resets the editor to an empty configuration
	 */
	public void reset();
	
	/**
	 * 
	 * @return	the config object associated with the data in the editor
	 */
	public Configuration getData();
	
	/**
	 * 
	 * @return	the ConfigType that represents the config object configured
	 * in this editor
	 */
	public ConfigType getType();
	
	/**
	 * 
	 * @return	The JPanel that shows all the configuration data for the panel
	 */
	public JPanel getPanel();
	
}

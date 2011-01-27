package editor;

import java.util.List;

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
	 * Instantiates a new configuration object (asking the user if the
	 * specific type is not immediately known) and returns that object.
	 * Also resets all the fields in the editor for the type of configuration
	 * that was returned.
	 * @return 
	 */
	public Configuration instantiateNewConfiguration();
	
	/**
	 * Takes in a configuration object and puts all of the data currently in
	 * the fields in the editor into the configuration object. Returns the
	 * type of the configuration object. If the configuration object does not
	 * match the type of configuration being edited in the editor, this method
	 * will throw an exception.
	 * 
	 * @param toSet TODO
	 * @return	the config object associated with the data in the editor
	 */
	public ConfigType getData(Configuration toSet);
	
	/**
	 * 
	 * @return	the ConfigType that represents the config object configured
	 * in this editor
	 */
	public List<ConfigType> getAllLoadableTypes();
	
	/**
	 * 
	 * @return	The JPanel that shows all the configuration data for the panel
	 */
	public JPanel getPanel();
	
}

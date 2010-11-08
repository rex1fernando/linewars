package editor.tech;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JPanel;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Function;
import linewars.gamestate.tech.Modifier;
import editor.BigFrameworkGuy;
import editor.ConfigurationEditor;

public class TechEditor implements ConfigurationEditor {

	private BigFrameworkGuy framework;
	private JPanel topLevel;
	private JPanel uriSelector;
	private JPanel keySelector;
	private JPanel functionPanel;
	
	private HashMap<String, HashMap<ParserKeys, Modifier>> modificationChain;
	private String name;
	private String tooltip;
	private Function costFunction;
	
	/**
	 * Forces the editor to load the config data object even if it
	 * is corrupted. The editor will ignore corrupted data and
	 * only load valid data.
	 * 
	 * @param cd
	 */
	@Override
	public void forceSetData(ConfigData cd) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @return	the ConfigData object associated with the data in the editor
	 */
	@Override
	public ConfigData getData() {
		ConfigData ret = new ConfigData();
		ret.add(ParserKeys.name, name);
		ret.add(ParserKeys.tooltip, tooltip);
		ret.add(ParserKeys.costFunction, costFunction.toConfigData());
		
		for(Entry<String, HashMap<ParserKeys, Modifier>> URI : modificationChain.entrySet()){
			ConfigData modURI = new ConfigData();
			modURI.add(ParserKeys.URI, URI.getKey());
			
			for(Entry<ParserKeys, Modifier> mod : modificationChain.get(URI.getKey()).entrySet()){
				ConfigData modKey = new ConfigData();
				modKey.add(ParserKeys.key, mod.getKey().toString());
				modKey.add(mod.getKey(), modificationChain.get(URI.getKey()).get(mod.getKey()).toConfigData());
				modURI.add(ParserKeys.modifiedKey, modKey);
			}
			
			ret.add(ParserKeys.modifiedURI, modURI);
		}
		
		return ret;
	}

	/**
	 * 
	 * @return	The JPanel that shows all the configuration data for the panel
	 */
	@Override
	public JPanel getPanel() {
		return topLevel;
	}

	@Override
	public ParserKeys getType() {
		return ParserKeys.techURI;
	}

	/**
	 * 
	 * @return	true if the data in the editor is valid for the configData being specified,
	 * false otherwise
	 */
	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Resets the editor to an empty configuration
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	/**
	 * Attempts to load the config data object cd. Throws an exception
	 * if the data is corrupted and cannot be loaded.
	 * 
	 * @param cd
	 */
	@Override
	public void setData(ConfigData cd) {
		// TODO Auto-generated method stub

	}

}

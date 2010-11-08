package editor.tech;

import java.awt.BorderLayout;
import java.awt.GridLayout;
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
	private JPanel bottomLevel;
	
	private NTCFEditor ntcf;
	private TechURISelector modifiedURIs;
	
	public TechEditor(){
		//init main components
		ntcf = new NTCFEditor();
		modifiedURIs = new TechURISelector();
		
		//set up layout
		topLevel = new JPanel();
		topLevel.setLayout(new BorderLayout());
		bottomLevel = new JPanel();
		bottomLevel.setLayout(new GridLayout(1, 2));
		
		//populate topLevel
		topLevel.add(ntcf.getPanel(), BorderLayout.PAGE_START);
		topLevel.add(bottomLevel, BorderLayout.CENTER);
		
		//populate bottomLevel
		bottomLevel.add(modifiedURIs.getPanel());
		//TODO add a dummy panel here? do we need to?
		
		//TODO does this have to be called here?
		reset();
	}
	
	/**
	 * Forces the editor to load the config data object even if it
	 * is corrupted. The editor will ignore corrupted data and
	 * only load valid data.
	 * 
	 * @param cd
	 */
	@Override
	public void forceSetData(ConfigData cd) {
		ntcf.forceSetData(cd);
		modifiedURIs.forceSetData(cd);
	}

	/**
	 * Resets the editor to an empty configuration
	 */
	@Override
	public void reset() {
		ntcf.reset();
		modifiedURIs.reset();
	}

	/**
	 * Attempts to load the config data object cd. Throws an exception
	 * if the data is corrupted and cannot be loaded.
	 * 
	 * @param cd
	 */
	@Override
	public void setData(ConfigData cd) {
		ntcf.setData(cd);
		modifiedURIs.setData(cd);
	}

	/**
	 * 
	 * @return	the ConfigData object associated with the data in the editor
	 */
	@Override
	public ConfigData getData() {
		ConfigData ntcfData = ntcf.getData();
		ConfigData modifiedURIsData = modifiedURIs.getData();
		ConfigData ret = new ConfigData();
		for(ParserKeys toAdd : ntcfData.getDefinedKeys()){
			ret.set(toAdd, ntcfData.getStringList(toAdd).toArray(new String[0]));
			ret.set(toAdd, ntcfData.getConfigList(toAdd).toArray(new ConfigData[0]));
		}
		for(ParserKeys toAdd : modifiedURIsData.getDefinedKeys()){
			ret.set(toAdd, modifiedURIsData.getStringList(toAdd).toArray(new String[0]));
			ret.set(toAdd, modifiedURIsData.getConfigList(toAdd).toArray(new ConfigData[0]));
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
		return ntcf.isValid() && modifiedURIs.isValid();
	}

}

package editor.tech;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import configuration.Configuration;

import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;

/**
 * 
 * @author Taylor Bergquist
 *
 */
public class TechEditor implements ConfigurationEditor {
	
	private JPanel topLevel;
	private JPanel bottomLevel;
	
	private NTCIEditor ntcf;
	private TechURISelector modifiedURIs;
	private TechKeySelector keySelector;
	
	public TechEditor(BigFrameworkGuy framework){
		//init main components
		ntcf = new NTCIEditor();
		keySelector = new TechKeySelector(framework);
		modifiedURIs = new TechURISelector(framework, keySelector);
		
		//set up layout
		topLevel = new JPanel();
		topLevel.setLayout(new BoxLayout(topLevel, BoxLayout.Y_AXIS));
		bottomLevel = new JPanel();
		bottomLevel.setLayout(new GridLayout(1, 2));
		
		//populate topLevel
		topLevel.add(ntcf.getPanel());
		topLevel.add(new JSeparator(JSeparator.HORIZONTAL));
		topLevel.add(bottomLevel);
		
		//populate bottomLevel
		bottomLevel.add(modifiedURIs.getPanel());
		bottomLevel.add(keySelector.getPanel());
		
		keySelector.getPanel().setVisible(false);
		
		topLevel.setPreferredSize(new Dimension(800, 700));
		
		instantiateNewConfiguration();
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
	public Configuration instantiateNewConfiguration() {
		ntcf.instantiateNewConfiguration();
		modifiedURIs.instantiateNewConfiguration();
	}

	/**
	 * Attempts to load the config data object cd. Throws an exception
	 * if the data is corrupted and cannot be loaded.
	 * 
	 * @param cd
	 */
	@Override
	public void setData(Configuration cd) {
		ntcf.setData(cd);
		modifiedURIs.setData(cd);
	}

	/**
	 * 
	 * @return	the ConfigData object associated with the data in the editor
	 */
	@Override
	public ConfigType getData(Configuration toSet) {
		ConfigData ntcfData = ntcf.getData(null);
		ConfigData modifiedURIsData = modifiedURIs.getData(null);
		ConfigData ret = new ConfigData();
		for(ParserKeys toAdd : ntcfData.getDefinedKeys()){
			ret.add(toAdd, ntcfData.getStringList(toAdd).toArray(new String[0]));
			ret.add(toAdd, ntcfData.getConfigList(toAdd).toArray(new ConfigData[0]));
		}
		for(ParserKeys toAdd : modifiedURIsData.getDefinedKeys()){
			ret.add(toAdd, modifiedURIsData.getStringList(toAdd).toArray(new String[0]));
			ret.add(toAdd, modifiedURIsData.getConfigList(toAdd).toArray(new ConfigData[0]));
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
	public List<ConfigType> getAllLoadableTypes() {
		return ParserKeys.techURI;
	}

	/**
	 * 
	 * @return	true if the data in the editor is valid for the configData being specified,
	 * false otherwise
	 */
	@Override
	public boolean isValidConfig() {
		return ntcf.isValidConfig() && modifiedURIs.isValidConfig();
	}

}

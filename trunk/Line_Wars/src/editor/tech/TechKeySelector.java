package editor.tech;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import editor.BigFrameworkGuy;
import editor.ConfigurationEditor;
import editor.ListURISelector;
import editor.ListURISelector.ListSelectorOptions;

public class TechKeySelector implements ConfigurationEditor {
	
	private HashMap<ParserKeys, ConfigData> modifiers;
	private String URI;
	
	private JPanel panel;
	
	private ListURISelector keySelector;

	private ParserKeys[] currentlyHighlightedKeys;
	
	private ListSelectorOptions keySelectorOptions = new ListSelectorOptions(){

		@Override
		public String[] getOptions() {
			ParserKeys[] enumForm = ParserKeys.values();
			String[] ret = new String[enumForm.length];
			for(int i = 0; i < ret.length; i++){
				ret[i] = enumForm[i].toString();
			}
			return ret;
		}

		@Override
		public void uriSelected(String uri) {
			ParserKeys key = ParserKeys.valueOf(uri);
			modifiers.put(key, new ConfigData());
		}

		@Override
		public void uriRemoved(String uri) {
			ParserKeys key = ParserKeys.valueOf(uri);
			modifiers.remove(key);
			currentlyHighlightedKeys = new ParserKeys[0];
		}

		@Override
		public void uriHighlightChange(String[] uris) {
			currentlyHighlightedKeys = new ParserKeys[uris.length];
			for(int i = 0; i < uris.length; i++){
				currentlyHighlightedKeys[i] = ParserKeys.valueOf(uris[i]);
			}
			updateCurrentlyHighlighted();
		}
	};

	public TechKeySelector(BigFrameworkGuy framework) {
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		keySelector = new ListURISelector("Key", keySelectorOptions);
		panel.add(keySelector);

		//TODO add a button that is only visible when exactly one thing is selected that does the function stuff
		
		reset();
	}

	protected void updateCurrentlyHighlighted() {
		if(currentlyHighlightedKeys.length == 1){
			//TODO make that one button visible
		}else{
			//TODO make that one button not visible
			//TODO kill the function panel thingy?
		}
	}

	@Override
	public void setData(ConfigData cd) {//TODO make sure the implementation of this saves the old version, or drastic measures must be taken!
		// TODO Auto-generated method stub

	}

	@Override
	public void forceSetData(ConfigData cd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		currentlyHighlightedKeys = new ParserKeys[0];
		modifiers = new HashMap<ParserKeys, ConfigData>();
		URI = null;
		// TODO Auto-generated method stub

	}

	@Override
	public ConfigData getData() {
		ConfigData ret = new ConfigData();
		ret.add(ParserKeys.URI, URI);
		for(Entry<ParserKeys, ConfigData> modifiedKey : modifiers.entrySet()){
			modifiedKey.getValue().set(ParserKeys.key, modifiedKey.getKey().toString());
			ret.add(ParserKeys.modifiedKey, modifiedKey.getValue());
		}
		return ret;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ParserKeys getType() {
		return ParserKeys.modifiedKey;
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	
	//returns true if the modification can be legalized
	//modifies it so it is legal if it can be made so
	boolean legalizeUpgradableModification(ConfigData modifiedUpgradable) {
		String uri = null;
		try{
			uri = modifiedUpgradable.getString(ParserKeys.URI);
		}catch(NoSuchKeyException e){
			return false;
		}
		
		//If nothing is modified for this URI, this is easy!
		List<ConfigData> modifiedKeys = null;
		try{
			modifiedKeys = modifiedUpgradable.getConfigList(ParserKeys.modifiedKey);
		}catch(NoSuchKeyException e){
			return true;
		}
		
		//Keep only the legal modifications
		for(ConfigData modifiedKey : modifiedKeys){
			if(!legalizeModification(modifiedKey, uri)){
				modifiedKeys.remove(modifiedKey);
			}
		}
		modifiedUpgradable.set(ParserKeys.modifiedKey, modifiedKeys.toArray(new ConfigData[0]));
		return true;
	}

	//means syntactically correct and complete
	boolean upgradableModificationIsValid(ConfigData modifiedUpgradable){
		//make sure there is a URI here
		String uri = null;
		try{
			uri = modifiedUpgradable.getString(ParserKeys.URI);
		}catch(NoSuchKeyException e){
			return false;
		}
		
		//Make sure something is modified for this URI
		List<ConfigData> modifiedKeys = null;
		try{
			modifiedKeys = modifiedUpgradable.getConfigList(ParserKeys.modifiedKey);
		}catch(NoSuchKeyException e){
			return false;
		}
		
		//Make sure all the modifications are legal
		for(ConfigData modifiedKey : modifiedKeys){
			if(!modificationIsValid(modifiedKey, uri)){
				return false;
			}
		}
		
		return false;
	}
	

	
	//TODO put these elsewhere?
	private boolean legalizeModification(ConfigData modifiedKey, String uri) {
		//check that there is a key
		try{
			String key = modifiedKey.getString(ParserKeys.key);
		}catch(NoSuchKeyException e){
			return false;
		}
		// TODO Auto-generated method stub
		return false;
	}
	
	private boolean modificationIsValid(ConfigData modifiedKey, String uri) {
		//check that there is a key
		try{
			String key = modifiedKey.getString(ParserKeys.key);
		}catch(NoSuchKeyException e){
			return false;
		}
		// TODO Auto-generated method stub
		return false;
	}
}

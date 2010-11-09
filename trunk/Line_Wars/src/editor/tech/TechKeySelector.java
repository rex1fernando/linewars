package editor.tech;

import java.util.List;

import javax.swing.JPanel;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import editor.ConfigurationEditor;

public class TechKeySelector implements ConfigurationEditor {

	@Override
	public void setData(ConfigData cd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void forceSetData(ConfigData cd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public ConfigData getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ParserKeys getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return null;
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

package editor.tech.modifierEditors;

import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import linewars.gamestate.tech.ModifierConfiguration;

import configuration.Configuration;
import configuration.Property;
import configuration.Usage;
import editor.BigFrameworkGuy.ConfigType;

public class ConfigurationMultipleModificationEditor extends NewModifierEditor {
	
	private Usage validUsage = Usage.CONFIGURATION;
	private Configuration template;
	
	private MultipleSubModificationModification data;
	
	private JPanel panel;

	public ConfigurationMultipleModificationEditor(Property property) {
		if(property.getUsage() != validUsage){
			//TODO do something here?
		}
		
		template = (Configuration) property.getValue();
		
		//TODO set up that JPanel
		//we need a list selector to select a list of names to modify (from the names defined by template)
		//the user should be able to highlight one of the names in the list to modify that sub-modification
		//should the list selector observe data? Or should it manipulate data? both?
	}

	//if the list selector is observing data, we just have to modify data and the list selector will keep up
	//if the list selector is manipulating data, we should call stuff in list selector and let it modify data...
	//either way, template won't be changing...
	@Override
	public void setData(Configuration cd) {
		//TODO
		//cast cd to toCopy
		
		//empty data
		
		//for each key in toCopy
			//data.put(toCopy.get(key))
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new MultipleSubModificationModification();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		MultipleSubModificationModification target = (MultipleSubModificationModification) toSet;
		//empty it
		Set<String> keySet = target.getModifiedPropertyNames();
		for(String key : keySet){
			target.removeSubModification(key);
		}
		
		//for each key in data
		for(String key : data.getModifiedPropertyNames()){
			target.setSubModification(key, data.getSubModification(key));
		}
		return ConfigType.modification;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public ModifierConfiguration getData() {
		MultipleSubModificationModification ret = new MultipleSubModificationModification();
		for(String key : data.getPropertyNames()){
			ret.setSubModification(key, data.getSubModification(key));
		}
		return ret;
	}

	@Override
	public void resetEditor() {
		throw new UnsupportedOperationException();
	}
}

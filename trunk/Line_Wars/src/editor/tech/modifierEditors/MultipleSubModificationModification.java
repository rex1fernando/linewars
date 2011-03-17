package editor.tech.modifierEditors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import configuration.Configuration;
import configuration.Property;
import configuration.Usage;
import linewars.gamestate.tech.ModifierConfiguration;

public class MultipleSubModificationModification extends ModifierConfiguration {
	
	/**
	 * Change this iff you change internal state variables...
	 */
	private static final long serialVersionUID = 7996061891926409553L;
	
	private static final String description = "Modify sub-configurations of this configuration.";
	
	static{
		ModifierConfiguration.addModifierForUsage(Usage.CONFIGURATION, MultipleSubModificationModification.class, description);
	}
	
	//modifies a configuration
	private Usage validUsage = Usage.CONFIGURATION;
	//contains multiple sub-modifications
	private Map<String, ModifierConfiguration> subModifications;

	@Override
	public Property applyTo(Property toModify) {
		if(toModify.getUsage() != validUsage){
			//TODO exception?
		}
		
		Configuration config = (Configuration) toModify.getValue();
		
		for(String key : subModifications.keySet()){
			subModifications.get(key).applyTo(config.getPropertyForName(key));
		}
		
		return toModify;
	}
	
	public MultipleSubModificationModification(){
		subModifications = new HashMap<String, ModifierConfiguration>();
	}

	public ModifierConfiguration removeSubModification(String key){
		removeProperty(key);
		return subModifications.remove(key);
	}
	
	public ModifierConfiguration getSubModification(String key){
		return subModifications.get(key);
	}
	
	public ModifierConfiguration setSubModification(String key, ModifierConfiguration config){
		setPropertyForName(key, new Property(Usage.CONFIGURATION, config));
		ModifierConfiguration ret = subModifications.get(key);
		subModifications.put(key, config);
		return ret;
	}
	
	public Set<String> getModifiedPropertyNames(){
		return subModifications.keySet();
	}
}

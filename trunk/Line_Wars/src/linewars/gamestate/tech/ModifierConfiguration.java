package linewars.gamestate.tech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utility.ForceLoadPackage;

import configuration.Configuration;
import configuration.Property;
import configuration.Usage;

public abstract class ModifierConfiguration extends Configuration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4468407517465792016L;
	private static HashMap<Usage, List<Class<? extends ModifierConfiguration>>> validModifiersForUsage;
	
	static{
		ForceLoadPackage.forceLoadClassesInPackage(ModifierConfiguration.class.getPackage());
	}
	
	public static void addModifierForUsage(Usage key, Class<? extends ModifierConfiguration> value){
		if(validModifiersForUsage == null){
			validModifiersForUsage = new HashMap<Usage, List<Class<? extends ModifierConfiguration>>>();
		}
		if(validModifiersForUsage.get(key) == null){
			validModifiersForUsage.put(key, new ArrayList<Class<? extends ModifierConfiguration>>());
		}
		validModifiersForUsage.get(key).add(value);
	}
	
	public static List<Class<? extends ModifierConfiguration>> getModifiersForUsage(Usage key){
		return validModifiersForUsage.get(key);
	}

	public abstract Property applyTo(Property toModify);
}

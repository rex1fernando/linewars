package linewars.gamestate.tech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import utility.ForceLoadPackage;

import configuration.Configuration;
import configuration.Property;
import configuration.Usage;

public abstract class ModifierConfiguration extends Configuration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4468407517465792016L;
	private static HashMap<Usage, List<ModifierMetaData>> validModifiersForUsage;
	
	static{
		ForceLoadPackage.forceLoadClassesInPackage(ModifierConfiguration.class.getPackage());
	}
	
	public static Class<? extends ModifierConfiguration> promptUserToSelectModificationType(JPanel location, List<ModifierMetaData> validModifications){
		return null;
	}
	
	public static void addModifierForUsage(Usage key, Class<? extends ModifierConfiguration> modifierType, String description){
		if(validModifiersForUsage == null){
			validModifiersForUsage = new HashMap<Usage, List<ModifierMetaData>>();
		}
		if(validModifiersForUsage.get(key) == null){
			validModifiersForUsage.put(key, new ArrayList<ModifierMetaData>());
		}
		ModifierMetaData toAdd = new ModifierMetaData(modifierType, description);
		validModifiersForUsage.get(key).add(toAdd);
	}
	
	public static List<ModifierMetaData> getModifiersForUsage(Usage key){
		return validModifiersForUsage.get(key);
	}

	public abstract Property applyTo(Property toModify);
	
	public static class ModifierMetaData{
		private final Class<? extends ModifierConfiguration> modifier;
		private final String description;
		
		public ModifierMetaData(Class<? extends ModifierConfiguration> modifier, String description){
			this.modifier = modifier;
			this.description = description;
		}
		
		public Class<? extends ModifierConfiguration> getModifier() {
			return modifier;
		}
		
		public String getDescription() {
			return description;
		}
	}
}

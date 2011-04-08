package editor.tech.modifierEditors;

import configuration.Configuration;
import configuration.Property;
import configuration.Usage;
import linewars.gamestate.tech.ModifierConfiguration;

public class SetConfigurationModification extends ModifierConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2316812298495798345L;

	static{
		ModifierConfiguration.addModifierForUsage(Usage.CONFIGURATION, SetConfigurationModification.class, "Set this sub-Configuration to a completely different Configuration.");
		ModifierConfiguration.addModifierForUsage(Usage.ANIMATION, SetConfigurationModification.class, "Set this sub-Configuration to a completely different Configuration.");
	}
	
	private static final Usage validUsage = Usage.CONFIGURATION;
	private static final Usage validUsage2 = Usage.ANIMATION;
	
	private static final String replacementName = "replacement";
	
	@Override
	public Property applyTo(Property toModify) {
		if(toModify.getUsage() != validUsage && toModify.getUsage() != validUsage2){
			throw new IllegalArgumentException();
		}
		
		return this.getPropertyForName(replacementName);
	}

	public Configuration getReplacement(){
		return (Configuration) this.getPropertyForName(replacementName).getValue();
	}
	
	public void setReplacement(Configuration replacement){
		this.setPropertyForName(replacementName, new Property(Usage.CONFIGURATION, replacement));
	}
}

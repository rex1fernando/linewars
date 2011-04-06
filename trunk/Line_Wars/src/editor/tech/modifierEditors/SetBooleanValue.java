package editor.tech.modifierEditors;

import linewars.gamestate.tech.ModifierConfiguration;
import configuration.Property;
import configuration.Usage;

public class SetBooleanValue extends ModifierConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6542265643625193251L;

	static{
		ModifierConfiguration.addModifierForUsage(Usage.BOOLEAN, SetBooleanValue.class, "Sets the value of a boolean variable.");
	}
	
	private static final Usage validUsage = Usage.BOOLEAN;
	
	private static final Usage targetValueUsage = Usage.BOOLEAN;
	private static final String targetValueName = "targetValue";
	
	public void setTargetValue(boolean target){
		this.setPropertyForName(targetValueName, new Property(targetValueUsage, target));
	}
	
	public boolean getTargetValue(){
		return (Boolean) this.getPropertyForName(targetValueName).getValue();
	}
	
	@Override
	public Property applyTo(Property toModify) {
		if(toModify.getUsage() != validUsage){
			throw new IllegalArgumentException(getClass() + " can not modify Properties of type " + toModify.getUsage());
		}
		return this.getPropertyForName(targetValueName);
	}

}

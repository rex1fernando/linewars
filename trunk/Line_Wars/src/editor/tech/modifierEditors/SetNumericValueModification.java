package editor.tech.modifierEditors;

import configuration.Property;
import configuration.Usage;
import linewars.gamestate.tech.ModifierConfiguration;

public strictfp class SetNumericValueModification extends ModifierConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4692519798610062670L;

	static{
		ModifierConfiguration.addModifierForUsage(Usage.NUMERIC_FLOATING_POINT, SetNumericValueModification.class, "Sets the value of a floating point variable.");
		ModifierConfiguration.addModifierForUsage(Usage.NUMERIC_INTEGER, SetNumericValueModification.class, "Sets the value of an integer variable.");
	}
	
	private static final Usage fpUsage = Usage.NUMERIC_FLOATING_POINT;
	
	private static final String fpValueName = "fpValue";
	
	public void setTargetValue(double targetValue){
		this.setPropertyForName(fpValueName, new Property(fpUsage, new Double(targetValue)));
	}
	
	public double getTargetValue(){
		return (Double) this.getPropertyForName(fpValueName).getValue();
	}
	
	@Override
	public Property applyTo(Property toModify) {
		if(toModify.getUsage() == fpUsage){
			return this.getPropertyForName(fpValueName);
		}else if(toModify.getUsage() == Usage.NUMERIC_INTEGER){
			return new Property(Usage.NUMERIC_INTEGER, new Integer(((Double) this.getPropertyForName(fpValueName).getValue()).intValue()));
		}
		throw new IllegalArgumentException();
	}

}

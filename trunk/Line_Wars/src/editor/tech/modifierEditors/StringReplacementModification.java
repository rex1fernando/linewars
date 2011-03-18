package editor.tech.modifierEditors;

import utility.Observable;
import utility.Observer;
import configuration.Configuration;
import configuration.Property;
import configuration.Usage;
import linewars.gamestate.tech.ModifierConfiguration;

public class StringReplacementModification extends ModifierConfiguration implements Observer {
	
	/**
	 * DO NOT TOUCH THIS (UNLESS YOU CHANGE STATE SUCH THAT OLD OBJECTS WILL BE INCOMPATIBLE)
	 */
	private static final long serialVersionUID = -4823817927869532598L;

	static{
		ModifierConfiguration.addModifierForUsage(Usage.STRING, StringReplacementModification.class, "Replaces the string being modified with another string.");
	}
	
	private static final Usage validUsage = Usage.STRING;
	
	private static final Usage replacementUsage = Usage.STRING;
	private static final String replacementKey = "replacement";
	private String replacement;
	
	public StringReplacementModification(){
		this.addObserver(this);
	}
	
	public void setReplacement(String replacement){
		this.setPropertyForName(replacementKey, new Property(replacementUsage, replacement));
	}
	
	public String getReplacement(){
		return replacement;
	}

	@Override
	public Property applyTo(Property toModify) {
		if(toModify.getUsage() != validUsage){
			//TODO do something here
		}
		return new Property(replacementUsage, replacement);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == null){
			return;
		}
		if(!this.equals(o)){
			return;
		}
		if(!replacementKey.equals(arg)){
			return;
		}
		replacement = (String) ((Configuration) o).getPropertyForName(replacementKey).getValue();
	}

}

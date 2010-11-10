package editor.tech;

import javax.swing.JPanel;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import editor.ConfigurationEditor;

/**
 * This ConfigurationEditor allows a user to edit a single Modifier.
 * 
 * It first allows the user to select a modifier type (which, for now, is only numeric),
 * then allows them to configure a Modifier of the type they chose.
 * 
 * This configuration should happen in another ConfigurationEditor; there should be one class
 * implementing ConfigurationEditor for each modifier type.
 * 
 * @author knexer
 *
 */
public class ModifierEditor implements ConfigurationEditor {
	
	private JPanel panel = new JPanel();
	
	private ConfigurationEditor currentModifierTypeEditor;

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
	public boolean isValidConfig() {
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
		return panel;
	}

	/**
	 * Computes whether the given ConfigData represents a valid (meaning both syntactically legal AND complete) modifier
	 * @param URI
	 * The URI of the Upgradable being modified.
	 * @param modifier
	 * The ConfigData representing the current configuration of the modifier.
	 * @return
	 * true iff the modifier is valid
	 */
	public boolean modifierIsValid(String URI, ConfigData modifier){
		//TODO
		return true;
	}
	
	/**
	 * Sanitizes the given ConfigData so that it comes to represent a legal modifier, if possible.
	 * Even if this method returns false, it will change the ConfigData given to it.
	 * @param URI
	 * The URI of the Upgradable being modified.
	 * @param modifier
	 * The ConfigData representing the current configuration of the modifier, and where the valid configuration is stored
	 * @return
	 * true iff the modifier could be sanitized
	 */
	public boolean legalizeModifier(String URI, ConfigData modifier){
		//TODO
		return true;
	}
	
	/**
	 * Returns an array of ParserKeys which contains every key that can be legally Modified for the given URI.
	 * 
	 * @param uri
	 * @return
	 */
	public static ParserKeys[] getModifiableKeysForURI(String uri){
		//TODO
		ParserKeys[] enumForm = ParserKeys.values();
		return enumForm;
	}
	
}

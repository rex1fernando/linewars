package editor.tech;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;

import configuration.Configuration;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ParserKeys;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
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
 * @author Taylor Bergquist
 *
 */
public class ModifierEditor implements ConfigurationEditor, ActionListener {
	
	private BigFrameworkGuy bfg;
	
	private JPanel panel;
	private JPanel buttonPanel;
	private HashMap<JButton, ModifierConfigurationEditor> subEditors;
	private HashMap<String, ModifierConfigurationEditor> typeToEditor;
	
	private HashMap<String, ParserKeys[]> unitValidModifiers;
	private HashMap<String, ParserKeys[]> buildingValidModifiers;
	private HashMap<String, ParserKeys[]> abilityValidModifiers;
	private HashMap<String, ParserKeys[]> projectileValidModifiers;
	
	private ModifierConfigurationEditor currentModifierTypeEditor;
	
	public ModifierEditor(TechKeySelector parent, BigFrameworkGuy framework){
		bfg = framework;
		
		panel = new JPanel();
		buttonPanel = new JPanel();
		subEditors = new HashMap<JButton, ModifierConfigurationEditor>();
		typeToEditor = new HashMap<String, ModifierConfigurationEditor>();
		
		unitValidModifiers = new HashMap<String, ParserKeys[]>();
		buildingValidModifiers = new HashMap<String, ParserKeys[]>();
		abilityValidModifiers = new HashMap<String, ParserKeys[]>();
		projectileValidModifiers = new HashMap<String, ParserKeys[]>();
		
		initializeSubEditors();
		
		panel.setLayout(new BorderLayout());
		panel.add(buttonPanel, BorderLayout.PAGE_START);
	}

	private void initializeSubEditors() {
		initializeSubEditor(new NumericModifierEditor(bfg));
	}
	
	private void initializeSubEditor(ModifierConfigurationEditor editor){
		JButton editorButton = new JButton(editor.getName());
		subEditors.put(editorButton, editor);
		typeToEditor.put(editor.getName(), editor);
		
		editorButton.addActionListener(this);
		
		unitValidModifiers.put(editor.getName(), editor.getValidUnitModifiers());
		buildingValidModifiers.put(editor.getName(), editor.getValidBuildingModifiers());
		abilityValidModifiers.put(editor.getName(), editor.getValidAbilityModifiers());
		projectileValidModifiers.put(editor.getName(), editor.getValidProjectileModifiers());
		
		buttonPanel.add(editorButton);
	}

	@Override
	public void setData(Configuration cd) {
		instantiateNewConfiguration();

		//figure out which editor should be used
		String modifierType = cd.getString(ParserKeys.modifiertype);
		ModifierConfigurationEditor properEditor = typeToEditor.get(modifierType);
		if(properEditor == null){
			throw new IllegalArgumentException(modifierType + " is not a modifier type!");
		}
		currentModifierTypeEditor = properEditor;

		//set its data
		properEditor.setData(cd);
	}

	@Override
	public void forceSetData(ConfigData cd) {
		instantiateNewConfiguration();
		
		try{
			String strKey = cd.getString(ParserKeys.key);
			ParserKeys key = ParserKeys.getKey(strKey);
			setModifiedKey(key);
		}catch(NoSuchKeyException e){
			throw new IllegalArgumentException(cd + " does not define the key to be modified!");
		}
		
		ConfigData subModifier = null;
		try{
			subModifier = cd.getConfig(ParserKeys.modifier);
		}catch(NoSuchKeyException e){
			return;
		}

		//figure out which editor should be used
		String modifierType = null;
		try{
			modifierType = subModifier.getString(ParserKeys.modifiertype);
		}catch(NoSuchKeyException e){
			//if the modifiertype is not there, we give up
			return;
		}
		ModifierConfigurationEditor properEditor = typeToEditor.get(modifierType);
		if(properEditor == null){
			//if the modifiertype is not valid, we give up
			return;
		}
		currentModifierTypeEditor = properEditor;

		//set its data
		properEditor.forceSetData(cd);
		//enable the editor
		enableSubEditor(properEditor);
	}

	private void setModifiedKey(ParserKeys key) {
		// TODO Auto-generated method stub
		for(ModifierConfigurationEditor subEditor : typeToEditor.values()){
			subEditor.setModifiedKey(key);
		}
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		//reset this class' panel
		if(currentModifierTypeEditor != null){
			panel.remove(currentModifierTypeEditor.getPanel());
		}
		//reset this class' state
		currentModifierTypeEditor = null;
		
		//reset every subeditor
		for(ConfigurationEditor toReset : subEditors.values()){
			toReset.instantiateNewConfiguration();
		}
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		if(currentModifierTypeEditor == null){
			return new ConfigData();
		}
		ConfigData ret = currentModifierTypeEditor.getData(null);
		ret.set(ParserKeys.modifiertype, currentModifierTypeEditor.getName());
		return ret;
	}

	@Override
	public boolean isValidConfig() {
		if(currentModifierTypeEditor == null){
			return false;
		}
		return currentModifierTypeEditor.isValidConfig();
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		return ParserKeys.modifier;
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
		String type = null;
		String key = null;
		try{
			key = modifier.getString(ParserKeys.key);
		}catch(NoSuchKeyException e){
			return false;
		}
		try{
			ParserKeys.getKey(key);
		}catch(IllegalArgumentException e){
			return false;
		}
		
		//get the config that defines the actual modifier
		ConfigData subConfig = null;
		try{
			subConfig = modifier.getConfig(ParserKeys.modifier);
		}catch(NoSuchKeyException e){
			return false;
		}
		try{
			type = subConfig.getString(ParserKeys.modifiertype);
		}catch(NoSuchKeyException e){
			return false;
		}
		ModifierConfigurationEditor properEditor = typeToEditor.get(type);
		if(properEditor == null){
			return false;
		}
		return properEditor.modifierIsValid(URI, modifier);
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
		String type = null;
		String key = null;
		try{
			key = modifier.getString(ParserKeys.key);
		}catch(NoSuchKeyException e){
			return false;
		}
		try{
			ParserKeys.getKey(key);
		}catch(IllegalArgumentException e){
			return false;
		}
		
		//get the config that defines the actual modifier
		ConfigData subConfig = null;
		try{
			subConfig = modifier.getConfig(ParserKeys.modifier);
		}catch(NoSuchKeyException e){
			return true;//if no modifier was defined, this is easy
		}
		try{
			type = subConfig.getString(ParserKeys.modifiertype);
		}catch(NoSuchKeyException e){
			modifier.set(ParserKeys.modifier, new ConfigData());
			return true;//if no type is defined, this is still easy
		}
		ModifierConfigurationEditor properEditor = typeToEditor.get(type);
		if(properEditor == null){
			modifier.set(ParserKeys.modifier, new ConfigData());
			return true;
		}
		return properEditor.legalizeModifier(URI, modifier);
	}
	
	/**
	 * Returns an array of ParserKeys which contains every key that can be legally Modified for the given URI.
	 * 
	 * @param uri
	 * @return
	 */
	public ParserKeys[] getModifiableKeysForURI(String uri){
		Set<ParserKeys> allValidKeys = new HashSet<ParserKeys>();
		for(ModifierConfigurationEditor currentEditor : typeToEditor.values()){
			if(stringIsInArray(bfg.getUnitURIs(), uri)){
				ParserKeys[] candidates = currentEditor.getValidUnitModifiers();
				for(ParserKeys toAdd : candidates){
					allValidKeys.add(toAdd);
				}
			}
			if(stringIsInArray(bfg.getAbilityURIs(), uri)){
				ParserKeys[] candidates = currentEditor.getValidAbilityModifiers();
				for(ParserKeys toAdd : candidates){
					allValidKeys.add(toAdd);
				}				
			}
			if(stringIsInArray(bfg.getBuildingURIs(), uri)){
				ParserKeys[] candidates = currentEditor.getValidBuildingModifiers();
				for(ParserKeys toAdd : candidates){
					allValidKeys.add(toAdd);
				}				
			}
			if(stringIsInArray(bfg.getProjectileURIs(), uri)){
				ParserKeys[] candidates = currentEditor.getValidProjectileModifiers();
				for(ParserKeys toAdd : candidates){
					allValidKeys.add(toAdd);
				}				
			}
		}
		ParserKeys[] ret = new ParserKeys[allValidKeys.size()];
		int i = 0;
		for(ParserKeys toRet : allValidKeys){
			ret[i] = toRet;
			i++;
		}
		return ret;
	}
	

	
	private boolean stringIsInArray(String[] arr, String query){
		for(String toTest : arr){
			if(toTest.equals(query)){
				return true;
			}
		}
		return false;		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();//we know its a JButton
		for(Entry<JButton, ModifierConfigurationEditor> entry : subEditors.entrySet()){
			if(source.equals(entry.getKey())){
				enableSubEditor(entry.getValue());
				return;
			}
		}
	}

	private void enableSubEditor(ModifierConfigurationEditor value) {
		currentModifierTypeEditor = value;
		panel.add(currentModifierTypeEditor.getPanel(), BorderLayout.CENTER);
		currentModifierTypeEditor.getPanel().setVisible(true);
		panel.validate();
	}
	
}

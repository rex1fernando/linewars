package editor.tech;

import java.util.List;

import javax.swing.JPanel;

import editor.BigFrameworkGuy;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

public class NumericModifierEditor implements ModifierConfigurationEditor {
	
	private ParserKeys modifiedKey;
	private FunctionEditor fEditor;
	private BigFrameworkGuy bfg;
	
	public NumericModifierEditor(BigFrameworkGuy guy) 
	{
		bfg = guy;
		fEditor = new FunctionEditor();
		modifiedKey = null;
	}

	@Override
	public void setData(ConfigData cd) {
		forceSetData(cd);
	}

	private boolean isValid(ConfigData cd) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void forceSetData(ConfigData cd) {
		reset();
		String strKey = cd.getString(ParserKeys.key);
		modifiedKey = ParserKeys.getKey(strKey);
		ConfigData modifier = cd.getConfig(ParserKeys.modifier);
		//ignore modifiertype; modifierisvalid handles that
		if(modifier.getDefinedKeys().contains(ParserKeys.valueFunction)){
			fEditor.forceSetData(modifier.getConfig(ParserKeys.valueFunction));
		}
	}

	@Override
	public void reset() {
		modifiedKey = null;
		fEditor.reset();
	}

	@Override
	public ConfigData getData() {
		ConfigData ret = new ConfigData();
		ret.set(ParserKeys.key, modifiedKey.toString());
		ConfigData modifier = new ConfigData();
		ret.set(ParserKeys.modifier, modifier);
		modifier.set(ParserKeys.modifiertype, getName());
		modifier.set(ParserKeys.valueFunction, fEditor.getData());
		return ret;
	}

	@Override
	public boolean isValidConfig() {
		return isValid(getData());
	}

	@Override
	public ParserKeys getType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JPanel getPanel() {
		return fEditor;
	}

	@Override
	public boolean modifierIsValid(String URI, ConfigData modifier) {
		
		ParserKeys[] validKeys = null;
		//first figure out what type of uri this is
		for(String u : bfg.getAbilityURIs())
			if(u.equals(URI)) //HEY! its an ability
				validKeys = this.getValidAbilityModifiers();
		for(String u : bfg.getBuildingURIs())
			if(u.equals(URI)) //HEY! its a building
				validKeys = this.getValidBuildingModifiers();
		for(String u : bfg.getProjectileURIs())
			if(u.equals(URI)) //HEY! its a projectile
				validKeys = this.getValidProjectileModifiers();
		for(String u : bfg.getUnitURIs())
			if(u.equals(URI)) //HEY! its a unit
				validKeys = this.getValidUnitModifiers();
		
		//make sure we found out what the uri was
		if(validKeys == null)
			return false;
		
		//make sure the key being modified is in the list of valid keys
		boolean found = false;
		for(ParserKeys key : validKeys)
			if(key.toString().equalsIgnoreCase(modifier.getString(ParserKeys.key)))
				found = true;
		
		//if its not, then return false
		if(!found)
			return false;
		
		//make sure the modifier config is there
		if(!modifier.getDefinedKeys().contains(ParserKeys.modifier) || 
				modifier.getConfig(ParserKeys.modifier) == null)
			return false;
		
		//from now on use the inner config
		modifier = modifier.getConfig(ParserKeys.modifier);
		
		//make sure modified type is correct
		if(!modifier.getDefinedKeys().contains(ParserKeys.modifiertype) ||
				modifier.getString(ParserKeys.modifiertype) == null ||
				!modifier.getString(ParserKeys.modifiertype).equalsIgnoreCase(this.getName()))
			return false;
		
		//make sure value function is defined
		if(!modifier.getDefinedKeys().contains(ParserKeys.valueFunction) || 
				modifier.getConfig(ParserKeys.valueFunction) == null)
			return false;
		
		ConfigData cd = modifier.getConfig(ParserKeys.valueFunction); 
		
		//holy shit if statement
		if (cd.getDefinedKeys().contains(ParserKeys.functionType)
				&& cd.getString(ParserKeys.functionType) != null
				&& cd.getDefinedKeys().contains(ParserKeys.coefficients)
				&& cd.getNumberList(ParserKeys.coefficients).size() > 0)
		{
			if(cd.getString(ParserKeys.functionType).equalsIgnoreCase("exponential"))
			{
				List<Double> cos = cd.getNumberList(ParserKeys.coefficients);
				if(cos.size() != 3)
					return false;
			}
			else if(cd.getString(ParserKeys.functionType).equalsIgnoreCase("polynomial"))
			{
				List<Double> cos = cd.getNumberList(ParserKeys.coefficients);
				if(cos.size() <= 0)
					return false;
			}
			else
				return false;
		}
		else
			return false;
		
		return true;
	}

	@Override
	public boolean legalizeModifier(String URI, ConfigData modifier) {
		if(!modifier.getDefinedKeys().contains(ParserKeys.key)){
			return false;
		}
		
		ParserKeys[] validKeys = null;
		//first figure out what type of uri this is
		for(String u : bfg.getAbilityURIs())
			if(u.equals(URI)) //HEY! its an ability
				validKeys = this.getValidAbilityModifiers();
		for(String u : bfg.getBuildingURIs())
			if(u.equals(URI)) //HEY! its a building
				validKeys = this.getValidBuildingModifiers();
		for(String u : bfg.getProjectileURIs())
			if(u.equals(URI)) //HEY! its a projectile
				validKeys = this.getValidProjectileModifiers();
		for(String u : bfg.getUnitURIs())
			if(u.equals(URI)) //HEY! its a unit
				validKeys = this.getValidUnitModifiers();
		
		//make sure we found out what the uri was
		if(validKeys == null)
			return false;
		
		//make sure the key being modified is in the list of valid keys
		boolean found = false;
		for(ParserKeys key : validKeys)
			if(key.toString().equalsIgnoreCase(modifier.getString(ParserKeys.key)))
				found = true;
		
		//if its not, then return false
		if(!found)
			return false;
		
		return true;
	}

	@Override
	public ParserKeys[] getValidUnitModifiers() {
		return new ParserKeys[]{ParserKeys.maxHP};
	}

	@Override
	public ParserKeys[] getValidBuildingModifiers() {
		return new ParserKeys[]{ParserKeys.cost, ParserKeys.buildTime};
	}

	@Override
	public ParserKeys[] getValidAbilityModifiers() {
		return new ParserKeys[]{ParserKeys.buildTime, ParserKeys.stuffIncome, ParserKeys.range};
	}

	@Override
	public ParserKeys[] getValidProjectileModifiers() {
		return new ParserKeys[]{ParserKeys.velocity};
	}

	@Override
	public String getName() {
		return "numeric";
	}

}

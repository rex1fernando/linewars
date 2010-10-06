package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.ConfigFileParser;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;

public abstract class AbilityDefinition {
	
	public static AbilityDefinition createAbilityDefinition(String name, ConfigFileParser parser)
	{
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public abstract boolean checkValidity(MapItemDefinition mid);
	
	public abstract boolean startsActive();
	
	public abstract Ability createAbility(MapItem m);
	
	public abstract int instancesOf();
	
	public abstract boolean unlocked();
	
	public abstract String getName();
	
	public abstract String getDescription();
	
	@Override
	public abstract boolean equals(Object o);

}

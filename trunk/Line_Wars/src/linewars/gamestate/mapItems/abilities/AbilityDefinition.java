package linewars.gamestate.mapItems.abilities;


import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.parser.Parser;

public abstract class AbilityDefinition {
	
	public static AbilityDefinition createAbilityDefinition(Parser parser, MapItemDefinition m)
	{
		//TODO
		AbilityDefinition ad = null;
		
		if(!ad.checkValidity())
			throw new IllegalArgumentException(m.getName() + " cannot have ability " + ad.getName());
		return null;
	}
	
	public abstract boolean checkValidity();
	
	public abstract boolean startsActive();
	
	public abstract Ability createAbility(MapItem m);
	
	public abstract int instancesOf();
	
	public abstract boolean unlocked();
	
	public abstract String getName();
	
	public abstract String getDescription();
	
	@Override
	public abstract boolean equals(Object o);

}

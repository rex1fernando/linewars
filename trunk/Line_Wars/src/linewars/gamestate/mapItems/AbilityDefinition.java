package linewars.gamestate.mapItems;

public abstract class AbilityDefinition {
	
	public static AbilityDefinition createAbilityDefinition(String name)
	{
		return null;
	}
	
	public abstract boolean startsActive();
	
	public abstract Ability createAbility(MapItem m);

}

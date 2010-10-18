package linewars.gamestate.mapItems.abilities;


import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.parser.Parser;

/**
 * 
 * @author cschenck
 *
 * This class represents the template for an ability. It is owned
 * by a MapItemDefinition and is responsible for creating abilities
 * of its type as well as checking to for validity. It also has a static
 * method which takes in a parser and MapItemDefinition and creates an
 * ability definition.
 */
public abstract class AbilityDefinition {
	
	/**
	 * Takes in a parser and uses it to parse the type of ability and its
	 * parameters and then associates that ability definition with the given
	 * MapItemDefinition.
	 * 
	 * @param parser	the parser containing the relavent information for the ability definition
	 * @param m			the MapItemDefinition that owns this ability definition.
	 * @return			the created ability definition
	 */
	public static AbilityDefinition createAbilityDefinition(Parser parser, MapItemDefinition m)
	{
		//TODO create the ability definition
		AbilityDefinition ad = null;
		
		if(!ad.checkValidity())
			throw new IllegalArgumentException(m.getName() + " cannot have ability " + ad.getName());
		return null;
	}
	
	protected MapItemDefinition owner = null;
	
	/**
	 * Checks to make sure that the associated MapItemDefinition can own
	 * this ability
	 * 
	 * @return	true if this ability definition is valid, false otherwise.
	 */
	public abstract boolean checkValidity();
	
	/**
	 * 
	 * @return whether or not this ability starts when the unit is created
	 */
	public abstract boolean startsActive();
	
	/**
	 * Creates an ability based off this ability definition. The given mapItem
	 * is the owner of the ability.
	 * 
	 * @param m		the mapItem that owns the created ability
	 * @return		the ability
	 */
	public abstract Ability createAbility(MapItem m);
	
	/**
	 * 
	 * @return	whether or not this ability is unlocked right now
	 */
	public abstract boolean unlocked();
	
	/**
	 * 
	 * @return	the name of this ability
	 */
	public abstract String getName();
	
	/**
	 * 
	 * @return	a tool tip description of this ability
	 */
	public abstract String getDescription();
	
	@Override
	public abstract boolean equals(Object o);

}

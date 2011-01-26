package linewars.gamestate.mapItems.abilities;


import linewars.gamestate.mapItems.MapItem;
import configuration.Configuration;

/**
 * 
 * @author , Connor Schenck
 *
 * This class represents the template for an ability. It is owned
 * by a MapItemDefinition and is responsible for creating abilities
 * of its type as well as checking to for validity. It also has a static
 * method which takes in a parser and MapItemDefinition and creates an
 * ability definition.
 */
public strictfp abstract class AbilityDefinition extends Configuration {
	
	public AbilityDefinition()
	{}
	
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

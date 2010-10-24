package linewars.gamestate.mapItems.abilities;


import java.io.FileNotFoundException;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.parser.Parser;
import linewars.parser.Parser.InvalidConfigFileException;
import linewars.parser.Parser.NoSuchKeyException;
import linewars.parser.ParserKeys;

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
	
	static private int IDS = 0;
	
	/**
	 * Takes in a parser and uses it to parse the type of ability and its
	 * parameters and then associates that ability definition with the given
	 * MapItemDefinition.
	 * 
	 * @param parser	the parser containing the relavent information for the ability definition
	 * @param m			the MapItemDefinition that owns this ability definition.
	 * @return			the created ability definition
	 * @throws InvalidConfigFileException 
	 * @throws NoSuchKeyException 
	 * @throws FileNotFoundException 
	 */
	public static AbilityDefinition createAbilityDefinition(Parser parser, MapItemDefinition m) throws FileNotFoundException, NoSuchKeyException, InvalidConfigFileException
	{
		AbilityDefinition ad = null;
		if(parser.getStringValue(ParserKeys.type).equalsIgnoreCase("ConstructUnit"))
		{
			ad = new ConstructUnitDefinition(m.getOwner().getUnitDefinition(
					parser.getStringValue(ParserKeys.unitURI)), m,
					(long) parser.getNumericValue(ParserKeys.buildTime));
		}
		else if(parser.getStringValue(ParserKeys.type).equalsIgnoreCase("ResearchTech"))
		{
			ad = new ResearchTechDefinition(m.getOwner().getTech(parser.getStringValue(ParserKeys.techURI)), m);
		}
		else if(parser.getStringValue(ParserKeys.type).equalsIgnoreCase("Shoot"))
		{
			ad = new ShootDefinition(m.getOwner().getProjectileDefinition(
					parser.getStringValue(ParserKeys.projectileURI)), m,
					parser.getNumericValue(ParserKeys.range));
		}
		else
			throw new IllegalArgumentException(
					parser.getStringValue(ParserKeys.type)
							+ " does not define a valid ability in "
							+ parser.getConfigFile().getURI());
		
		if(!ad.checkValidity())
			throw new IllegalArgumentException(m.getName() + " cannot have ability " + ad.getName());
		return ad;
	}
	
	protected MapItemDefinition owner = null;
	private int ID;
	
	public AbilityDefinition()
	{
		ID = IDS++;
	}
	
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
	
	/**
	 * 
	 * @return	 the URI of the icon image associated with this ability
	 */
	public abstract String getIconURI();
	
	/**
	 * 
	 * @return	the unique ID of this ability definition
	 */
	public int getID()
	{
		return ID;
	}
	
	@Override
	public abstract boolean equals(Object o);

}

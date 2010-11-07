package linewars.gamestate.mapItems.abilities;


import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Player;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.gamestate.tech.Upgradable;

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
public strictfp abstract class AbilityDefinition implements Upgradable{
	
	/**
	 * Takes in a parser and uses it to parse the type of ability and its
	 * parameters and then associates that ability definition with the given
	 * MapItemDefinition.
	 * 
	 * @param parser	the parser containing the relevant information for the ability definition
	 * @param m			the MapItemDefinition that owns this ability definition.
	 * @return			the created ability definition
	 * @throws InvalidConfigFileException 
	 * @throws NoSuchKeyException 
	 * @throws FileNotFoundException 
	 */
	public static AbilityDefinition createAbilityDefinition(ConfigData parser, Player m, int ID) throws FileNotFoundException, NoSuchKeyException, InvalidConfigFileException
	{
		AbilityDefinition ad = null;
		if(parser.getString(ParserKeys.type).equalsIgnoreCase("ConstructUnit"))
		{
			ad = new ConstructUnitDefinition(parser, m, ID);
		}
		else if(parser.getString(ParserKeys.type).equalsIgnoreCase("ResearchTech"))
		{
			ad = new ResearchTechDefinition(parser, m, ID);
		}
		else if(parser.getString(ParserKeys.type).equalsIgnoreCase("Shoot"))
		{
			ad = new ShootDefinition(parser, m, ID);
		}
		else if(parser.getString(ParserKeys.type).equalsIgnoreCase("GenerateStuff"))
			ad = new GenerateStuffDefinition(ID, parser, m.getGameState());
		else
			throw new IllegalArgumentException(
					parser.getString(ParserKeys.type)
							+ " does not define a valid ability in "
							+ parser.getURI());
		
		if(!ad.checkValidity())
			throw new IllegalArgumentException(ad.getName() + " is invalid");
		return ad;
	}
	
	protected Player owner;
	private int ID;
	
	public AbilityDefinition(int id)
	{
		ID = id;
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
	 * @return	 the URI of the pressed icon image associated with this ability
	 */
	public abstract String getPressedIconURI();
	
	/**
	 * 
	 * @return	 the URI of the roll-over icon image associated with this ability
	 */
	public abstract String getRolloverIconURI();
	
	/**
	 * 
	 * @return	 the URI of the selected icon image associated with this ability
	 */
	public abstract String getSelectedIconURI();
	
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

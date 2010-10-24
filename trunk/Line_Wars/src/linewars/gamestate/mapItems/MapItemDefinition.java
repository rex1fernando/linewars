package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;
import java.util.ArrayList;


import linewars.gamestate.Player;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.strategies.collision.AllEnemies;
import linewars.gamestate.mapItems.strategies.collision.CollidesWithAll;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.collision.Ground;
import linewars.gamestate.mapItems.strategies.collision.NoCollision;
import linewars.gamestate.shapes.Shape;
import linewars.gamestate.shapes.ShapeAggregate;
import linewars.parser.ConfigFile;
import linewars.parser.Parser;
import linewars.parser.ParserKeys;
import linewars.parser.Parser.InvalidConfigFileException;

/**
 * 
 * @author cschenck
 *
 * This class represents a definition for a map item. It is used
 * to create the map items it defines (similar to the way a class
 * is used to define how to create objects of its type). For the
 * map items it creates, it knows what states they are allowed to
 * be in, what their name is, what parser they use, what abilities
 * they are allowed to use, who owns them, and what collision strategy
 * they use.
 */
public abstract class MapItemDefinition {
	
	private ArrayList<MapItemState> validStates;
	private String name;
	private Parser parser;
	protected ArrayList<AbilityDefinition> abilities;
	private Player owner;
	protected CollisionStrategy cStrat;
	protected Shape body;
	
	public MapItemDefinition(String URI, Player owner) throws FileNotFoundException, InvalidConfigFileException
	{
		parser = new Parser(new ConfigFile(URI));
		
		this.owner = owner;
		validStates = new ArrayList<MapItemState>();
		String[] vs = parser.getList(ParserKeys.ValidStates);
		for(String s : vs)
			validStates.add(MapItemState.valueOf(s));
		
		name = parser.getStringValue(ParserKeys.name);
		
		abilities = new ArrayList<AbilityDefinition>();
		try
		{
			vs = parser.getList(ParserKeys.abilities);
			for(String s : vs)
			{
				AbilityDefinition ad = AbilityDefinition.createAbilityDefinition(parser.getParser(s), this);
				abilities.add(ad);
			}
		}
		catch (Parser.NoSuchKeyException e)
		{}
		
		try
		{
			Parser strat = parser.getParser(ParserKeys.collisionStrategy);
			String type = strat.getStringValue(ParserKeys.type);
			if(type.equalsIgnoreCase("AllEnemies"))
				cStrat = new AllEnemies();
			else if(type.equalsIgnoreCase("CollidesWithAll"))
				cStrat = new CollidesWithAll();
			else if(type.equalsIgnoreCase("Ground"))
				cStrat = new Ground();
			else if(type.equalsIgnoreCase("NoCollision"))
				cStrat = new NoCollision();
		}
		catch(Parser.NoSuchKeyException e)
		{
			cStrat = new NoCollision();
		}
		
		//check to make sure this is a valid strat for this definition
		if(!cStrat.isValidMapItem(this))
			throw new IllegalArgumentException(cStrat.name() + " is not compatible with map item " + getName());
		
		body = new ShapeAggregate(parser.getParser(ParserKeys.body));
	}
	
	protected MapItemDefinition()
	{
		validStates = new ArrayList<MapItemState>();
		validStates.add(MapItemState.Idle);
		this.owner = null;
		name = "";
		parser = null;
		abilities = new ArrayList<AbilityDefinition>();
		cStrat = null;
		body = null;
	}

	/**
	 * Checks to see if the given state is valid for this type of map item
	 * 
	 * @param m	the state
	 * @return	true if its valid, false otherwise
	 */
	public boolean isValidState(MapItemState m)
	{
		return validStates.contains(m);
	}
	
	/**
	 * 
	 * @return	the name of the map items
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * 
	 * @return	the parser that defines the map items
	 */
	public Parser getParser()
	{
		return parser;
	}
	
	/**
	 * 
	 * @return	the list of availabel ability definitions
	 */
	public AbilityDefinition[] getAbilityDefinitions()
	{
		return abilities.toArray(new AbilityDefinition[0]);
	}
	
	/**
	 * 
	 * @return	the player that owns this mapItemDefinition
	 */
	public Player getOwner()
	{
		return owner;
	}
	
	/**
	 * 
	 * @return	the collision strategy associated with this type of map item
	 */
	public CollisionStrategy getCollisionStrategy()
	{
		return cStrat;
	}
	
	/**
	 * 
	 * @return	the shape aggregate associated with this map item
	 */
	public Shape getBody()
	{
		return body;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof MapItemDefinition)
		{
			return parser.equals(((MapItemDefinition)o).getParser());			
		}
		else
			return false;
	}
	
}

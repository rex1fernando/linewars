package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileReader;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.strategies.collision.AllEnemies;
import linewars.gamestate.mapItems.strategies.collision.AllEnemyUnits;
import linewars.gamestate.mapItems.strategies.collision.CollidesWithAll;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.collision.Ground;
import linewars.gamestate.mapItems.strategies.collision.NoCollision;
import linewars.gamestate.shapes.Shape;
import linewars.gamestate.shapes.ShapeAggregate;

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
	private ConfigData parser;
	protected ArrayList<AbilityDefinition> abilities;
	private Player owner;
	protected CollisionStrategy cStrat;
	protected Shape body;
	private GameState gameState;
	
	public MapItemDefinition(String URI, Player owner, GameState gameState) throws FileNotFoundException, InvalidConfigFileException
	{
		parser = new ConfigFileReader(URI).read();
		
		this.gameState = gameState;
		
		this.owner = owner;
		validStates = new ArrayList<MapItemState>();
		List<String> vs = parser.getStringList(ParserKeys.ValidStates);
		for(String s : vs)
			validStates.add(MapItemState.valueOf(s));
		
		name = parser.getString(ParserKeys.name);
		
		abilities = new ArrayList<AbilityDefinition>();
		try
		{
			List<ConfigData> abs = parser.getConfigList(ParserKeys.abilities);
			for(ConfigData s : abs)
			{
				AbilityDefinition ad = AbilityDefinition.createAbilityDefinition(s, this, abilities.size());
				abilities.add(ad);
			}
		}
		catch (ConfigData.NoSuchKeyException e)
		{}
		
		try
		{
			ConfigData strat = parser.getConfig(ParserKeys.collisionStrategy);
			String type = strat.getString(ParserKeys.type);
			if(type.equalsIgnoreCase("AllEnemies"))
				cStrat = new AllEnemies();
			else if(type.equalsIgnoreCase("CollidesWithAll"))
				cStrat = new CollidesWithAll();
			else if(type.equalsIgnoreCase("Ground"))
				cStrat = new Ground();
			else if(type.equalsIgnoreCase("NoCollision"))
				cStrat = new NoCollision();
			else if(type.equalsIgnoreCase("AllEnemyUnits"))
				cStrat = new AllEnemyUnits();
		}
		catch(ConfigData.NoSuchKeyException e)
		{
			cStrat = new NoCollision();
		}
		
		//check to make sure this is a valid strat for this definition
		if(!cStrat.isValidMapItem(this))
			throw new IllegalArgumentException(cStrat.name() + " is not compatible with map item " + getName());
		
		body = Shape.buildFromParser(parser.getConfig(ParserKeys.body));//new ShapeAggregate(parser.getParser(ParserKeys.body));
	}
	
	protected MapItemDefinition(GameState gameState)
	{
		validStates = new ArrayList<MapItemState>();
		validStates.add(MapItemState.Idle);
		this.owner = null;
		name = "";
		parser = null;
		abilities = new ArrayList<AbilityDefinition>();
		cStrat = null;
		body = null;
		this.gameState = gameState;
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
	public ConfigData getParser()
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
	
	public GameState getGameState()
	{
		return gameState;
	}
	
}

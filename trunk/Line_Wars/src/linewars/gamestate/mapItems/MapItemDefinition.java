package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import linewars.gamestate.ConfigFileParser;
import linewars.gamestate.ConfigFileParser.InvalidConfigFileException;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.strategies.CollisionStrategy;

public abstract class MapItemDefinition {
	
	private ArrayList<MapItemState> validStates;
	private String name;
	private ConfigFileParser parser;
	protected ArrayList<AbilityDefinition> abilities;
	private Player owner;
	private CollisionStrategy cStrat;
	
	public MapItemDefinition(String URI, Player owner) throws FileNotFoundException, InvalidConfigFileException
	{
		parser = new ConfigFileParser(URI);
		
		this.owner = owner;
		validStates = new ArrayList<MapItemState>();
		String[] vs = parser.getList("validStates");
		for(String s : vs)
			validStates.add(MapItemState.valueOf(s));
		
		name = parser.getStringValue("name");
		
		abilities = new ArrayList<AbilityDefinition>();
		try
		{
			vs = parser.getList("abilities");
			for(String s : vs)
			{
				AbilityDefinition ad = AbilityDefinition.createAbilityDefinition(s, parser);
				if(!ad.checkValidity(this))
					throw new IllegalArgumentException(name + " cannot have ability " + ad.getName());
				abilities.add(ad);
			}
		}
		catch (ConfigFileParser.NoSuchKeyException e)
		{}
		
		try
		{
			//TODO convert string to collision strategy
			String strat = parser.getStringValue("collisionStrategy");
		}
		catch(ConfigFileParser.NoSuchKeyException e)
		{
			//TODO set collision strat to some default value
		}
		
		//check to make sure this is a valid strat for this definition
		if(!cStrat.isValidMapItem(this))
			throw new IllegalArgumentException(cStrat.name() + " is not compatible with map item " + getName());
	}

	public boolean isValidState(MapItemState m)
	{
		return validStates.contains(m);
	}
	
	public String getName()
	{
		return name;
	}
	
	public ConfigFileParser getParser()
	{
		return parser;
	}
	
	public AbilityDefinition[] getAbilityDefinitions()
	{
		return abilities.toArray(new AbilityDefinition[0]);
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public CollisionStrategy getCollisionStrategy()
	{
		return cStrat;
	}
	
}

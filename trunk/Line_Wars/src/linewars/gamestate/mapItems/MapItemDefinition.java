package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;
import java.util.ArrayList;


import linewars.gamestate.Player;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.strategies.CollisionStrategy;
import linewars.parser.Parser;
import linewars.parser.ParserKeys;

public abstract class MapItemDefinition {
	
	private ArrayList<MapItemState> validStates;
	private String name;
	private Parser parser;
	protected ArrayList<AbilityDefinition> abilities;
	private Player owner;
	private CollisionStrategy cStrat;
	
	public MapItemDefinition(String URI, Player owner) throws FileNotFoundException
	{
		//TODO ask the player for the parser
//		parser = new ConfigFileParser(URI);
		
		this.owner = owner;
		validStates = new ArrayList<MapItemState>();
		String[] vs = parser.getList(ParserKeys.ValidStates);
		for(String s : vs)
			validStates.add(MapItemState.valueOf(s));
		
		name = parser.getStringValue(ParserKeys.name);
		
		abilities = new ArrayList<AbilityDefinition>();
		try
		{
			//TODO
//			vs = parser.getList("abilities");
			for(String s : vs)
			{
				AbilityDefinition ad = AbilityDefinition.createAbilityDefinition(s, parser);
				if(!ad.checkValidity(this))
					throw new IllegalArgumentException(name + " cannot have ability " + ad.getName());
				abilities.add(ad);
			}
		}
		catch (Parser.NoSuchKeyException e)
		{}
		
		try
		{
			//TODO convert string to collision strategy
//			String strat = parser.getStringValue("collisionStrategy");
		}
		catch(Parser.NoSuchKeyException e)
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
	
	public Parser getParser()
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

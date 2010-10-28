package linewars.gamestate;

import java.io.FileNotFoundException;

import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.parser.ConfigFile;
import linewars.parser.Parser;
import linewars.parser.Parser.InvalidConfigFileException;
import linewars.parser.Parser.NoSuchKeyException;
import linewars.parser.ParserKeys;

public class Tech {
	
	private MapItemDefinition definition;
	private ParserKeys field;
	private Function f;
	private int currentResearch = 0;
	private Parser parser;
	private Tech[] prereqs;
	private int maxTimesResearchable;
	private String name;
	
	public Tech(String URI, Player owner) throws FileNotFoundException, InvalidConfigFileException
	{
		parser = new Parser(new ConfigFile(URI));
		definition = owner.getMapItemDefinition(parser.getStringValue(ParserKeys.mapItemURI));
		field = ParserKeys.valueOf(parser.getStringValue(ParserKeys.field));
		f = new Function(parser.getParser(ParserKeys.valueFunction));
		try
		{
		String[] list = parser.getList(ParserKeys.preReqs);
		prereqs = new Tech[list.length];
		for(int i = 0; i < list.length; i++)
			prereqs[i] = owner.getTech(list[i]);
		}
		catch (NoSuchKeyException e)
		{
			prereqs = new Tech[0];
		}
		
		try
		{
			maxTimesResearchable = (int) parser.getNumericValue(ParserKeys.maxTimesResearchable);
		}
		catch(NoSuchKeyException e)
		{
			maxTimesResearchable = Integer.MAX_VALUE;
		}
		name = parser.getStringValue(ParserKeys.name);
	}
	
	/**
	 * This method checks to see if the pre-req's for this
	 * tech have been researched.
	 * 
	 * @return	true if the pre-req's have been researched, false otherwise
	 */
	public boolean researchable()
	{
		for(Tech t : prereqs)
			if(t.currentResearch <= 0)
				return false;
		
		return true;
	}
	
	/**
	 * This method returns the maximum number of times this tech
	 * is allowed to be researched.
	 * 
	 * @return	the number of times to allow research
	 */
	public int maxTimesResearchable()
	{
		return maxTimesResearchable;
	}
	
	/**
	 * researches the tech
	 */
	public void research()
	{
		if(field == ParserKeys.cost)
			((BuildingDefinition)definition).setCost(f.f(++currentResearch));
		else if(field == ParserKeys.buildTime)
			((BuildingDefinition)definition).setBuildTime(f.f(++currentResearch));
		else if(field == ParserKeys.maxHP)
			((UnitDefinition)definition).setMaxHP(f.f(++currentResearch));
		else if(field == ParserKeys.velocity)
			((ProjectileDefinition)definition).setVelocity(f.f(++currentResearch));
	}
	
	/**
	 * gets the name of this tech
	 * 
	 * @return	the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * returns the tooltip description of the tech
	 * 
	 * @return	the description
	 */
	public String getDescription()
	{
		return "Modifies " + field.toString() + " in " + definition.getName() + " to " + f.f((double)(currentResearch + 1));
	}
	
	/**
	 * gets the parser associated with this tech
	 * 
	 * @return	the parser
	 */
	public Parser getParser()
	{
		return parser;
	}
	
	public MapItemDefinition getMapItemDefinition()
	{
		return definition;
	}

}

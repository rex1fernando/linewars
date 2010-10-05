package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import linewars.gamestate.ConfigFileParser;
import linewars.gamestate.ConfigFileParser.InvalidConfigFileException;
import linewars.gamestate.Position;

public abstract class MapItemDefinition<T extends MapItem> {
	
	private ArrayList<MapItemState> validStates;
	private String name;
	private ConfigFileParser parser;
	
	public MapItemDefinition(String URI) throws FileNotFoundException, InvalidConfigFileException
	{
		parser = new ConfigFileParser(URI);
		
		validStates = new ArrayList<MapItemState>();
		String[] vs = parser.getList("validStates");
		for(String s : vs)
			validStates.add(MapItemState.valueOf(s));
		
		name = parser.getStringValue("name");
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
	
	public abstract T createMapItem(Position p, double rotation);
	
}

package linewars.gamestate;

import java.awt.geom.Dimension2D;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import linewars.gamestate.mapItems.*;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.parser.Parser;
import linewars.parser.Parser.InvalidConfigFileException;

public class GameState
{
	// TODO finish implementation!
	
	private Map map;
	private HashMap<Integer, Player> players;
	private int numPlayers;
	
	public int getNumPlayers()
	{
		return numPlayers;
	}
	
	public Player getPlayer(int playerID)
	{
		return players.get(playerID);
	}
	
	public GameState(Parser mapParser)
	{
		map = new Map(mapParser, null, null);
	}
	
	public Dimension2D getMapSize()
	{
		return map.getDimensions();
	}
	
	public String getMap()
	{
		return map.getMapURI();
	}
	
	public List<Player> getPlayers()
	{
		return new ArrayList<Player>();
	}
	
	public long getTime()
	{
		return System.currentTimeMillis();
	}
	
	public List<MapItem> getUnits()
	{
		//test code
		Transformation t = new Transformation(new Position(300, 300), 0);
		UnitDefinition def = null;
		try
		{
			def = new UnitDefinition("resources/units/dummy_unit.cfg", null);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidConfigFileException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Unit unit = new Unit(t, def, null, null);
		List<MapItem> units = new ArrayList<MapItem>();
		units.add(unit);
		//end test code
		
		return units;
	}
	
	public List<MapItem> getBuildings()
	{
		return new ArrayList<MapItem>();
	}
	
	public List<MapItem> getProjectiles()
	{
		return new ArrayList<MapItem>();
	}
	
	public List<CommandCenter> getCommandCenters()
	{
		ArrayList<CommandCenter> ccs = new ArrayList<CommandCenter>();
		Node[] nodes = map.getNodes();
		for(Node n : nodes)
			ccs.add(n.getCommandCenter());
		return ccs;
	}
}

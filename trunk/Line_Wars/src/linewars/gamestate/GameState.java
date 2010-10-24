package linewars.gamestate;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Unit;
import linewars.parser.Parser;

public class GameState
{
	// TODO finish implementation!
	
	private Map map;
	private int numPlayers;
	
	public int getNumPlayers()
	{
		return numPlayers;
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
		//Unit unit = new Unit
		List<MapItem> units = new ArrayList<MapItem>();
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
}

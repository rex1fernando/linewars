package linewars.gamestate;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.mapItems.MapItem;

public class GameState
{
	// TODO finish implementation!
	
	private Map map;
	
	public Dimension2D getMapSize()
	{
		//return map.getDimensions();
		return new Dimension(800, 600);
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
		return new ArrayList<MapItem>();
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

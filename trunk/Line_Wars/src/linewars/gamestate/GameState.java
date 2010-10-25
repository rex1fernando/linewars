package linewars.gamestate;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import linewars.gameLogic.GameTimeManager;
import linewars.gamestate.mapItems.*;
import linewars.parser.Parser;

public class GameState
{
	// TODO finish implementation!
	
	private Map map;
	private HashMap<Integer, Player> players;
	private int numPlayers;
	private ArrayList<Race> races;
	
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
		List<Player> players = new ArrayList<Player>();
		for(int i = 0; i < numPlayers; i++)
			players.add(this.players.get(i));
		
		return players;
	}
	
	public long getTime()
	{
		return GameTimeManager.currentTimeMillis();
	}
	
	public List<Unit> getUnits()
	{
		List<Unit> units = new ArrayList<Unit>();
		Lane[] lanes = map.getLanes();
		for(Lane l : lanes)
		{
			Wave[] waves = l.getWaves();
			for(Wave w : waves)
			{
				Unit[] us = w.getUnits();
				for(Unit u : us)
					units.add(u);
			}
		}
		
		return units;
	}
	
	public List<Building> getBuildings()
	{
		List<Building> buildings = new ArrayList<Building>();
		Node[] nodes = map.getNodes();
		for(Node n : nodes)
		{
			Building[] bs = n.getContainedBuildings();
			for(Building b : bs)
				buildings.add(b);
		}
		
		return buildings;
	}
	
	public List<Projectile> getProjectiles()
	{
		List<Projectile> projectiles = new ArrayList<Projectile>();
		Lane[] lanes = map.getLanes();
		for(Lane l : lanes)
		{
			Projectile[] ps = l.getProjectiles();
			for(Projectile p : ps)
				projectiles.add(p);
		}
		
		return projectiles;
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

package linewars.gamestate;

import java.awt.geom.Dimension2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileReader;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.display.layers.MapItemLayer.MapItemType;
import linewars.gameLogic.TimingManager;
import linewars.gamestate.mapItems.*;
import linewars.network.messages.AdjustFlowDistributionMessage;
import linewars.network.messages.BuildMessage;
import linewars.network.messages.Message;
import linewars.network.messages.UpgradeMessage;


/**
 * 
 * @author Connor Schenck
 *
 * This class represents the entirety of the game state at a given point
 * in time. It also has the ability to update to the game state for the
 * next tick.
 *
 */
public strictfp class GameState
{
	
	private static final double STARTING_STUFF = 10000;
	
	private int timerTick;
	private Map map;
	private HashMap<Integer, Player> players;
	private int numPlayers;
	private ArrayList<Race> races;
	
	private Player winningPlayer = null;
	
	public int getNumPlayers()
	{
		return numPlayers;
	}
	
	public Player getPlayer(int playerID)
	{
		return players.get(playerID);
	}
	
	/**
	 * This constructor constructs the game state. It takes in the parser for the map,
	 * the number of players, and the list of race URIs, in order for each player
	 * (eg the 1st spot in the list is the race for the 1st player and so on).
	 * 
	 * @param mapParser		the parser for the map	
	 * @param numPlayers	the number of players
	 * @param raceURIs		the URI's of the races
	 * @throws FileNotFoundException
	 * @throws InvalidConfigFileException
	 */
	public GameState(String mapURI, int numPlayers, List<String> raceURIs, List<String> playerNames) throws FileNotFoundException, InvalidConfigFileException
	{
		ConfigData mapParser = new ConfigFileReader(mapURI).read();
		map = new Map(this, mapParser);
		players = new HashMap<Integer, Player>();
		this.numPlayers = numPlayers;
		timerTick = 0;
		
		races = new ArrayList<Race>();
		for(int i = 0; i < raceURIs.size(); i++)
		{
			Race r = new Race(new ConfigFileReader(raceURIs.get(i)).read());
			if(!races.contains(r))
				races.add(r);
			Node[] startNode = { map.getStartNode(i) };
			//TODO I'm changing this to add the players to the HashMap. Let me know if that's incorrect. -John G.
			Player p = new Player(this, startNode, r, playerNames.get(i), i);
			players.put(i, p);
		}
	}
	
	/**
	 * 
	 * @return	the dimensions of the map
	 */
	public Dimension2D getMapSize()
	{
		return map.getDimensions();
	}
	
	/**
	 * 
	 * @return	the game map
	 */
	public Map getMap()
	{
		return map;
	}
	
	/**
	 * 
	 * @return	the list of players
	 */
	public List<Player> getPlayers()
	{
		List<Player> players = new ArrayList<Player>();
		for(int i = 0; i < numPlayers; i++)
			players.add(this.players.get(i));
		
		return players;
	}
	
	/**
	 * 
	 * @return	the current time represented as a double, in seconds
	 */
	public double getTime()
	{
		return timerTick * TimingManager.GAME_TIME_PER_TICK_S;
	}
	
	/**
	 * 
	 * @return	the current tick for the game state
	 */
	public int getTimerTick()
	{
		return timerTick;
	}
	
	/**
	 * Takes in a MapItemType enum object and returns a list of
	 * all the associated types of map items in the game state.
	 * 
	 * @param type	the type of map item
	 * @return		a list of all the map items of type type in the game state
	 */
	public List<? extends MapItem> getMapItemsOfType(MapItemType type)
	{
		switch (type)
		{
		case UNIT:
			return getUnits();
		case PROJECTILE:
			return getProjectiles();
		case BUILDING:
			return getBuildings();
		case LANEBORDER:
			return getLaneBorders();
		default:
			return new ArrayList<MapItem>(0);
		}
	}
	
	/**
	 * 
	 * @return	all the units in the game state
	 */
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
				{
					units.add(u);
				}
			}
		}
		
		return units;
	}
	
	/**
	 * 
	 * @return	all the buildings in the game state
	 */
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
	
	/**
	 * 
	 * @return	all the projectiles in the game state
	 */
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
	
	/**
	 * 
	 * @return	all the lane borders in the game state
	 */
	public List<LaneBorder> getLaneBorders()
	{
		List<LaneBorder> borders = new ArrayList<LaneBorder>();
		for(Lane l : map.getLanes())
			borders.addAll(l.getLaneBorders());
		return borders;
	}
	
	/**
	 * 
	 * @return	all the command centers in the game state
	 */
	public List<CommandCenter> getCommandCenters()
	{
		ArrayList<CommandCenter> ccs = new ArrayList<CommandCenter>();
		Node[] nodes = map.getNodes();
		for(Node n : nodes)
			ccs.add(n.getCommandCenter());
		return ccs;
	}
	
	/**
	 * 
	 * @return	the amount of stuff each player starts with
	 */
	public double getStartingStuffAmount()
	{
		return STARTING_STUFF;
	}
	
	/**
	 * Updates the game state to the next tick. First applies the messages
	 * past in as arguments, then updates the nodes, then the lanes, then checks
	 * for a winning player.
	 * 
	 * @param messages	the messages to apply for this game state tick
	 */
	public void update(Message[] messages)
	{
		for(Message m : messages)
			m.apply(this);
		
		for(Node n : map.getNodes())
			n.update();
		
		for(Lane l : map.getLanes())
			l.update();
		
		timerTick++;
		
		//check for win
		Node n1 = map.getNodes()[0];
		for(Node n : map.getNodes())
			if(n1.getOwner() == null || !n1.getOwner().equals(n.getOwner()))
				return;
		winningPlayer = n1.getOwner();
	}
	
	/**
	 * 
	 * @return	if there is a winning player, the winning player; else null
	 */
	public Player getWinningPlayer()
	{
		return winningPlayer;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null) return false;
		if(!(o instanceof GameState)) return false;
		GameState other = (GameState) o;
		if(other.timerTick != timerTick) return false;
		if(other.numPlayers != numPlayers) return false;
		if(!other.map.equals(map)) return false;
		return true;
	}
}

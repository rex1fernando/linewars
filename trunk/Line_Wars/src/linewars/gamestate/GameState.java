package linewars.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import linewars.display.layers.MapItemLayer.MapItemType;
import linewars.gameLogic.TimingManager;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemAggregate;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import linewars.init.PlayerData;
import linewars.network.messages.Message;


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
	private List<Race> races;
	
	private Player winningPlayer = null;
	
	private int IDCounter = 0;
	
	public int getNumPlayers()
	{
		return numPlayers;
	}
	
	public Player getPlayer(int playerID)
	{
		return players.get(playerID);
	}
	
//	public GameState(MapConfiguration mapConfig, int numPlayers, List<Race> races, List<String> playerNames)
//	{
//		map = mapConfig.createMap(this);
//		players = new HashMap<Integer, Player>();
//		this.numPlayers = numPlayers;
//		timerTick = 0;
//		
//		this.races = races;
//		for(int i = 0; i < races.size(); i++)
//		{
//			Race r = races.get(i);
//			Node[] startNode = { map.getStartNode(i) };
//			Player p = new Player(this, startNode, r, playerNames.get(i), i);
//			players.put(i, p);
//		}
//	}
	
	public GameState(MapConfiguration mapConfig, List<PlayerData> players) {
		map = mapConfig.createMap(this);
		this.players = new HashMap<Integer, Player>();
		this.numPlayers = players.size();
		timerTick = 0;
		
		this.races = new ArrayList<Race>();
		for(int i = 0; i < players.size(); i++)
		{
			Race r = players.get(i).getRace();
			this.races.add(r);
			Node[] startNodes = { map.getStartNode(players.get(i).getStartingSlot()) };
			Player p = new Player(this, startNodes, r, players.get(i).getName(), i);
			this.players.put(i, p);
		}
	}

	/**
	 * 
	 * @return	the dimensions of the map
	 */
	public Position getMapSize()
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
		List<? extends MapItem> list = null;
		switch (type)
		{
			case UNIT:
				list = getUnits();
				break;
			case PROJECTILE:
				list = getProjectiles();
				break;
			case BUILDING:
				list = getBuildings();
				break;
			default:
				list = new ArrayList<MapItem>(0);
		}
		
		List<MapItem> ret = new ArrayList<MapItem>();
		for(MapItem m : list)
			ret.add(m);
		
		for(int i = 0; i < ret.size(); i++)
		{
			if(ret.get(i) instanceof MapItemAggregate)
			{
				ret.addAll(((MapItemAggregate)ret.get(i)).getContainedItems());
			}
		}
		return ret;
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
	 * @return	all the command centers in the game state
	 */
	public List<Building> getCommandCenters()
	{
		ArrayList<Building> ccs = new ArrayList<Building>();
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
	
	public int getNextMapItemID()
	{
		return IDCounter++;
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

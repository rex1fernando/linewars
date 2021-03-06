package linewars.gamestate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import configuration.Configuration;

import linewars.display.layers.MapItemLayer.MapItemType;
import linewars.gameLogic.TimingManager;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemAggregate;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.collision.FlyingConfiguration.Flying;
import linewars.gamestate.mapItems.strategies.collision.GroundConfiguration.Ground;
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
	
	private static final double STARTING_STUFF = 400;
	public static final double MAX_PLAYER_ENERGY = 100;
	private static final double ENERGY_INCREMENT_RATE = 1;
	
	private int timerTick;
	private Map map;
	private HashMap<Integer, Player> players;
	private List<Race> races;
	
	private Player winningPlayer = null;
	
	private int IDCounter = 0;
	
	private double lastLoopTime = 0;
	private double timeAtEndOfLastLoop = 0;
	
	private boolean locked = false;
	
	public boolean isLocked()
	{
		return locked;
	}
	
	public void setLocked(boolean b)
	{
		locked = b;
	}
	
	public int getNumPlayers()
	{
		return this.players.size();
	}
	
	public void validateLock()
	{
		if(this.isLocked())
			throw new IllegalStateException("Cannot update a locked game state");
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
		timerTick = 0;
		
		this.races = new ArrayList<Race>();
		for(int i = 0; i < players.size(); i++)
		{
			Race r = null;
			try {
				r = (Race) Configuration.copyConfiguration(players.get(i).getRace());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(r == null)
				throw new RuntimeException("Error copying race");
			
			this.races.add(r);
			Node[] startNodes = { map.getStartNode(players.get(i).getStartingSlot() - 1) };
			Player p = new Player(this, startNodes, r, players.get(i).getName(), i);
			this.players.put(i, p);
		}
		
		//TODO this dummy player is for debugging purposes
		Race r = null;
		int i = this.players.size();
		try {
			r = (Race) Configuration.copyConfiguration(players.get(0).getRace());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(r == null)
			throw new RuntimeException("Error copying race");
		this.races.add(r);
		Node[] nodes = this.getMap().getNodes();
		List<Node> dummyStartNodes = new ArrayList<Node>();
		for(Node n : nodes)
			if(n.getOwner() == null)
				dummyStartNodes.add(n);
		Player dummyPlayer = new Player(this, dummyStartNodes.toArray(new Node[0]), r, "dummy PLayer", i);
		this.players.put(i, dummyPlayer);
		
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
		for(int i = 0; i < this.players.size(); i++)
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
	 * @return	the time in seconds since the last loop
	 */
	public double getLastLoopTime()
	{
		return lastLoopTime;
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
		
		Collections.sort(units, new Comparator<Unit>() {

			@Override
			public int compare(Unit o1, Unit o2) {
				if(o1.getCollisionStrategy() instanceof Ground && o2.getCollisionStrategy() instanceof Flying)
					return -1;
				else if(o2.getCollisionStrategy() instanceof Ground && o1.getCollisionStrategy() instanceof Flying)
					return 1;
				else
					return 0;
			}
		});
		
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
		this.validateLock();
		
		for(Message m : messages)
			m.apply(this);
		
		this.validateLock();
		
		for(Node n : map.getNodes())
			n.update();
		
		this.validateLock();
		
		for(Lane l : map.getLanes())
			l.update();
		
		this.validateLock();
		
		double energyToAdd = ENERGY_INCREMENT_RATE*this.getLastLoopTime();
		for(Player p : players.values())
		{
			p.setPlayerEnergy(p.getPlayerEnergy() + energyToAdd);
			if(p.getPlayerEnergy() > MAX_PLAYER_ENERGY)
				p.setPlayerEnergy(MAX_PLAYER_ENERGY);
		}		
		
		timerTick++;
		
		lastLoopTime = this.getTime() - timeAtEndOfLastLoop;
		timeAtEndOfLastLoop = this.getTime();
		
		this.validateLock();
		
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
		if(!other.map.equals(map)) return false;
		return true;
	}
}

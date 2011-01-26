package linewars.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.Gate;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.shapes.Circle;
import linewars.gamestate.shapes.Shape;

/**
 * 
 * @author John George, Connor Schenck
 * 
 * Represents a node in the game map
 *
 */
public strictfp class Node {
	
	private static final long TIME_TO_OCCUPY = 30000;
	private static final long TIME_TO_SPAWN = 5000;
	
	private GameState gameState;
	private Player owner;
	private Player invader;
	private long occupationStartTime;
	
	private ArrayList<Building> containedBuildings;
	private Building cCenter;
	private ArrayList<Unit> containedUnits;
	private long lastSpawnTime;
	
	private ArrayList<Lane> attachedLanes;
	
	private int ID;
	
	private NodeConfiguration config;
	
//	private HashMap<Double, Lane> laneMap;
	
	public Node(NodeConfiguration config, GameState gameState, int id)
	{
		ID = id;
		invader = null;
		owner = null;
		occupationStartTime = -1;
		this.cCenter = null;
		containedUnits = new ArrayList<Unit>();
		lastSpawnTime = (long) (gameState.getTime()*1000);
		containedBuildings = new ArrayList<Building>();
		
		this.gameState = gameState;
		
		attachedLanes = new ArrayList<Lane>();
	}
	
	public NodeConfiguration getConfig()
	{
		return config;
	}
	
	/**
	 * 
	 * @return	the current owner of this node. If there is no owner, returns null
	 */
	public Player getOwner()
	{
		return owner;
	}
	
	/**
	 * 
	 * @return	true if the node is contested; false otherwise
	 */
	public boolean isContested()
	{
		return occupationStartTime >= 0;
	}
	
	/**
	 * 
	 * @return	the current invader of the node; null if there is no invader
	 */
	public Player getInvader()
	{
		return invader;
	}
	
	/**
	 * 
	 * @return	the time at which the occupation of this node started in ms
	 */
	public long getOccupationStartTime()
	{
		return occupationStartTime;
	}
	
	/**
	 * 
	 * @return	the buildings contained in this node NOT including the commander center
	 */
	public Building[] getContainedBuildings()
	{
		ArrayList<Building> ret = new ArrayList<Building>(containedBuildings);
		if(cCenter != null)
		{
			ret.add(cCenter);
		}
		
		return ret.toArray(new Building[0]);
	}
	
	/**
	 * 
	 * @param l	a lane to attach to this node
	 */
	public void addAttachedLane(Lane l)
	{
		attachedLanes.add(l);
	}
	
	/**
	 * 
	 * @return	all the lanes attached to this node
	 */
	public Lane[] getAttachedLanes()
	{
		return attachedLanes.toArray(new Lane[0]);
	}
	
	/**
	 * 
	 * @return	all the units current garrasoned in the node
	 */
	public Unit[] getContainedUnits()
	{
		return containedUnits.toArray(new Unit[0]);
	}
	
	/**
	 * 
	 * @return	a circle that bounds the entire node
	 */
	public Circle getBoundingCircle()
	{
		return config.getShape().boundingCircle();
	}
	
	/**
	 * 
	 * @return	the command center in the node; if there isn't one, returns null
	 */
	public Building getCommandCenter()
	{
		return cCenter;
	}
	
	/**
	 * This method is the "entry point" for distributing the units in this node into waves in the correct lanes.
	 */
	private void generateWaves()
	{
		if(gameState.getTime()*1000 - lastSpawnTime > TIME_TO_SPAWN)
		{
			
			Random rand = new Random(gameState.getTimerTick());
			HashMap<Player, Entry<Double[], Lane[]>> flows = getAllFlow(this);
			for(int i = 0; i < containedUnits.size();)
			{
				Lane destination = null;
				Player owner = containedUnits.get(i).getOwner();
				Double[] currentFlowSet = flows.get(owner).getKey();
				double totalFlow = 0;
				if(currentFlowSet.length > 0)
					totalFlow = currentFlowSet[currentFlowSet.length - 1];
				double number = rand.nextDouble() * totalFlow;
				
				for(int j = 0; j < currentFlowSet.length; j++)
				{
					if(number <= currentFlowSet[j])
					{
						destination = flows.get(owner).getValue()[j];
						break;
					}
				}
				
				if(destination != null) //if there is no output from this node, just delete the units
					destination.addToPending(this, containedUnits.get(i));
				containedUnits.remove(i);
			}
			
			lastSpawnTime = (long) (gameState.getTime()*1000);
		}
	}
	
	private double getTotal(Double[] flows)
	{
		double ret = 0;
		for(int i = 0; i < flows.length; i++)
		{
			ret = ret + flows[i];
		}
		return ret;
	}
	
	/**
	 * This method is a helper method for spawnWaves that generates the array of flow values that spawnWaves
	 * uses to randomly decide what lanes to send each player's units to. Only considers lanes where n is
	 * the start node.
	 * @param n
	 * the node to get the flows from
	 */
	private HashMap<Player, Entry<Double[], Lane[]>> getAllFlow(Node n)
	{
		
		List<Player> players = gameState.getPlayers();
		HashMap<Player, Entry<Double[], Lane[]>> ret = new HashMap<Player, Entry<Double[], Lane[]>>();
		List<Double> flows;
		List<Lane> lanes;
		
		//Iterate through every player.
		for(int i = 0; i < players.size(); i++)
		{
			
			Lane[] l = gameState.getMap().getLanes();
			flows = new ArrayList<Double>();
			lanes = new ArrayList<Lane>();
			Player p = players.get(i);
			double total = 0;
			
			for(Lane lane : l)
			{
				if(p.isStartPoint(lane, n))
				{
					flows.add(p.getFlowDist(lane) + total);
					lanes.add(lane);
					total += p.getFlowDist(lane);
				}
			}
			
			ret.put(players.get(i), new Pair<Double[], Lane[]>(flows.toArray(new Double[0]), lanes.toArray(new Lane[0])));
		}
		return ret;
		
	}
	
	/**
	 * This method gets the next available position and rotation to build a
	 * building at. Transformation includes a position and
	 * rotation. Returns null if there isn't a spot left to
	 * build any buildings at.
	 * 
	 * @return the next available spot or null
	 */
	public Transformation getNextAvailableBuildingSpot()
	{
		if(containedBuildings.size() >= config.buildingSpots().size())
		{
			return null;
		}
		return config.buildingSpots().get(containedBuildings.size()).getTrans();
	}
	
	/**
	 * This method attempts to put the input building into the spot
	 * that the building says its in. If that spot is available to
	 * place a building, then it is added and this method returns
	 * true. Otherwise this method returns false.
	 * 
	 * @param b		the building to add to the node
	 * @return		if the building was successfully added
	 */
	public boolean addBuilding(Building b)
	{
		for(int i = 0; i < containedBuildings.size(); i++)
		{
			if(b.getTransformation().equals(containedBuildings.get(i).getTransformation()))
			{
				return false;
			}
		}
		b.setNode(this);
		return containedBuildings.add(b);
	}
	
	
	/**
	 * Adds a unit to the list of contained units in the node
	 * 
	 * @param u		the contained units in the node
	 */
	public void addUnit(Unit u)
	{
		containedUnits.add(u);
	}
	
	public List<BuildingSpot> getBuildingSpots()
	{
		return config.buildingSpots();
	}
	
	public BuildingSpot getCommandCenterSpot()
	{
		return config.getCommandCenterSpot();
	}

	/**
	 * Gets the position of the shape that makes up this Node, defined as the center of the Node.
	 * @return a Transformation representing the center of this Node.
	 */
	public Transformation getTransformation()
	{
		return config.getShape().position();
	}
	
	/**
	 * This method updates everything in the node. It calls update on all the buildings (including the
	 * command center). It also redistributes the units in the node (puts them in waves when needed) It also
	 * needs to make sure to place gates if it gets taken over
	 */
	public void update()
	{
		if(!this.isContested())
		{
			for(Building b : containedBuildings)
				b.updateMapItem();
			if(cCenter != null)
				cCenter.updateMapItem();
		}
		generateWaves();
		
		//Check whether the node should change owners. 
		if(occupationStartTime > 0 && gameState.getTime()*1000 - occupationStartTime > TIME_TO_OCCUPY)
			setOwner(invader);
	}
	
	/**
	 * Sets the owner of this Node to p and sets this node up for the new player.
	 * @param p
	 */
	public void setOwner(Player p)
	{
		owner = p;
		cCenter = p.getCommandCenterDefinition().createMapItem(this.getCommandCenterSpot().getTrans(), p, gameState);
		cCenter.setNode(this);
		containedBuildings.clear();
		for(Lane l : attachedLanes)
		{
			l.addGate(this, owner);
		}
		invader = null;
		occupationStartTime = -1;
		
		//now check to see if this node is an start point for any flows
		boolean isStart = false;
		for(Lane l : attachedLanes)
			if(p.isStartPoint(l, this))
				isStart = true;
	}
	
	/**
	 * 
	 * @return	the shape that represents this node
	 */
	public Shape getShape()
	{
		return config.getShape();
	}
	
	/**
	 * 
	 * @return	true if this node is a start node; false otherwise
	 */
	public boolean isStartNode()
	{
		return config.isStartNode();
	}
	
	/**
	 * Sets the invader to p and restarts the occupation timer if the invader
	 * was not already p. If p is the owner, returns the node pack to non-
	 * contested state.
	 * 
	 * @param p	the player invading the node
	 */
	public void setInvader(Player p)
	{
		if(p.equals(owner))
		{
			invader = null;
			occupationStartTime = -1;
		}
		else
		{
			if(invader != null && p.equals(invader))
				return;
			invader = p;
			occupationStartTime = (long) (gameState.getTime()*1000);
			for(Lane l : attachedLanes){
				Gate toKill = l.getGate(this);
				if(toKill != null){
					toKill.setState(MapItemState.Dead);
				}
			}
			
			for(Building b : containedBuildings)
				b.setState(MapItemState.Idle);
		}
	}
	
	/**
	 * 
	 * @return	the unique ID associated with this node
	 */
	public int getID()
	{
		return ID;
	}
	
	@Override
	public String toString()
	{
		return getTransformation().getPosition().toString();
	}
	
	private class Pair<K, V> implements Entry<K, V> {

		private K key;
		private V value;
		
		public Pair(K k, V v)
		{
			key = k;
			value = v;
		}
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			this.value = value;
			return value;
		}
		
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Node)
			return this.getID() == ((Node)o).getID();
		else
			return false;
	}
	
	@Override
	public int hashCode()
	{
		return this.getID();
	}
	
	/**
	 * 
	 * @return	the last time unit's were spawned in ms
	 */
	public long getLastSpawnTime() {
		return lastSpawnTime;
	}
	
	/**
	 * 
	 * @return	the time interval between spawns
	 */
	public long getSpawnTime() {
		return TIME_TO_SPAWN;
	}
	
	/**
	 * 
	 * @return	the time interval to capture a node
	 */
	public long getCaptureTime() {
		return TIME_TO_OCCUPY;
	}
}

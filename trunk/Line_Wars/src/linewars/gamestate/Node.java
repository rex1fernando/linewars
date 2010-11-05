package linewars.gamestate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;



import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.CommandCenter;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.shapes.*;

public class Node {
	
	private static final long TIME_TO_OCCUPY = 30000;
	private static final long TIME_TO_SPAWN = 5000;
	
	private GameState gameState;
	private Player owner;
	private Player invader;
	private long occupationStartTime;
	
	private ArrayList<Building> containedBuildings;
	private CommandCenter cCenter;
	private ArrayList<Unit> containedUnits;
	private long lastSpawnTime;
	
	private ArrayList<Lane> attachedLanes;
	private Transformation[] buildingSpots;
	private Shape shape;
	private boolean isStartNode;
	private Transformation cCenterTransform;
	private int ID;
	
	private HashMap<Double, Lane> laneMap;
	
	public Node(ConfigData parser, GameState gameState, Lane[] lanes, int id)
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
		List<String> laneNames = parser.getStringList(ParserKeys.lanes);
		for(String name : laneNames)
			for(Lane l : lanes)
				if(name.equals(l.getName()))
				{
					attachedLanes.add(l);
					l.addNode(this);
				}
		
		List<ConfigData> transforms = parser.getConfigList(ParserKeys.buildingSpots);
		buildingSpots = new Transformation[transforms.size()];
		for(int i = 0; i < transforms.size(); i++)
			buildingSpots[i] = new Transformation(transforms.get(i));
		
		laneMap = new HashMap<Double, Lane>();
		
		shape = Shape.buildFromParser(parser.getConfig(ParserKeys.shape));
		
		cCenterTransform = new Transformation(parser.getConfig(ParserKeys.commandCenterTransformation));
		
		isStartNode = Boolean.parseBoolean(parser.getString(ParserKeys.isStartNode));
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public boolean isContested()
	{
		return occupationStartTime >= 0;
	}
	
	public Player getInvader()
	{
		return invader;
	}
	
	public long getOccupationStartTime()
	{
		return occupationStartTime;
	}
	
	public Building[] getContainedBuildings()
	{
		ArrayList<Building> ret = new ArrayList<Building>(containedBuildings);
		if(cCenter != null)
		{
			ret.add(cCenter);
		}
		
		return ret.toArray(new Building[0]);
	}
	
	public Lane[] getAttachedLanes()
	{
		return (Lane[])attachedLanes.toArray();
	}
	
	public Unit[] getContainedUnits()
	{
		return (Unit[])containedUnits.toArray();
	}
	
	public Circle getBoundingCircle()
	{
		return shape.boundingCircle();
	}
	
	public CommandCenter getCommandCenter()
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
				double totalFlow = getTotal(flows.get(owner).getKey());
				Double[] currentFlowSet = flows.get(owner).getKey();
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
			
			for(int i = 0; i < attachedLanes.size(); i++)
			{
				attachedLanes.get(i).addPendingWaves(this);
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
		if(containedBuildings.size() >= buildingSpots.length)
		{
			return null;
		}
		return buildingSpots[containedBuildings.size()];
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
	
	/**
	 * Gets the position of the shape that makes up this Node, defined as the center of the Node.
	 * @return a Transformation representing the center of this Node.
	 */
	public Transformation getPosition()
	{
		return shape.position();
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
				b.update();
			if(cCenter != null)
				cCenter.update();
		}
		if(cCenter != null)
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
		cCenter = (CommandCenter) p.getCommandCenterDefinition().createCommandCenter(cCenterTransform, this);
		containedBuildings.clear();
		for(Lane l : attachedLanes)
		{
			l.addGate(this, owner);
		}
		invader = null;
		occupationStartTime = -1;
	}
	
	public Shape getShape()
	{
		return shape;
	}
	
	public boolean isStartNode()
	{
		return isStartNode;
	}
	
	public void setInvader(Player p)
	{
		invader = p;
		occupationStartTime = (long) (gameState.getTime()*1000);
		for(Lane l : attachedLanes)
			l.removeGate(this);
	}
	
	public int getID()
	{
		return ID;
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
}

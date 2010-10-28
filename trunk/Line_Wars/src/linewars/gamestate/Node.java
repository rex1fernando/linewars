package linewars.gamestate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;



import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.CommandCenter;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.shapes.*;
import linewars.parser.Parser;
import linewars.parser.ParserKeys;

public class Node {
	
	private GameState gameState;
	private Player owner;
	private Player invader;
	private long occupationTime;
	private boolean changedOwners;
	
	private ArrayList<Building> containedBuildings;
	private CommandCenter cCenter;
	private ArrayList<Unit> containedUnits;
	
	private ArrayList<Lane> attachedLanes;
	private Transformation[] buildingSpots;
	private Shape shape;
	private boolean isStartNode;
	private Transformation cCenterTransform;
	
	private HashMap<Double, Lane> laneMap;
	
	public Node(Parser parser, GameState gameState, Lane[] lanes)
	{
		changedOwners = false;
		invader = null;
		owner = null;
		occupationTime = 0;
		this.cCenter = null;
		containedUnits = new ArrayList<Unit>();
		containedBuildings = new ArrayList<Building>();
		
		this.gameState = gameState;
		
		attachedLanes = new ArrayList<Lane>();
		String[] laneNames = parser.getList(ParserKeys.lanes);
		for(String name : laneNames)
			for(Lane l : lanes)
				if(name.equals(l.getName()))
				{
					attachedLanes.add(l);
					l.addNode(this);
				}
		
		String[] transformNames = parser.getList(ParserKeys.buildingSpots);
		buildingSpots = new Transformation[transformNames.length];
		for(int i = 0; i < transformNames.length; i++)
			buildingSpots[i] = new Transformation(parser.getParser(transformNames[i]));
		
		laneMap = new HashMap<Double, Lane>();
		
		shape = Shape.buildFromParser(parser.getParser(ParserKeys.shape));
		
		cCenterTransform = new Transformation(parser.getParser(ParserKeys.commandCenterTransformation));
		
		isStartNode = Boolean.parseBoolean(parser.getStringValue(ParserKeys.isStartNode));
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public boolean isContested()
	{
		return occupationTime != 0;
	}
	
	public Player getInvader()
	{
		return invader;
	}
	
	public long getOccupationTime()
	{
		return occupationTime;
	}
	
	public Building[] getContainedBuildings()
	{
		return containedBuildings.toArray(new Building[0]);
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
		Random rand = new Random(gameState.getTimerTick());
		HashMap<Player, double[]> flows = getAllFlow();
		boolean foundDest;
		for(int i = 0; i < containedUnits.size(); i++)
		{
			foundDest = false;
			Lane destination = null;
			Player owner = containedUnits.get(i).getOwner();
			double totalFlow = getTotal(flows.get(owner));
			double[] currentFlowSet = flows.get(owner);
			double number = rand.nextDouble() * totalFlow;
			
			for(int j = 0; j < currentFlowSet.length; j++)
			{
				if(number <= currentFlowSet[j] && !foundDest)
				{
					destination = laneMap.get(currentFlowSet[j]);
					if(owner.isStartPoint(destination, this))
					{
						foundDest = true;
					}
				}
			}
			
			if(!foundDest)
			{
				containedUnits.remove(i);
			}else{
				destination.addToPending(this, containedUnits.get(i));
			}
		}
		
		for(int i = 0; i < attachedLanes.size(); i++)
		{
			attachedLanes.get(i).addPendingWaves(this);
		}
	}
	
	private double getTotal(double[] flows)
	{
		double ret = 0;
		for(int i = 0; i < flows.length; i++)
		{
			ret = ret + flows[i];
		}
		return ret;
	}
	
	//TODO I don't understand how this method works (Connor)
	/**
	 * This method is a helper method for spawnWaves that generates the array of flow values that spawnWaves
	 * uses to randomly decide what lanes to send each player's units to.
	 */
	private HashMap<Player, double[]> getAllFlow()
	{
		
		List<Player> players = gameState.getPlayers();
		HashMap<Player, double[]> ret = new HashMap<Player, double[]>();
		double[] flows;
		
		//Iterate through every player.
		for(int i = 0; i < players.size(); i++)
		{
			
			Lane[] l = gameState.getMap().getLanes();
			flows = new double[l.length];
			Player p = players.get(i);
			double total = 0;
			
			/**
			 * This loop is where the work gets done. It creates an array of unique doubles such that every
			 * element is the sum of itself and all of the elements before it. This allows the random
			 * lane chooser to pick a random double between 0 and the total while maintaining an arbitrary
			 * weight.
			 */
			for(int j = 0; j < l.length; j++)
			{
				total = p.getFlowDist(l[j]) + total;
				flows[j] = total;
				/**
				 * This map exists to maintain the mapping from each of the new doubles to the lanes, so the
				 * random lane chooser knows which values correspond to which lanes.
				 */
				laneMap.put(total, l[j]);
			}
			Arrays.sort(flows);
			ret.put(p, flows);
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
		for(Building b : containedBuildings)
		{
			b.update();
		}
		
		cCenter.update();
		
		generateWaves();
		
		if(changedOwners)
		{
			for(Lane l : attachedLanes)
			{
				l.addGate(this, owner);
			}
		}
	}
	
	public void setOwner(Player p)
	{
		owner = p;
		//TODO change the command center somewhere
		cCenter = (CommandCenter) p.getCommandCenterDefinition().createCommandCenter(cCenterTransform, this);
	}
	
	public Shape getShape()
	{
		return shape;
	}
	
	public boolean isStartNode()
	{
		return isStartNode;
	}
}

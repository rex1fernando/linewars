package linewars.gamestate.mapItems;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


import linewars.gamestate.GameState;
import linewars.gamestate.Lane;
import linewars.gamestate.Map;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.Unit;

public class Node {
	
	private Transformation trans;
	private Map gameMap;
	private GameState gameState;
	private Player owner;
	private Player invader;
	private long occupationTime;
	private ArrayList<Lane> attachedLanes;
	private ArrayList<Building> containedBuildings;
	private CommandCenter center;
	private ArrayList<Unit> containedUnits;
	private Transformation[] buildingSpots;
	private int numBuildings;
	
	private HashMap<Double, Lane> laneMap;
	
	/*
	 * TODO The Display needs the size of a Node to properly draw
	 * the colored circle over it in strategic view.
	 */
	private Dimension size;
	
	public Node(Player owner, Lane[] lanes, CommandCenter center, Transformation[] buildingSpots, Transformation trans)
	{
		this.trans = trans;
		this.owner = owner;
		this.center = center;
		invader = null; //Maybe have a special value to set this to in order to avoid null?
		occupationTime = 0;
		attachedLanes = new ArrayList<Lane>();
		for(int i = 0; i < lanes.length; i++)
		{
			attachedLanes.add(lanes[i]);
		}
		this.center = center;
		containedUnits = new ArrayList<Unit>();
		this.buildingSpots = buildingSpots;
		
		//TODO set the size of the Node
		size = new Dimension(100, 100);
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
		return (Building[])containedBuildings.toArray();
	}
	
	public Lane[] getAttachedLanes()
	{
		return (Lane[])attachedLanes.toArray();
	}
	
	public Unit[] getContainedUnits()
	{
		return (Unit[])containedUnits.toArray();
	}
	
	public int getNumBuildings()
	{
		return numBuildings;
	}
	
	public Dimension getSize()
	{
		return size;
	}
	
	//TODO The display also needs access to information regarding the center of the Node
	public CommandCenter getCommandCenter()
	{
		return center;
	}
	
	//TODO Get the timer tick to seed the random the same in all systems.
	public void generateWaves()
	{
		Random rand = new Random(50);
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
	
	private HashMap<Player, double[]> getAllFlow()
	{
		List<Player> players = gameState.getPlayers();
		HashMap<Player, double[]> ret = new HashMap<Player, double[]>();
		double[] flows;
		for(int i = 0; i < players.size(); i++)
		{
			Lane[] l = gameMap.getLanes();
			flows = new double[l.length];
			Player p = players.get(i);
			double total = 0;
			for(int j = 0; j < l.length; j++)
			{
				total = p.getFlowDist(l[j]) + total;
				flows[j] = total;
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
		if(numBuildings >= buildingSpots.length)
		{
			return null;
		}
		return buildingSpots[numBuildings];
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
		if(containedBuildings.add(b))
		{
			numBuildings++;
			return true;
		}
		return false;
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
	
	public Position getPosition()
	{
		return trans.getPosition();
	}
}

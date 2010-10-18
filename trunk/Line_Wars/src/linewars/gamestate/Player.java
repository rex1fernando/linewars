package linewars.gamestate;
import java.util.*;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.Lane;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Node;
import linewars.gamestate.mapItems.UnitDefinition;
import java.util.*;
public class Player {

	private Map map;
	private double stuffAmount;
	private ArrayList<Node> ownedNodes;
	private HashMap<Lane, Node> startPoints;
	private HashMap<Lane, Double> flowDist;
	private ArrayList<BuildingDefinition> buildingDefs;
	private ArrayList<UnitDefinition> unitDefs;
	private ArrayList<Tech> techLevels;
	private String name;
	
	public Player(double startingStuff, Node[] startingNodes, Race r){
		stuffAmount = startingStuff;
		ownedNodes = new ArrayList<Node>();
		
		for(int i = 0; i < startingNodes.length; i++)
		{
			ownedNodes.add(startingNodes[i]);
		}
		
		flowDist = new HashMap<Lane, Double>();
		startPoints = new HashMap<Lane, Node>();
		flowSetup();
		
		buildingDefs = new ArrayList<BuildingDefinition>();
		unitDefs = new ArrayList<UnitDefinition>();
		techLevels = new ArrayList<Tech>();
	}
	
	/**
	 * Sets up the initial flow distribution for this player. For each lane,
	 * set that lane's flow distribution to 50 and, if available, set an owned
	 * node attached to that lane as the start point for flow. If no owned node
	 * is attached to that lane, set a random one as the start point.
	 */
	private void flowSetup()
	{
		boolean foundOwnedNode = false;
		Lane[] lanes = map.getLanes();
		for(int i = 0; i < lanes.length; i++)
		{
			Node[] currentNodes = lanes[i].getNodes();
			for(int j = 0; j < currentNodes.length; j++)
			{
				if(ownedNodes.contains(currentNodes[j]) && !foundOwnedNode)
				{
					startPoints.put(lanes[i], currentNodes[j]);
					foundOwnedNode = true;
				}
			}
			if(!foundOwnedNode)
			{
				startPoints.put(lanes[i], lanes[i].getNodes()[0]);
			}
			flowDist.put(lanes[i], new Double(50));
		}
	}
	
	public double getStuff(){
		return stuffAmount;
	}
	
	public String getPlayerName(){
		return name;
	}
	
	public double getFlowDist(Node n){
		return flowDist.get(n);
	}
	
	public ArrayList<Node> getOwnedNodes(){
		return ownedNodes;
	}
	
	public UnitDefinition[] getUnitDefinitions(){
		return (UnitDefinition[])unitDefs.toArray();
	}
	
	public BuildingDefinition[] getBuildingDefintions()
	{
		return (BuildingDefinition[])buildingDefs.toArray();
	}
	
	public Tech[] getTech()
	{
		return (Tech[])techLevels.toArray();
	}
	
	//TODO implement addMapItem
	/**
	 * adds the given mapItem to the player's master list of owned items
	 * 
	 * @param m	the mapItem to add
	 */
	public void addMapItem(MapItem m)
	{
		
	}
	
	/**
	 * Adds the specified amount of Stuff to the Player's account.
	 * @param amount The amount of Stuff to be added. Note that negative values
	 * 					are simply ignored.
	 */
	public void addStuff(double amount)
	{
		if(amount > 0)
		{
			stuffAmount = stuffAmount + amount;
		}
	}
	
	/**
	 * Sets the magnitude of the flow distribution for the lane l to the specified
	 * value.
	 * @param l The lane which is having its distribution modified.
	 * @param val The new magnitude for the flow distribution.
	 */
	public void setFlowDist(Lane l, Double val)
	{
		flowDist.put(l, val);
	}
	
	/**
	 * Sets the specified node as the start point for flow, meaning units will move
	 * from node n to the opposite end of the lane.
	 * @param l The lane in question.
	 * @param n The desired starting node for the lane l.
	 */
	public void setStartPoint(Lane l, Node n)
	{
		startPoints.put(l, n);
	}
	
	/**
	 * spends the given amount of stuff, throws an exception if there
	 * isn't enough stuff left to spend
	 * 
	 * @param amount	the amount of stuff to spend
	 */
	public void spendStuff(double amount) throws IllegalArgumentException
	{
		if(amount > stuffAmount){
			throw new IllegalArgumentException("Not enough Stuff. " +name
					+" Tried spending " +amount +" Stuff but only has " +stuffAmount);
		}
		stuffAmount = stuffAmount - amount;
	}
	
}

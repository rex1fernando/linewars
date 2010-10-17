package linewars.gamestate;
import java.util.*;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Node;
import linewars.gamestate.mapItems.UnitDefinition;
import java.util.*;
public class Player {

	private double stuffAmount;
	private ArrayList<Node> ownedNodes;
	private HashMap<Node, Double> flowDist;
	private BuildingDefinition[] buildingDefs;
	private UnitDefinition[] unitDefs;
	private Tech[] techLevels;
	
	public Player(){
		//TODO 
	}
	
	public double getStuff(){
		return stuffAmount;
	}
	
	public double getFlowDist(Node n){
		return flowDist.get(n);
	}
	
	
	
	public ArrayList<Node> getOwnedNodes(){
		return ownedNodes;
	}
	
	public UnitDefinition[] getUnitDefinitions(){
		return unitDefs;
	}
	
	public BuildingDefinition[] getBuildingDefintions()
	{
		return buildingDefs;
	}
	
	public Tech[] getTech()
	{
		return techLevels;
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
	
	//TODO implement spendStuff
	/**
	 * spends the given amount of stuff, throws and exception if there
	 * isn't enough stuff left to spend
	 * 
	 * @param amount	the amount of stuff to spend
	 */
	public void spendStuff(double amount)
	{
		
	}
	
}

package linewars.gamestate;
import java.util.*;
import linewars.gamestate.mapItems.BuildingDefinition;
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
	
}

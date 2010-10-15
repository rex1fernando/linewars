package linewars.gamestate;
import java.util.*;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.Node;
import linewars.gamestate.mapItems.Tech;
import linewars.gamestate.mapItems.UnitDefinition;
import java.util.*;
public class Player {

	int stuffAmount;
	ArrayList<Node> ownedNodes;
	HashMap<Node, Double> flowDist;
	BuildingDefinition[] buildingDefs;
	UnitDefinition[] unitDefs;
	Tech[] techLevels;
	
	public BuildingDefinition[] getBuildingDefintions()
	{
		return buildingDefs;
	}
	
	public Tech[] getTech()
	{
		return techLevels;
	}
	
}

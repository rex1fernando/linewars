package linewars.gamestate.mapItems;

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

package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.Node;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.MapItemState;

/**
 * 
 * @author , Connor Schenck
 *
 * This class represents the ability that constructs a building.
 */
public strictfp class ConstructBuilding implements Ability {

	private BuildingDefinition buildingDefinition = null;
	private long startTime;
	private boolean built = false;
	private Building building = null;
	
	/**
	 * Creates the ability that constructs a building in node n
	 * using definition bd.
	 * 
	 * @param n
	 * @param bd
	 */
	public ConstructBuilding(Node n, BuildingDefinition bd)
	{
		buildingDefinition = bd;
		startTime = (long) (buildingDefinition.getGameState().getTime()*1000);
		
		Transformation t = n.getNextAvailableBuildingSpot();
		//can't construct the building
		if(t == null || bd.getOwner().getStuff() < bd.getCost())
		{
			built = true;
			return;
		}
		
		bd.getOwner().spendStuff(bd.getCost());
		
		building = buildingDefinition.createBuilding(t, n);
		building.setState(MapItemState.Constructing);
		if(!n.addBuilding(building))
			throw new RuntimeException("This should never happen: the building was not placed correctly.");
		building.getOwner().addMapItem(building);
	}
	
	@Override
	public void update() {
		if(!built && (long) (buildingDefinition.getGameState().getTime()*1000) - startTime >= buildingDefinition.getBuildTime())
		{
			built = true;
			building.setState(MapItemState.Idle);
		}
	}

	@Override
	public boolean killable() {
		return true;
	}

	@Override
	public boolean finished() {
		return built;
	}

}

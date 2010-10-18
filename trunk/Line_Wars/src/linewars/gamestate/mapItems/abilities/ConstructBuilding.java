package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Node;

/**
 * 
 * @author cschenck
 *
 * This class represents the ability that constructs a building.
 */
public class ConstructBuilding implements Ability {

	private BuildingDefinition buildingDefinition = null;
	private long startTime;
	private boolean built = false;
	private Building building = null;
	
	public ConstructBuilding(Node n, BuildingDefinition bd)
	{
		buildingDefinition = bd;
		startTime = System.currentTimeMillis();
		
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
		if(!built && System.currentTimeMillis() - startTime >= buildingDefinition.getBuildTime())
		{
			built = true;
			building.setState(MapItemState.Idle);
			for(AbilityDefinition ad : buildingDefinition.getAbilityDefinitions())
				if(ad.startsActive())
					building.addActiveAbility(ad.createAbility(building));
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

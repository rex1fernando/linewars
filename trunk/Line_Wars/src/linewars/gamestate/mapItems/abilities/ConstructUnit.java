package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.UnitDefinition;

/**
 * 
 * @author , Connor Schenck
 *
 * This class represents the ability that constructs units.
 */
public strictfp class ConstructUnit implements Ability {
	
	private Building building;
	private long startTime;
	private ConstructUnitDefinition def;
	
	public ConstructUnit(Building b, ConstructUnitDefinition def)
	{
		this.def = def;
		building = b;
		b.setState(MapItemState.Active);
		startTime = (long)(b.getDefinition().getGameState().getTime()*1000);
	}

	@Override
	public void update() {
		if((long)(building.getDefinition().getGameState().getTime()*1000) - startTime > def.getBuildTime())
		{
			if(building.getState() != MapItemState.Active)
				building.setState(MapItemState.Active);
			Unit u = def.getUnitDefinition().createUnit(building.getTransformation());
			building.getNode().addUnit(u);
			startTime = (long)(building.getDefinition().getGameState().getTime()*1000);
		}
	}

	@Override
	public boolean killable() {
		return true;
	}

	@Override
	public boolean finished() {
		return false;
	}

}

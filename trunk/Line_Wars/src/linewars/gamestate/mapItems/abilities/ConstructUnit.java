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
	
	private UnitDefinition unitDefinition;
	private Building building;
	private long startTime;
	private long buildTime;
	
	public ConstructUnit(UnitDefinition ud, Building b, long buildTime)
	{
		unitDefinition = ud;
		building = b;
		b.setState(MapItemState.Active);
		startTime = (long)(unitDefinition.getGameState().getTime()*1000);
		this.buildTime = buildTime;
	}

	@Override
	public void update() {
		if((long)(unitDefinition.getGameState().getTime()*1000) - startTime > buildTime)
		{
			if(building.getState() != MapItemState.Active)
				building.setState(MapItemState.Active);
			Unit u = unitDefinition.createUnit(building.getTransformation());
			building.getNode().addUnit(u);
			startTime = (long)(unitDefinition.getGameState().getTime()*1000);
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

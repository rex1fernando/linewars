package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.UnitDefinition;

public class ConstructUnit implements Ability {
	
	private UnitDefinition unitDefinition;
	private Building building;
	private long startTime;
	private long buildTime;
	
	public ConstructUnit(UnitDefinition ud, Building b, long buildTime)
	{
		unitDefinition = ud;
		building = b;
		b.setState(MapItemState.Active);
		startTime = System.currentTimeMillis();
		this.buildTime = buildTime;
	}

	@Override
	public void update() {
		if(System.currentTimeMillis() - startTime > buildTime)
		{
			Unit u = unitDefinition.createUnit(building.getTransformation());
			building.getNode().addUnit(u);
			startTime = System.currentTimeMillis();
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

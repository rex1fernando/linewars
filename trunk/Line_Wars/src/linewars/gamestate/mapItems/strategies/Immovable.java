package linewars.gamestate.mapItems.strategies;

import linewars.gamestate.mapItems.Unit;

public class Immovable implements MovementStrategy {

	@Override
	public void setUnit(Unit u) {}

	@Override
	public MovementStrategy copy() {
		return new Immovable();
	}

}

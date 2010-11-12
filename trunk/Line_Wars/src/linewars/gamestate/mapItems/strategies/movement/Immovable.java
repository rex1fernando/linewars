package linewars.gamestate.mapItems.strategies.movement;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.Unit;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines a movement strategy that doesn't move.
 */
public strictfp class Immovable implements MovementStrategy {

	@Override
	public void setUnit(Unit u) {}

	@Override
	public MovementStrategy copy() {
		return new Immovable();
	}

	@Override
	public double setTarget(Transformation t) {
		return 1;
	}

	@Override
	public void move() {}

}

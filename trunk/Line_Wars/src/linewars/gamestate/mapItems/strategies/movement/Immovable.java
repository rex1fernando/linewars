package linewars.gamestate.mapItems.strategies.movement;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.Unit;

/**
 * 
 * @author cschenck
 *
 * This class defines a movement strategy that doesn't move.
 */
public class Immovable implements MovementStrategy {

	@Override
	public void setUnit(Unit u) {}

	@Override
	public MovementStrategy copy() {
		return new Immovable();
	}

	@Override
	public double setTarget(Transformation t) {
		return 0;
	}

	@Override
	public void setIgnoreCollision(boolean ignore) {}

	@Override
	public void move(Unit[] possibleCollisions) {}

}

package linewars.gamestate.mapItems.strategies.turret;

import linewars.gamestate.Position;

public interface MinimumRangeTurretStrategy extends TurretStrategy {

	/**
	 * 
	 * @return
	 * The closest that this Turret ever wants to get to its target.
	 */
	public double getMinimumRange();
	
	public void setTarget(Position target);
}

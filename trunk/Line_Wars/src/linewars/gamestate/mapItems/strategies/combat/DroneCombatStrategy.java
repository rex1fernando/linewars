package linewars.gamestate.mapItems.strategies.combat;

import linewars.gamestate.mapItems.Unit;

public interface DroneCombatStrategy {

	/**
	 * 
	 * @param target	the target that this combat strategy is to use,
	 * 					allows null values
	 */
	public void setTarget(Unit target);
	
	/**
	 * Returns true if this drone has finished doing whatever it needs
	 * to on this target.
	 * 
	 * @return
	 */
	public boolean isFinishedOnTarget();
	
	/**
	 * Selects the best target from the list and returns it
	 * 
	 * @param targets
	 * @return
	 */
	public Unit pickBestTarget(Unit[] targets);
	
	/**
	 * Sets the carrier for this drone to carrier
	 * 
	 * @param carrier
	 */
	public void setDroneCarrier(Unit carrier);
	
	/**
	 * Returns the drone carrier for this drone
	 * 
	 * @return
	 */
	public Unit getDroneCarrier();
	
	/**
	 * Returns the last game state tick that the fight method
	 * of this combat strategy was called. This is used to tell
	 * when this drone has gone out of combat.
	 * 
	 * @return
	 */
	public int getLastFightTick();
	
}

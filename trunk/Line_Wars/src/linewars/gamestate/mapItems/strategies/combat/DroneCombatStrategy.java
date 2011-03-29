package linewars.gamestate.mapItems.strategies.combat;

import linewars.gamestate.mapItems.Unit;

public interface DroneCombatStrategy {

	/**
	 * 
	 * @param target	the target that this combat strategy is to use
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
	
}

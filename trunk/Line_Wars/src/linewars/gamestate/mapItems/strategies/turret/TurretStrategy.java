package linewars.gamestate.mapItems.strategies.turret;

import linewars.gamestate.mapItems.Turret;
import linewars.gamestate.mapItems.Unit;

public interface TurretStrategy {
	
	/**
	 * Sets the unit this combat strategy is associated with.
	 * 	
	 * @param u		the unit this strategy is associated with.
	 */
	public void setTurret(Turret t);
	
	/**
	 * Creates a copy of this combat strategy and returns it.
	 * 
	 * @return	a copy of this combat strategy
	 */
	public TurretStrategy copy();
	
	/**
	 * Returns the maximum range at which this combat would consider
	 * using an ability or doing anything other than moving.
	 * 
	 * @return	the range of this strategy
	 */
	public double getRange();
	
	/**
	 * Tells the combat strategy to employ the unit's combat tactics. It
	 * handles moving the unit when in combat, as well as using the abilities
	 * of the unit and any other actions that may be necessary for the unit
	 * to do in combat.
	 * 
	 * @param availableTargets	The list of possible targets for this unit engage
	 */
	public void fight(Unit[] availableTargets);

}

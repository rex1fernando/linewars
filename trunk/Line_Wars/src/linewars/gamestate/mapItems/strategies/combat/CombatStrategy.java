package linewars.gamestate.mapItems.strategies.combat;

import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.Strategy;

/**
 * 
 * @author , Connor Schenck
 *
 *This interface represents a combat strategy for a unit. It decides,
 *when in combat, how a unit should move and use its abilities.
 */
public strictfp interface CombatStrategy extends Strategy<CombatStrategyConfiguration> {
	
	
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
	 * @param availableEnemies	The list of possible targets for this unit engage
	 * @param availableAllies TODO
	 */
	public void fight(Unit[] availableEnemies, Unit[] availableAllies);

}

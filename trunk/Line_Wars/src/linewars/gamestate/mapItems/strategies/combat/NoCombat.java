package linewars.gamestate.mapItems.strategies.combat;

import linewars.gamestate.mapItems.Unit;

/**
 * 
 * @author cschenck
 *
 * This class defines a combat strategy that does not
 * engage in combat. It does nothing.
 */
public class NoCombat implements CombatStrategy {

	@Override
	public void setUnit(Unit u) 
	{}

	@Override
	public CombatStrategy copy() {
		return new NoCombat();
	}

	@Override
	public double getRange() {
		return 0;
	}

	@Override
	public void fight(Unit[] a) {}

}

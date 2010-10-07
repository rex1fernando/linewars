package linewars.gamestate.mapItems.strategies;

import linewars.gamestate.mapItems.Unit;

public class NoCombat implements CombatStrategy {

	@Override
	public void setUnit(Unit u) {}

	@Override
	public CombatStrategy copy() {
		return new NoCombat();
	}

}

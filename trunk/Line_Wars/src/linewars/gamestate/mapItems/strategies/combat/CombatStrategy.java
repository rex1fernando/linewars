package linewars.gamestate.mapItems.strategies.combat;

import linewars.gamestate.mapItems.Unit;

public interface CombatStrategy {
	
	public void setUnit(Unit u);
	
	public CombatStrategy copy();
	
	public double getRange();
	
	public void fight(Unit[] availableTargets);

}

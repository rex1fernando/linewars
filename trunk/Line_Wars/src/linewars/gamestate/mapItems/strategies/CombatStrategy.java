package linewars.gamestate.mapItems.strategies;

import linewars.gamestate.mapItems.Unit;

public interface CombatStrategy {
	
	public void setUnit(Unit u);
	
	public CombatStrategy copy();

}

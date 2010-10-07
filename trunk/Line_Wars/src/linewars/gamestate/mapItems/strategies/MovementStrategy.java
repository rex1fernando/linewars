package linewars.gamestate.mapItems.strategies;

import linewars.gamestate.mapItems.Unit;

public interface MovementStrategy {
	
	public void setUnit(Unit u);
	
	public MovementStrategy copy();

}

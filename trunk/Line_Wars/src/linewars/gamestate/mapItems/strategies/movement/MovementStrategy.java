package linewars.gamestate.mapItems.strategies.movement;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.Unit;

public interface MovementStrategy {
	
	public void setUnit(Unit u);
	
	public MovementStrategy copy();
	
	public double setTarget(Transformation t);
	
	public void setIgnoreCollision(boolean ignore);
	
	public void move(Unit[] possibleCollisions);

}

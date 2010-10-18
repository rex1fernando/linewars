package linewars.gamestate.mapItems.strategies.movement;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.Unit;

/**
 * 
 * @author cschenck
 *
 * This interface represents how a unit gets around on
 * the map. Before it can be told to move, the target
 * must be set and whether or not to ignore collision.
 */
public interface MovementStrategy {
	
	/**
	 * Sets the unit associated with this movement strategy
	 * 
	 * @param u		the unit that owns this strategy
	 */
	public void setUnit(Unit u);
	
	/**
	 * creates a copy of this movement strategy
	 * 
	 * @return	a copy of this movement strategy
	 */
	public MovementStrategy copy();
	
	/**
	 * Sets the target for this unit to move to. Returns
	 * how far the unit will actually move.
	 * 
	 * @param t		the target for this unit to move to
	 * @return		[0,1] where 1 = will move all the way there, 0.5 = will move half way there, etc.
	 */
	public double setTarget(Transformation t);
	
	/**
	 * Sets whether or not this movement strategy will ignore collision
	 * while it moves.
	 * 
	 * @param ignore	true to ignore collision.
	 */
	public void setIgnoreCollision(boolean ignore);
	
	/**
	 * Moves the unit to the target position, considering all
	 * the input units as possible units to collide with and
	 * handles the situation appropriately. DOES NOT PERFORM
	 * PATH FINDING.
	 * 
	 * @param possibleCollisions	the list of possible units for this unit to collide with.
	 */
	public void move(Unit[] possibleCollisions);

}

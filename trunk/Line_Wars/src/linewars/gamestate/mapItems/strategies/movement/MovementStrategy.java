package linewars.gamestate.mapItems.strategies.movement;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.Strategy;

/**
 * 
 * @author , Connor Schenck
 *
 * This interface represents how a unit gets around on
 * the map. Before it can be told to move, the target
 * must be set and whether or not to ignore collision.
 */
public strictfp interface MovementStrategy extends Strategy<MovementStrategyConfiguration> {
	
	
	/**
	 * Sets the target for this unit to move to. Returns on average
	 * how far the unit will actually move.
	 * 
	 * @param t		the target for this unit to move to
	 * @return		[0,1] where 1 = will move all the way there, 0.5 = will move half way there, etc.
	 */
	public double setTarget(Transformation t);
	
	/**
	 * Moves the unit to the target position, considering all
	 * the input units as possible units to collide with and
	 * handles the situation appropriately. DOES NOT PERFORM
	 * PATH FINDING.
	 * 
	 * @param possibleCollisions	the list of possible units for this unit to collide with.
	 */
	public void move();
	
	/**
	 * Notifies this MovementStrategy that its unit is currently colliding with something.
	 * The MovementStrategy is then responsible for modifying its future behavior to resolve the collision
	 * at some point in the future.  Note that this method must not cause the unit to move IMMEDIATELY,
	 * but rather ONLY THE NEXT TIME move() IS CALLED.  Bad things will happen otherwise.
	 * 
	 * @param direction
	 * Indicates the direction to the center of the object that this unit is colliding with.
	 * Note that the length of this vector is not important.
	 */
	public void notifyOfCollision(Position direction);

}

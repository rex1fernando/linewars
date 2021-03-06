package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.strategies.Strategy;

/**
 * 
 * @author , Connor Schenck
 *
 *This interface specifies how a mapItem can collide with other
 *mapItems. Each mapItem must have one.
 */
public strictfp interface CollisionStrategy extends Strategy<CollisionStrategyConfiguration> {
	
	/**
	 * Checks to see if this mapItem can collide with the given mapItem.
	 * 
	 * Here's the call heirarcy for collisions. Strategies must determine
	 * how a collision will work out for strategies lower than them on the
	 * list and defer to strategies higher up for collision.
	 * 
	 * NoCollision
	 * CollidesWithAll
	 * EnemyProjectiles
	 * AllEnemyUnits
	 * AllEnemies
	 * Ground
	 * 
	 * @param m		the mapItem colliding with this one
	 * @return		whether or not the two mapItems can collide
	 */
	public boolean canCollideWith(MapItem m);

}

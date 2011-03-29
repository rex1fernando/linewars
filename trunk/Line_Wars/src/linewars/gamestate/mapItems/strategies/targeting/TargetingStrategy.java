package linewars.gamestate.mapItems.strategies.targeting;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.Strategy;

public interface TargetingStrategy extends Strategy<TargetingStrategyConfiguration> {
	
	/**
	 * This method gets the target that the projectile should move to.
	 * It should only return where the projectile should move to, not actually
	 * move the projectile or calculate collisions.
	 * 
	 * @return	the transformation to move the projectile to
	 */
	public abstract Transformation getTarget();

}

package linewars.gamestate.mapItems.strategies;

import linewars.gamestate.mapItems.strategies.collision.CollisionStrategyConfiguration;

public interface Strategy<T extends StrategyConfiguration<?>> {
	
	/**
	 * Returns the name of this collision strategy
	 * 
	 * @return		the name of this collision strategy
	 */
	public String name();
	
	public T getConfig();

}

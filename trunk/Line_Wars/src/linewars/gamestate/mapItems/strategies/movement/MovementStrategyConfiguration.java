package linewars.gamestate.mapItems.strategies.movement;

import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

public abstract class MovementStrategyConfiguration extends StrategyConfiguration<MovementStrategy> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3537543624668064752L;

	static {
		StrategyConfiguration.setStrategyType("Movement", MovementStrategyConfiguration.class);
	}
	
}

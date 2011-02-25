package linewars.gamestate.mapItems.strategies.movement;

import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

public abstract class MovementStrategyConfiguration extends StrategyConfiguration<MovementStrategy> {

	static {
		StrategyConfiguration.setStrategyType("Movement", MovementStrategyConfiguration.class);
	}
	
}

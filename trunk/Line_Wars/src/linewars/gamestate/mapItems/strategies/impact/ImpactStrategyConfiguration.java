package linewars.gamestate.mapItems.strategies.impact;

import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

public abstract class ImpactStrategyConfiguration extends StrategyConfiguration<ImpactStrategy> {

	static {
		StrategyConfiguration.setStrategyType("Impact", ImpactStrategyConfiguration.class);
	}
	
}

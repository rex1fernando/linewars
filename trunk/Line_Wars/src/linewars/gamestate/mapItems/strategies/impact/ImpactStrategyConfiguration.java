package linewars.gamestate.mapItems.strategies.impact;

import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

public abstract class ImpactStrategyConfiguration extends StrategyConfiguration<ImpactStrategy> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6847239916954472927L;

	static {
		StrategyConfiguration.setStrategyType("Impact", ImpactStrategyConfiguration.class);
	}
	
}

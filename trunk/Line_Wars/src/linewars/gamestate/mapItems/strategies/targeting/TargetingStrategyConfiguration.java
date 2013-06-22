package linewars.gamestate.mapItems.strategies.targeting;

import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

public abstract class TargetingStrategyConfiguration extends StrategyConfiguration<TargetingStrategy> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6387814164912546526L;

	static {
		StrategyConfiguration.setStrategyType("Targeting", TargetingStrategyConfiguration.class);
	}

}

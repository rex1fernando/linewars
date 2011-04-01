package linewars.gamestate.mapItems.strategies.combat;

import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import configuration.Configuration;


public abstract class CombatStrategyConfiguration extends StrategyConfiguration<CombatStrategy> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2110197635705723644L;

	static {
		StrategyConfiguration.setStrategyType("Combat", CombatStrategyConfiguration.class);
	}

}

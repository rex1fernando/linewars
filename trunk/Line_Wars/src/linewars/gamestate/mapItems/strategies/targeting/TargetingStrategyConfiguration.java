package linewars.gamestate.mapItems.strategies.targeting;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.turret.TurretStrategyConfiguration;

public abstract class TargetingStrategyConfiguration extends StrategyConfiguration<TargetingStrategy> {
	
	static {
		StrategyConfiguration.setStrategyType("Targeting", TargetingStrategyConfiguration.class);
	}

}

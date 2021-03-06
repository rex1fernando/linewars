package linewars.gamestate.mapItems.strategies.turret;

import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

public abstract class TurretStrategyConfiguration extends StrategyConfiguration<TurretStrategy> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5719646641415322578L;

	static {
		StrategyConfiguration.setStrategyType("Turret", TurretStrategyConfiguration.class);
	}
	
}

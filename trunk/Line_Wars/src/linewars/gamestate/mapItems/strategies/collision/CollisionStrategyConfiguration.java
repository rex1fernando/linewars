package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import configuration.Configuration;

public abstract class CollisionStrategyConfiguration extends StrategyConfiguration<CollisionStrategy> {
	static {
		StrategyConfiguration.setStrategyType("Collision", CollisionStrategyConfiguration.class);
	}
}

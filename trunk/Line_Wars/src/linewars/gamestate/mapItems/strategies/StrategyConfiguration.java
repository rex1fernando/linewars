package linewars.gamestate.mapItems.strategies;

import linewars.gamestate.mapItems.MapItem;
import configuration.Configuration;

public abstract class StrategyConfiguration<T> extends Configuration {
	
	public abstract T createStrategy(MapItem m);
	
	public abstract boolean equals(Object obj);

}

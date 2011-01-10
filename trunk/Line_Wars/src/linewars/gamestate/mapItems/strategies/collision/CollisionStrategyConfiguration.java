package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.MapItem;
import configuration.Configuration;

public abstract class CollisionStrategyConfiguration extends Configuration {
	
	public abstract CollisionStrategy createStrategy(MapItem m);
	
	public abstract boolean equals(Object obj);

}

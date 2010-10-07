package linewars.gamestate.mapItems.strategies;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;

public interface CollisionStrategy {
	
	public boolean isValidMapItem(MapItemDefinition m);
	
	public CollisionStrategy createInstanceOf(MapItem m);
	
	public boolean canCollideWith(MapItem m);
	
	public String name();

}

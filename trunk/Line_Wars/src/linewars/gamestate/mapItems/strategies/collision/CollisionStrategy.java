package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;

public interface CollisionStrategy {
	
	public boolean isValidMapItem(MapItemDefinition m);
	
	public CollisionStrategy createInstanceOf(MapItem m);
	
	public boolean canCollideWith(MapItem m);
	
	public String name();
	
	/*
	 * Here's the call heirarcy for collisions. Strategies must determine
	 * how a collision will work out for strategies lower than them on the
	 * list and defer to strategies higher up for collision.
	 * 
	 * NoCollision
	 * AllEnemies
	 * Ground
	 */

}

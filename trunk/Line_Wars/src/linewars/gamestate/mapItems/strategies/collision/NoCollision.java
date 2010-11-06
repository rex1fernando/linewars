package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;

/**
 * 
 * @author cschenck
 *
 *	This class defines a collision strategy for map items
 *	that can't collide with anything.
 */
public strictfp class NoCollision implements CollisionStrategy {
	
	public NoCollision() {}

	@Override
	public boolean isValidMapItem(MapItemDefinition m) {
		return true;
	}

	@Override
	public CollisionStrategy createInstanceOf(MapItem m) {
		return new NoCollision();
	}

	@Override
	public boolean canCollideWith(MapItem m) {
		return false;
	}

	@Override
	public String name() {
		return "No Collision Strataegy";
	}

}

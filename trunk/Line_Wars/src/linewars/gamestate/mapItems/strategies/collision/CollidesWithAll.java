package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;

/**
 * 
 * @author Connor Schenck
 *
 */
public strictfp class CollidesWithAll implements CollisionStrategy {

	private MapItem mapItem = null;
	
	@Override
	public boolean canCollideWith(MapItem m) {
		if(m.getCollisionStrategy() instanceof NoCollision)
			return m.getCollisionStrategy().canCollideWith(mapItem);
		else
		return true;
	}

	@Override
	public CollisionStrategy createInstanceOf(MapItem m) {
		CollidesWithAll cwa = new CollidesWithAll();
		cwa.mapItem = m;
		return cwa;
	}

	@Override
	public boolean isValidMapItem(MapItemDefinition m) {
		return true;
	}

	@Override
	public String name() {
		return "Collides with all";
	}

}

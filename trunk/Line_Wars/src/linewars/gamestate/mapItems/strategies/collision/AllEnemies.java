package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;

/**
 * 
 * @author cschenck
 *
 *This class is a type of CollisionStrategy. It defines a strategy that collides with
 *only enemies.
 */
public class AllEnemies implements CollisionStrategy {
	
	private MapItem mapItem = null;
	
	public AllEnemies() {}
	
	public AllEnemies(MapItem m)
	{
		mapItem = m;
	}

	@Override
	public boolean isValidMapItem(MapItemDefinition m) {
		return true;
	}

	@Override
	public CollisionStrategy createInstanceOf(MapItem m) {
		return new AllEnemies(m);
	}

	@Override
	public boolean canCollideWith(MapItem m) {
		if((m.getCollisionStrategy() instanceof AllEnemies) || (m.getCollisionStrategy() instanceof Ground))
			return m.getOwner().equals(mapItem.getOwner());
		else
			return m.getCollisionStrategy().canCollideWith(mapItem);
	}

	@Override
	public String name() {
		return "All Enemies Collision Strategy";
	}

}

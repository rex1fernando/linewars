package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;

/**
 * 
 * @author cschenck
 *
 * This class defines a collision strategy for ground map items.
 * That is, any map item that is considered on the "ground".
 */
public strictfp class Ground implements CollisionStrategy {
	
	private MapItem mapItem = null;
	
	public Ground(){}
	
	public Ground(MapItem m)
	{
		mapItem = m;
	}

	@Override
	public boolean isValidMapItem(MapItemDefinition m) {
		return true;
	}

	@Override
	public CollisionStrategy createInstanceOf(MapItem m) {
		return new Ground(m);
	}

	@Override
	public boolean canCollideWith(MapItem m) {
		if(mapItem == null)
			throw new IllegalStateException("This collision strategy isn't associated with a map item.");
		
		if(m.getCollisionStrategy() instanceof Ground)
			return true;
		else
			return m.getCollisionStrategy().canCollideWith(mapItem);
	}

	@Override
	public String name() {
		return "Ground Collision Strategy";
	}

}

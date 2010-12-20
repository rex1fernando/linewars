package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.LaneBorder;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.MapItemDefinition;

/**
 * 
 * @author Connor Schenck
 * 
 * This class defines a collision strategy that collides only with
 * enemy units
 *
 */
public strictfp class AllEnemyUnits implements CollisionStrategy {
	
	private MapItem mapItem = null;

	@Override
	public boolean isValidMapItem(MapItemDefinition m) {
		return true;
	}

	@Override
	public CollisionStrategy createInstanceOf(MapItem m) {
		AllEnemyUnits aeu = new AllEnemyUnits();
		aeu.mapItem = m;
		return aeu;
	}

	@Override
	public boolean canCollideWith(MapItem m) {
		if(m.getCollisionStrategy() instanceof AllEnemyUnits ||
				m.getCollisionStrategy() instanceof AllEnemies ||
				m.getCollisionStrategy() instanceof Ground)
			return !mapItem.getOwner().equals(m.getOwner()) && (m instanceof Unit || m instanceof LaneBorder);
		else
			return m.getCollisionStrategy().canCollideWith(mapItem);
	}

	@Override
	public String name() {
		return "All Enemy Units";
	}

}

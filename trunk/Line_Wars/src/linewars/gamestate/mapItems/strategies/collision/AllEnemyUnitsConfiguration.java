package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.collision.AllEnemiesConfiguration.AllEnemies;
import linewars.gamestate.mapItems.strategies.collision.GroundConfiguration.Ground;

/**
 * 
 * @author Connor Schenck
 * 
 * This class defines a collision strategy that collides only with
 * enemy units
 *
 */
public strictfp class AllEnemyUnitsConfiguration extends CollisionStrategyConfiguration {
	
	public class AllEnemyUnits implements CollisionStrategy
	{
		private MapItem mapItem = null;
		
		private AllEnemyUnits(MapItem m)
		{
			mapItem = m;
		}

		@Override
		public boolean canCollideWith(MapItem m) {
			if(m.getCollisionStrategy() instanceof AllEnemyUnits ||
					m.getCollisionStrategy() instanceof AllEnemies ||
					m.getCollisionStrategy() instanceof Ground)
				return !mapItem.getOwner().equals(m.getOwner()) && (m instanceof Unit);
			else
				return m.getCollisionStrategy().canCollideWith(mapItem);
		}

		@Override
		public String name() {
			return "All Enemy Units";
		}

		@Override
		public CollisionStrategyConfiguration getConfig() {
			return AllEnemyUnitsConfiguration.this;
		}
	}

	@Override
	public CollisionStrategy createStrategy(MapItem m) {
		return new AllEnemyUnits(m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AllEnemiesConfiguration);
	}
	
	

}
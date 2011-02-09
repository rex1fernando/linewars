package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.strategies.collision.NoCollisionConfiguration.NoCollision;

/**
 * 
 * @author Connor Schenck
 * 
 * this strategy represents a collision strategy that collides
 * with everything
 *
 */
public strictfp class CollidesWithAllConfiguration extends CollisionStrategyConfiguration {

	public class CollidesWithAll implements CollisionStrategy
	{
	
		private MapItem mapItem = null;
		
		private CollidesWithAll(MapItem m)
		{
			mapItem = m;
		}
		
		@Override
		public boolean canCollideWith(MapItem m) {
			if(m.getCollisionStrategy() instanceof NoCollision)
				return m.getCollisionStrategy().canCollideWith(mapItem);
			else
				return true;
		}
	
		@Override
		public String name() {
			return "Collides with all";
		}

		@Override
		public CollisionStrategyConfiguration getConfig() {
			return CollidesWithAllConfiguration.this;
		}
	}

	@Override
	public CollisionStrategy createStrategy(MapItem m) {
		return new CollidesWithAll(m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof CollidesWithAllConfiguration);
	}

}

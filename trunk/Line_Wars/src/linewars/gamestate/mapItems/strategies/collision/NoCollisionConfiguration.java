package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;

/**
 * 
 * @author , Connor Schenck
 *
 *	This class defines a collision strategy for map items
 *	that can't collide with anything.
 */
public strictfp class NoCollisionConfiguration extends CollisionStrategyConfiguration {
	
	public class NoCollision implements CollisionStrategy
	{
	
		private NoCollision() {}
	
		@Override
		public boolean canCollideWith(MapItem m) {
			return false;
		}
	
		@Override
		public String name() {
			return "No Collision Strataegy";
		}

		@Override
		public CollisionStrategyConfiguration getConfig() {
			return NoCollisionConfiguration.this;
		}
	}

	@Override
	public CollisionStrategy createStrategy(MapItem m) {
		return new NoCollision();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof NoCollisionConfiguration);
	}

}

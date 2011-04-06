package linewars.gamestate.mapItems.strategies.collision;

import editor.abilitiesstrategies.AbilityStrategyEditor;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

/**
 * 
 * @author , Connor Schenck
 *
 *	This class defines a collision strategy for map items
 *	that can't collide with anything.
 */
public strictfp class NoCollisionConfiguration extends CollisionStrategyConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8971341401782308094L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("No Collision",
				NoCollisionConfiguration.class, AbilityStrategyEditor.class);
	}
	
	class NoCollision implements CollisionStrategy
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

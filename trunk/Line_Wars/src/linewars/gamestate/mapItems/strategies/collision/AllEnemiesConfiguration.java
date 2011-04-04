package linewars.gamestate.mapItems.strategies.collision;

import editor.abilitiesstrategies.AbilityStrategyEditor;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.collision.GroundConfiguration.Ground;

/**
 * 
 * @author , Connor Schenck
 *
 *This class is a type of CollisionStrategy. It defines a strategy that collides with
 *only enemies.
 */
public strictfp class AllEnemiesConfiguration extends CollisionStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6974832296463333018L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("All Enemies",
				AllEnemiesConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class AllEnemies implements CollisionStrategy
	{
		private MapItem mapItem = null;
		
		private AllEnemies(MapItem m)
		{
			mapItem = m;
		}

		@Override
		public boolean canCollideWith(MapItem m) {
			return !m.getOwner().equals(mapItem.getOwner());
		}

		@Override
		public String name() {
			return "All Enemies Collision Strategy";
		}

		@Override
		public CollisionStrategyConfiguration getConfig() {
			return AllEnemiesConfiguration.this;
		}
	}
	
	
	public AllEnemiesConfiguration() {}

	@Override
	public CollisionStrategy createStrategy(MapItem m) {
		return new AllEnemies(m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AllEnemiesConfiguration);
	}

}

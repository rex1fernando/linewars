package linewars.gamestate.mapItems.strategies.collision;

import editor.abilitiesstrategies.AbilityStrategyEditor;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

public class FlyingConfiguration extends CollisionStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 752324523627082913L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Flying",
				FlyingConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class Flying implements CollisionStrategy
	{
		private MapItem m;
		
		private Flying(MapItem m)
		{
			this.m = m;
		}

		@Override
		public String name() {
			return "Flying Collision Strategy";
		}

		@Override
		public CollisionStrategyConfiguration getConfig() {
			return FlyingConfiguration.this;
		}

		@Override
		public boolean canCollideWith(MapItem m) {
			return (m.getCollisionStrategy() instanceof Flying);
		}
		
	}

	@Override
	public CollisionStrategy createStrategy(MapItem m) {
		return new Flying(m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof FlyingConfiguration);
	}

}

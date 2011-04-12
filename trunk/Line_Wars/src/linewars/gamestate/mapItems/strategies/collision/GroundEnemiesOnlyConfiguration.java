package linewars.gamestate.mapItems.strategies.collision;

import editor.abilitiesstrategies.AbilityStrategyEditor;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.collision.GroundConfiguration.Ground;

public class GroundEnemiesOnlyConfiguration extends
		CollisionStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5277254781549608479L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Ground Enemies",
				GroundEnemiesOnlyConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class GroundEnemiesOnly implements CollisionStrategy
	{
		private  MapItem m;
		
		private GroundEnemiesOnly(MapItem m)
		{
			this.m = m;
		}
		
		@Override
		public String name() {
			return "Collides with ground enemies";
		}

		@Override
		public CollisionStrategyConfiguration getConfig() {
			return GroundEnemiesOnlyConfiguration.this;
		}

		@Override
		public boolean canCollideWith(MapItem m) {
			return ((m.getCollisionStrategy() instanceof Ground) ||
					(m.getCollisionStrategy() instanceof GroundEnemiesOnly)) &&
					!m.getOwner().equals(this.m.getOwner());
		}
		
	}

	@Override
	public CollisionStrategy createStrategy(MapItem m) {
		return new GroundEnemiesOnly(m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof GroundEnemiesOnlyConfiguration);
	}

}

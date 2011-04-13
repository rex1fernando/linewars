package linewars.gamestate.mapItems.strategies.collision;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import editor.abilitiesstrategies.AbilityStrategyEditor;

public class EnemyProjectilesConfiguration extends
		CollisionStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6121934056614686805L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Enemy Projectiles",
				EnemyProjectilesConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class EnemyProjectile implements CollisionStrategy
	{
		
		private MapItem mapItem;
		
		private EnemyProjectile(MapItem m)
		{
			mapItem = m;
		}

		@Override
		public String name() {
			return "Collides with enemy projectiles";
		}

		@Override
		public CollisionStrategyConfiguration getConfig() {
			return EnemyProjectilesConfiguration.this;
		}

		@Override
		public boolean canCollideWith(MapItem m) {
			return m.getOwner() != mapItem.getOwner() && m instanceof Projectile;
		}
		
	}

	@Override
	public CollisionStrategy createStrategy(MapItem m) {
		return new EnemyProjectile(m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof EnemyProjectilesConfiguration);
	}

}

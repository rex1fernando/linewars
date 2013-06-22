package linewars.gamestate.mapItems.strategies.collision;

import editor.abilitiesstrategies.AbilityStrategyEditor;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

/**
 * 
 * @author Connor Schenck
 * 
 * this strategy represents a collision strategy that collides
 * with everything
 *
 */
public strictfp class CollidesWithAllConfiguration extends CollisionStrategyConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4045511208789704059L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Collides with All",
				CollidesWithAllConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class CollidesWithAll implements CollisionStrategy
	{
	
		private MapItem mapItem = null;
		
		private CollidesWithAll(MapItem m)
		{
			mapItem = m;
		}
		
		@Override
		public boolean canCollideWith(MapItem m) {
			return m.getCollisionStrategy().canCollideWith(mapItem);
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

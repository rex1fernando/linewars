package linewars.gamestate.mapItems.strategies.collision;

import editor.abilitiesstrategies.AbilityStrategyEditor;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines a collision strategy for ground map items.
 * That is, any map item that is considered on the "ground".
 */
public strictfp class GroundConfiguration extends CollisionStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7609432744841062284L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Ground",
				GroundConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class Ground implements CollisionStrategy
	{
	
		private MapItem mapItem = null;
		
		private Ground(MapItem m)
		{
			mapItem = m;
		}
	
		@Override
		public boolean canCollideWith(MapItem m) {
			if(mapItem == null)
				throw new IllegalStateException("This collision strategy isn't associated with a map item.");
			
			return (m.getCollisionStrategy() instanceof Ground);
		}
	
		@Override
		public String name() {
			return "Ground Collision Strategy";
		}

		@Override
		public CollisionStrategyConfiguration getConfig() {
			return GroundConfiguration.this;
		}
	}

	@Override
	public CollisionStrategy createStrategy(MapItem m) {
		return new Ground(m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof GroundConfiguration);
	}

}

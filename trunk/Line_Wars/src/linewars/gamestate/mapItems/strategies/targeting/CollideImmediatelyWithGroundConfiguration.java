package linewars.gamestate.mapItems.strategies.targeting;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import editor.abilitiesstrategies.AbilityStrategyEditor;

public strictfp class CollideImmediatelyWithGroundConfiguration extends TargetingStrategyConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3899623323676296665L;
	
	static {
		StrategyConfiguration.setStrategyConfigMapping("Collide Immediately With Ground",
				CollideImmediatelyWithGroundConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public strictfp class CollideImmediatelyWithGround implements TargetingStrategy
	{

		private Projectile m;
		
		private CollideImmediatelyWithGround(Projectile m)
		{
			this.m = m;
		}
		
		@Override
		public String name() {
			return "Collide with ground immediately";
		}

		@Override
		public TargetingStrategyConfiguration getConfig() {
			return CollideImmediatelyWithGroundConfiguration.this;
		}

		@Override
		public Transformation getTarget() {
			m.getImpactStrategy().handleImpact(m.getPosition());
			return m.getTransformation();
		}
		
	}

	@Override
	public TargetingStrategy createStrategy(MapItem m) {
		return new CollideImmediatelyWithGround((Projectile) m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof CollideImmediatelyWithGroundConfiguration);
	}

}

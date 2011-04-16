package linewars.gamestate.mapItems.strategies.targeting;

import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategy;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategyConfiguration;

public strictfp class CollideWithGroundAfterDelayConfiguration extends
		TargetingStrategyConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7273370114992630565L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Collide With Ground after Delay",
				CollideWithGroundAfterDelayConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public strictfp class CollideWithGroundAfterDelay implements TargetingStrategy
	{

		private Projectile m;
		private ImpactStrategy oldImpact;
		private double startTime;
		private boolean finished = false;
		
		private CollideWithGroundAfterDelay(Projectile m)
		{
			this.m = m;
			oldImpact = m.getImpactStrategy();
			startTime = m.getGameState().getTime();
			m.setImpactStrategy(new ImpactStrategy() {
				
				@Override
				public String name() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public ImpactStrategyConfiguration getConfig() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public void handleImpact(Position p) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void handleImpact(MapItem m) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		@Override
		public String name() {
			return "Collide with ground immediately";
		}

		@Override
		public TargetingStrategyConfiguration getConfig() {
			return CollideWithGroundAfterDelayConfiguration.this;
		}

		@Override
		public Transformation getTarget() {
			if(!finished && m.getGameState().getTime() - startTime > getDelay())
			{
				m.setImpactStrategy(oldImpact);
				m.getImpactStrategy().handleImpact(m.getPosition());
				finished = true;
			}
			return m.getTransformation();
		}
		
	}
	
	public CollideWithGroundAfterDelayConfiguration()
	{
		super.setPropertyForName("delay", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The time in seconds to delay the impact with the ground"));
	}
	
	public double getDelay()
	{
		return (Double)super.getPropertyForName("delay").getValue();
	}

	@Override
	public TargetingStrategy createStrategy(MapItem m) {
		return new CollideWithGroundAfterDelay((Projectile) m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof CollideWithGroundAfterDelayConfiguration);
	}

}

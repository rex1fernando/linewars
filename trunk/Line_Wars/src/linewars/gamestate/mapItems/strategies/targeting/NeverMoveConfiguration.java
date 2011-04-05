package linewars.gamestate.mapItems.strategies.targeting;

import editor.abilitiesstrategies.AbilityStrategyEditor;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.impact.CatchTargetOnFireConfiguration;

public class NeverMoveConfiguration extends TargetingStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4448498217478051135L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Never Move",
				NeverMoveConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class NeverMove implements TargetingStrategy
	{
		private Projectile proj;
		
		private NeverMove(Projectile p)
		{
			proj = p;
		}

		@Override
		public String name() {
			return "Never, EVER move";
		}

		@Override
		public TargetingStrategyConfiguration getConfig() {
			return NeverMoveConfiguration.this;
		}

		@Override
		public Transformation getTarget() {
			return proj.getTransformation();
		}
		
	}

	@Override
	public TargetingStrategy createStrategy(MapItem m) {
		return new NeverMove((Projectile) m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof NeverMoveConfiguration);
	}

}

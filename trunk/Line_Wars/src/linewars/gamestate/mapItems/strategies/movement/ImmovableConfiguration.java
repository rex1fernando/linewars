package linewars.gamestate.mapItems.strategies.movement;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import editor.abilitiesstrategies.AbilityStrategyEditor;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines a movement strategy that doesn't move.
 */
public strictfp class ImmovableConfiguration extends MovementStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6900872710747929102L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Immovable",
				ImmovableConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class Immovable implements MovementStrategy
	{
		
		private Immovable() {}
	
		@Override
		public double setTarget(Transformation t) {
			return 1;
		}
	
		@Override
		public void move() {}

		@Override
		public String name() {
			return "Immovable";
		}

		@Override
		public MovementStrategyConfiguration getConfig() {
			return ImmovableConfiguration.this;
		}

		@Override
		public void notifyOfCollision(Position direction) {
			//this method should do nothing, as this unit can never move!
		}
	}

	@Override
	public MovementStrategy createStrategy(MapItem m) {
		return new Immovable();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ImmovableConfiguration);
	}

}

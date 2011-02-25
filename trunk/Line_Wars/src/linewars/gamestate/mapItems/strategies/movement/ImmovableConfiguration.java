package linewars.gamestate.mapItems.strategies.movement;

import editor.abilitiesstrategies.AbilityStrategyEditor;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.collision.AllEnemiesConfiguration;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines a movement strategy that doesn't move.
 */
public strictfp class ImmovableConfiguration extends MovementStrategyConfiguration {
	
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

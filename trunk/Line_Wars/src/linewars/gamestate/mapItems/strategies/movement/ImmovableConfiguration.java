package linewars.gamestate.mapItems.strategies.movement;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Unit;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines a movement strategy that doesn't move.
 */
public strictfp class ImmovableConfiguration extends MovementStrategyConfiguration {
	
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

package linewars.gamestate.mapItems.strategies.combat;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Unit;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines a combat strategy that does not
 * engage in combat. It does nothing.
 */
public strictfp class NoCombatConfiguration extends CombatStrategyConfiguration {
	
	public class NoCombat implements CombatStrategy
	{

		private NoCombat() {}
	
		@Override
		public double getRange() {
			return 0;
		}
	
		@Override
		public void fight(Unit[] a) {}

		@Override
		public String name() {
			return "No Combat";
		}

		@Override
		public CombatStrategyConfiguration getConfig() {
			return NoCombatConfiguration.this;
		}
	}

	@Override
	public CombatStrategy createStrategy(MapItem m) {
		return new NoCombat();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof NoCombatConfiguration);
	}

}

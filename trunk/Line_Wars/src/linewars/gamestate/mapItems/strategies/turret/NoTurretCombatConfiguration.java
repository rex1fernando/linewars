package linewars.gamestate.mapItems.strategies.turret;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Turret;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import editor.abilitiesstrategies.AbilityStrategyEditor;

public class NoTurretCombatConfiguration extends TurretStrategyConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4701862695867278200L;
	
	static {
		StrategyConfiguration.setStrategyConfigMapping("No Turret Combat",
				NoTurretCombatConfiguration.class, AbilityStrategyEditor.class);
	}

	@Override
	public TurretStrategy createStrategy(final MapItem m) {
		return new TurretStrategy() {
			
			@Override
			public String name() {
				return "No Turret Combat";
			}
			
			@Override
			public TurretStrategyConfiguration getConfig() {
				return NoTurretCombatConfiguration.this;
			}
			
			@Override
			public double getRange() {
				return ((Turret)m).getWave().getLane().getWidth();
			}
			
			@Override
			public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof NoTurretCombatConfiguration);
	}

}

package linewars.gamestate.mapItems.strategies.turret;

import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategy;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategyConfiguration;

public class MeleeDamageConfiguration extends TurretStrategyConfiguration {
	
	static {
		StrategyConfiguration.setStrategyConfigMapping("Melee Damage Around Turret",
				MeleeDamageConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private double damage;
	//private double //TODO how to figure out how large the hit box should be?
	
	public class MeleeDamage implements TurretStrategy
	{

		private MeleeDamage()
		{
			
		}
		
		@Override
		public String name() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TurretStrategyConfiguration getConfig() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public double getRange() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public MeleeDamageConfiguration()
	{
		this.setPropertyForName("damage", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, null, EditorUsage.PositiveReal,
				"The damage dealt per second"));
	}

	@Override
	public TurretStrategy createStrategy(MapItem m) {
		// TODO Auto-generated method stub
		compile error
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

}

package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Unit;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class HealWhenNotInCombatConfiguration extends AbilityDefinition {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9179324593382086379L;

	static {
		AbilityDefinition.setAbilityConfigMapping("Heal when not in combat",
				HealWhenNotInCombatConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class HealWhenNotInCombat implements Ability
	{
		private Unit u;
		
		private HealWhenNotInCombat(Unit u)
		{
			this.u = u;
		}
		
		@Override
		public void update() {
			if(!u.getWave().isInCombat())
			{
				u.setHP(u.getHP() + getHPS()*u.getGameState().getLastLoopTime());
			}
		}

		@Override
		public boolean killable() {
			return true;
		}

		@Override
		public boolean finished() {
			return false;
		}
		
	}
	
	public HealWhenNotInCombatConfiguration()
	{
		super.setPropertyForName("hps", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "The amount of healing to do per second"));
	}
	
	public double getHPS()
	{
		return (Double)super.getPropertyForName("hps").getValue();
	}
	
	@Override
	public boolean startsActive() {
		return true;
	}

	@Override
	public Ability createAbility(MapItem m) {
		return new HealWhenNotInCombat((Unit) m);
	}

	@Override
	public String getName() {
		return "Heal when not in combat";
	}

	@Override
	public String getDescription() {
		return "Heals the unit when it is not in combat";
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof HealWhenNotInCombatConfiguration) &&
				((HealWhenNotInCombatConfiguration) o).getHPS() == getHPS();
	}

}

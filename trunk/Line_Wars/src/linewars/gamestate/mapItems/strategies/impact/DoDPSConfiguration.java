package linewars.gamestate.mapItems.strategies.impact;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public strictfp class DoDPSConfiguration extends ImpactStrategyConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5469258878542063451L;
	
	private static final String name = "Do DPS and Remain";
	
	static{
		StrategyConfiguration.setStrategyConfigMapping(name, DoDPSConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private static final String dpsName = "dps";
	private static final Usage dpsUsage = Usage.NUMERIC_FLOATING_POINT;
	private static final EditorUsage dpsEditorUsage = EditorUsage.PositiveReal;
	private static final String dpsDescription = "Deals constant damage over time to its targets.";
	
	public DoDPSConfiguration(){
		EditorProperty dpsProperty = new EditorProperty(dpsUsage, null, dpsEditorUsage, dpsDescription);
		this.setPropertyForName(dpsName, dpsProperty);
	}
	
	@Override
	public ImpactStrategy createStrategy(MapItem m) {
		return new DoDPS((Projectile) m);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof DoDPSConfiguration)) return false;
		DoDPSConfiguration other = (DoDPSConfiguration) obj;
		return other.getPropertyForName(dpsName).equals(this.getPropertyForName(dpsName));
	}

	public strictfp class DoDPS implements ImpactStrategy{
		
		Projectile owner;

		public DoDPS(Projectile m) {
			owner = m;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public ImpactStrategyConfiguration getConfig() {
			return DoDPSConfiguration.this;
		}

		@Override
		public void handleImpact(MapItem m) {
			if(!(m instanceof Unit)){
				throw new IllegalArgumentException("This impac strategy cannot deal damage to non-units.");
			}
			
			Unit target = (Unit) m;
			
			double timeElapsed = m.getGameState().getLastLoopTime();
			double damageMultiplier = owner.getModifier().getModifier(MapItemModifiers.damageDealt);
			double rofMultiplier = owner.getModifier().getModifier(MapItemModifiers.fireRate);
			double dps = (Double) DoDPSConfiguration.this.getPropertyForName(dpsName).getValue();
			double damageToDeal = dps * timeElapsed * damageMultiplier * rofMultiplier;
			
			double currentHealth = target.getHP();
			target.setHP(currentHealth - damageToDeal);
		}

		@Override
		public void handleImpact(Position p) {
		}
		
	}
}

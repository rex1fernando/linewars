package linewars.gamestate.mapItems.strategies.impact;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class DealTotalDurabilityDamageConfiguration extends
		ImpactStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4167573596343339054L;
	
	private static final String name = "Deal an amount of durability damage.";
	
	static{
		StrategyConfiguration.setStrategyConfigMapping(name, DealTotalDurabilityDamageConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private static final String damageName = "damage";
	private static final Usage damageUsage = Usage.NUMERIC_FLOATING_POINT;
	private static final EditorUsage damageEditorUsage = EditorUsage.PositiveReal;
	private static final String damageDescription = "The total durability damage that this strategy should deal between all of its impacts.";
	private EditorProperty damageProperty = new EditorProperty(damageUsage, null, damageEditorUsage, damageDescription);

	public DealTotalDurabilityDamageConfiguration(){
		this.setPropertyForName(damageName, damageProperty);
	}
	
	@Override
	public ImpactStrategy createStrategy(MapItem m) {
		return new DealTotalDurabilityDamage(m);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof DealTotalDurabilityDamageConfiguration)) return false;
		DealTotalDurabilityDamageConfiguration other = (DealTotalDurabilityDamageConfiguration) obj;
		return this.getPropertyForName(damageName).equals(other.getPropertyForName(name));
	}

	public class DealTotalDurabilityDamage implements ImpactStrategy{
		
		private Projectile owner;
		private double durabilityRemaining;

		public DealTotalDurabilityDamage(MapItem m){
			if(!(m instanceof Projectile)){
				throw new IllegalArgumentException("Only a Projectile may have this impact strategy!");
			}
			
			owner = (Projectile) m;
			durabilityRemaining = (Double) DealTotalDurabilityDamageConfiguration.this.getPropertyForName(damageName).getValue();
		}
		
		@Override
		public String name() {
			return name;
		}

		@Override
		public ImpactStrategyConfiguration getConfig() {
			return DealTotalDurabilityDamageConfiguration.this;
		}

		@Override
		public void handleImpact(MapItem m) {
			if(m == null || !(m instanceof Projectile)) return;
			Projectile target = (Projectile) m;
			
			double currentDurability = target.getDurability();
			
			target.setDurability(currentDurability - durabilityRemaining);
			durabilityRemaining -= currentDurability;
			
			if(durabilityRemaining <= 0){
				owner.setState(MapItemState.Dead);
			}
			
			//TODO spawn a projectile here to get the explosion effect?
		}

		@Override
		public void handleImpact(Position p) {
			//Nothing to do here!
		}
		
	}
}

package linewars.gamestate.mapItems.strategies.impact;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import utility.Observable;
import utility.Observer;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public strictfp class DealDurabilityDamageOnceConfiguration extends
		ImpactStrategyConfiguration implements Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9077168737278007204L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Deal Durability Damage Once",
				DealDurabilityDamageOnceConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private double damage;
	
	public strictfp class DealDurabilityDamageOnce implements ImpactStrategy
	{
		
		private Projectile projectile;
		
		private DealDurabilityDamageOnce(Projectile p)
		{
			projectile = p;
		}

		@Override
		public String name() {
			return "Deal " + damage + " to durability of a projectile on impact";
		}

		@Override
		public ImpactStrategyConfiguration getConfig() {
			return DealDurabilityDamageOnceConfiguration.this;
		}

		@Override
		public void handleImpact(MapItem m) {
			if(m instanceof Projectile)
			{
				Projectile p = (Projectile) m;
				p.setDurability(p.getDurability() - damage);
			}
			projectile.setState(MapItemState.Dead);
		}

		@Override
		public void handleImpact(Position p) {
			projectile.setState(MapItemState.Dead);
		}
		
	}
	
	public DealDurabilityDamageOnceConfiguration()
	{
		super.setPropertyForName("damage", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, null, EditorUsage.PositiveReal,
				"The damage delt to durabilty on impact"));
		this.addObserver(this);
	}

	@Override
	public ImpactStrategy createStrategy(MapItem m) {
		return new DealDurabilityDamageOnce((Projectile) m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof DealDurabilityDamageOnceConfiguration) &&
				(((DealDurabilityDamageOnceConfiguration)obj).damage == damage);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == this && arg.equals("damage"))
			damage = (Double)this.getPropertyForName("damage").getValue();
	}

}

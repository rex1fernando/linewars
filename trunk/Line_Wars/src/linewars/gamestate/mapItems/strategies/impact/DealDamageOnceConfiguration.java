package linewars.gamestate.mapItems.strategies.impact;

import java.util.Observable;
import java.util.Observer;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.collision.AllEnemiesConfiguration;
import configuration.Usage;
import editor.abilitiesstrategies.*;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines an impact strategy. This strategy
 * impacts the first thing it hits, deals damage, and 
 * then stops.
 */
public strictfp class DealDamageOnceConfiguration extends ImpactStrategyConfiguration implements Observer {
	
	static {
		StrategyConfiguration.setStrategyConfigMapping("Deal Damage Once",
				DealDamageOnceConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private double damage;
	
	public class DealDamageOnce implements ImpactStrategy
	{
	
		private boolean hit = false;
		private Projectile projectile = null;
		
		private DealDamageOnce(Projectile p) 
		{
			projectile = p;
		}
	
		@Override
		public void handleImpact(MapItem m) {
			if(!hit)	
			{
				hit = true;
				projectile.setState(MapItemState.Dead);
				projectile.setPosition(m.getPosition());
				if(m instanceof Unit)
				{
					Unit u = (Unit)m;
					u.setHP(u.getHP() - damage);
				}
			}
		}
	
		@Override
		public void handleImpact(Position p) {
			if(!hit)
			{
				hit = true;
				projectile.setState(MapItemState.Dead);
			}
		}

		@Override
		public String name() {
			return "Deal damage once";
		}

		@Override
		public ImpactStrategyConfiguration getConfig() {
			return DealDamageOnceConfiguration.this;
		}
	}
	
	public DealDamageOnceConfiguration()
	{
		super.setPropertyForName("damage", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, null, EditorUsage.PositiveReal, "The damage delt on impact"));
		this.addObserver(this);
	}

	@Override
	public ImpactStrategy createStrategy(MapItem m) {
		if(!(m instanceof Projectile))
			throw new IllegalArgumentException("Only projectiles may have impact strategies");
		
		return new DealDamageOnce((Projectile)m);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DealDamageOnceConfiguration)
			return ((DealDamageOnceConfiguration)obj).damage == damage;
		else
			return false;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 == this && arg1.equals("damage"))
		{
			damage = (Double)this.getPropertyForName("damage").getValue();
		}
	}

}

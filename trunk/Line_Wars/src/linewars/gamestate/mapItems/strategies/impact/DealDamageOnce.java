package linewars.gamestate.mapItems.strategies.impact;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;

/**
 * 
 * @author cschenck
 *
 * This class defines an impact strategy. This strategy
 * impacts the first thing it hits, deals damage, and 
 * then stops.
 */
public class DealDamageOnce implements ImpactStrategy {
	
	private boolean hit = false;
	private double damage;
	private Projectile projectile = null;
	
	public DealDamageOnce(double damage) 
	{
		this.damage = damage;
	}
	
	public DealDamageOnce(double damage, Projectile p)
	{
		this(damage);
		projectile = p;
	}

	@Override
	public ImpactStrategy createInstanceOf(MapItem m) {
		if(m instanceof Projectile)
			return new DealDamageOnce(damage, (Projectile)m);
		else
			throw new IllegalArgumentException("Impact Strategies may only be owned by Projectiles.");
	}

	@Override
	public void handleImpact(MapItem m) {
		if(!hit)	
		{
			hit = true;
//			projectile.setState(MapItemState.Dead);
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

}

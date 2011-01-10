package linewars.gamestate.mapItems.strategies.turret;

import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

import configuration.Usage;

import editor.abilities.EditorProperty;
import editor.abilities.EditorUsage;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Turret;
import linewars.gamestate.mapItems.TurretDefinition;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.abilities.ShootDefinition;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines a combat strategy that finds the closest
 * unit, moves within range of it, and shoots. Any unit using
 * this strategy must have the ability shoot.
 */
public strictfp class ShootClosestTargetConfiguration extends TurretStrategyConfiguration implements Observer {
	
	private long shootCoolDown;
	
	public class ShootClosestTarget implements TurretStrategy
	{		
		private Turret turret = null;
		private ShootDefinition shootDefinition;
		
		private long lastShootTime = 0;
		
		private ShootClosestTarget(Turret t) 
		{
			turret = t;
			for(AbilityDefinition ad : t.getAvailableAbilities())
			{
				if(ad instanceof ShootDefinition)
				{
					shootDefinition = (ShootDefinition) ad;
					break;
				}
			}
			if(shootDefinition == null)
				throw new IllegalArgumentException(t.getName() + " does not have the ability to shoot");
		}
	
		@Override
		public double getRange() {
			return shootDefinition.getRange();
		}
	
		@Override
		public void fight(Unit[] availableTargets) {
			if(availableTargets.length == 0)
				throw new IllegalArgumentException("Why are you asking me to fight when there is no one to fight?");
			//first get the closest target
			double dis = turret.getPosition().distanceSquared(availableTargets[0].getPosition());
			Unit closest = availableTargets[0];
			for(Unit u : availableTargets)
			{
				double nd = turret.getPosition().distanceSquared(u.getPosition());
				if(nd < dis && u.getState() != MapItemState.Dead)
				{
					dis = nd;
					closest = u;
				}
			}
			
			//if the target is in range, turn to face it
			if(Math.sqrt(dis) <= shootDefinition.getRange())
			{
				long currentTime = (long) (turret.getGameState().getTime()*1000);
				if(currentTime - lastShootTime < shootCoolDown)
					return;
				//now calculate the angle the unit needs to face to shoot the target
				Position p = closest.getPosition().subtract(turret.getPosition());
				double angle = Math.atan2(p.getY(), p.getX());
				//if we're already facing the correct angle (or close enough) then FIRE!!!!!!
				//(used cosine and sine so that it doesn't matter the exact value of the rotation)
				if(Math.abs(Math.cos(angle) - Math.cos(turret.getRotation())) + 
						Math.abs(Math.sin(angle) - Math.sin(turret.getRotation())) < 0.01)
				{
					turret.addActiveAbility(shootDefinition.createAbility(turret));
					lastShootTime = currentTime;
				}
				else //face that way
				{
					turret.setRotation(angle);
				}
			}
		}

		@Override
		public String name() {
			return "Shoot the closest target";
		}

		@Override
		public TurretStrategyConfiguration getConfig() {
			return ShootClosestTargetConfiguration.this;
		}
	}
	
	public ShootClosestTargetConfiguration()
	{
		this.setPropertyForName("shootCoolDown", new EditorProperty(Usage.NUMERIC_INTEGER, null, EditorUsage.NaturalNumber, "The cool down for shooting"));
		this.addObserver(this);
	}

	@Override
	public TurretStrategy createStrategy(MapItem m) {
		if(!(m instanceof Turret))
			throw new IllegalArgumentException("Only turrets may have turret strategies");
		
		return new ShootClosestTarget((Turret)m);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ShootClosestTargetConfiguration)
			return shootCoolDown == ((ShootClosestTargetConfiguration)obj).shootCoolDown;
		else
			return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == this && arg.equals("shootCoolDown"))
		{
			shootCoolDown = (Long)this.getPropertyForName("shootCoolDown").getValue();
		}
	}

}

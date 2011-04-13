package linewars.gamestate.mapItems.strategies.turret;

import linewars.display.DisplayConfiguration;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Turret;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.abilities.ShootDefinition;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import utility.Observable;
import utility.Observer;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines a combat strategy that finds the closest
 * unit, moves within range of it, and shoots. Any unit using
 * this strategy must have the ability shoot.
 */
public strictfp class ShootClosestTargetConfiguration extends TurretStrategyConfiguration implements Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5195933536096324821L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Shoot Closest Target",
				ShootClosestTargetConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private long shootCoolDown;
	
	public class ShootClosestTarget implements TurretStrategy
	{		
		private Turret turret = null;
		private ShootDefinition shootDefinition;
		private Unit target = null;
		
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
			
			//TODO this is a hack
			DisplayConfiguration dc = (DisplayConfiguration) turret.getDefinition().getDisplayConfiguration();
			double time = 0;
			for(int i = 0; i < dc.getAnimation(MapItemState.Firing).getNumImages(); i++)
				time += dc.getAnimation(MapItemState.Firing).getImageTime(i);
			final double firingTime = time;
			
			t.addActiveAbility(new Ability() {
				
				@Override
				public void update() {
					if(turret.getGameState().getTime()*1000 - lastShootTime > firingTime)
						turret.setState(MapItemState.Idle);
				}
				
				@Override
				public boolean killable() {
					return true;
				}
				
				@Override
				public boolean finished() {
					return false;
				}
			});
		}
	
		@Override
		public double getRange() {
			return shootDefinition.getRange();
		}
	
		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			if(availableEnemies.length == 0)
				throw new IllegalArgumentException("Why are you asking me to fight when there is no one to fight?");
			
			//do i need a new target?
			if(target == null || target.getState().equals(MapItemState.Dead))
				target = acquireTarget(availableEnemies);
			
			if(target == null) //there are no valid targets
				return;
			
			double dis = target.getPosition().distanceSquared(turret.getPosition());
			
			//if the target is in range, turn to face it
			if(Math.sqrt(dis) <= shootDefinition.getRange())
			{
				long currentTime = (long) (turret.getGameState().getTime()*1000);
				if(currentTime - lastShootTime < shootCoolDown/turret.getModifier().getModifier(MapItemModifiers.fireRate))
				{
					return;
				}
				//now calculate the angle the unit needs to face to shoot the target
				Position p = target.getPosition().subtract(turret.getPosition());
				double angle = Math.atan2(p.getY(), p.getX());
				//if we're already facing the correct angle (or close enough) then FIRE!!!!!!
				//(used cosine and sine so that it doesn't matter the exact value of the rotation)
				if(Math.abs(Math.cos(angle) - Math.cos(turret.getRotation())) + 
						Math.abs(Math.sin(angle) - Math.sin(turret.getRotation())) < 0.01)
				{
					turret.addActiveAbility(shootDefinition.createAbility(turret));
					turret.setState(MapItemState.Firing);
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
		
		private Unit acquireTarget(Unit[] availableEnemies)
		{
			Unit ret = null;
			double dis = Double.POSITIVE_INFINITY;
			int marks = Integer.MAX_VALUE;
			for(Unit u : availableEnemies)
			{
				int count = 0;
				for(Ability a : u.getActiveAbilities())
				{
					if(a instanceof MarkTarget)
						++count;
				}
				if(count < marks)
				{
					ret = u;
					marks = count;
					dis = turret.getPosition().distanceSquared(ret.getPosition());
				}
				else if(count == marks)
				{
					double d = turret.getPosition().distanceSquared(u.getPosition());
					if(d < dis)
					{
						dis = d;
						ret = u;
					}
				}
			}
			ret.addActiveAbility(new MarkTarget(ret));
			return ret;
		}
		
		private class MarkTarget implements Ability
		{
			private Unit unit;
			
			private MarkTarget(Unit u)
			{
				unit = u;
			}

			@Override
			public void update() {}

			@Override
			public boolean killable() {
				return true;
			}

			@Override
			public boolean finished() {
				return unit != target || 
				ShootClosestTarget.this.turret.getState().equals(MapItemState.Dead);
			}
			
		}
	}
	
	public ShootClosestTargetConfiguration()
	{
		this.setPropertyForName("shootCoolDown", new EditorProperty(Usage.NUMERIC_INTEGER, null, EditorUsage.NaturalNumber, "The cool down for shooting (ms)"));
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
			shootCoolDown = (long)(int)(Integer)this.getPropertyForName("shootCoolDown").getValue();
		}
	}

}

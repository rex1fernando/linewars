package linewars.gamestate.mapItems.strategies.combat;

import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

public class AssaultDroneConfiguration extends CombatStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5384043049638143171L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Assault Drone",
				AssaultDroneConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class AssaultDrone extends GeneralDroneCombatStrategy
	{

		private double lastShootTime = 0;
		
		protected AssaultDrone(Unit u) {
			super(u);
		}

		@Override
		public boolean isFinishedOnTarget() {
			return (this.getTarget() == null) ||
					(this.getTarget().getState().equals(MapItemState.Dead));
		}

		@Override
		public Unit pickBestTarget(Unit[] targets) {
			double dis = Double.POSITIVE_INFINITY;
			Unit closest = null;
			for(Unit u : targets)
			{
				double d = u.getPosition().distanceSquared(this.getDrone().getPosition()); 
				if(dis > d)
				{
					dis = d;
					closest = u;
				}
			}
			return closest;
		}

		@Override
		public String name() {
			return "Assault Drone Combat Strategy";
		}

		@Override
		public CombatStrategyConfiguration getConfig() {
			return AssaultDroneConfiguration.this;
		}

		@Override
		protected void applyEffect(Unit target) {
			//check to see if its time to shoot
			if(target.getGameState().getTime() - lastShootTime > getCooldown()
					/this.getDrone().getModifier().getModifier(MapItemModifiers.fireRate))
			{
				lastShootTime = target.getGameState().getTime();
				double angle = target.getPosition().subtract(this.getDrone().getPosition()).getAngle();
				Projectile p = getProjectileToShoot().createMapItem(new Transformation(this.getDrone().getPosition(), angle),
								this.getDrone().getOwner(), target.getGameState());
				p.getModifier().pushUnderStack(this.getDrone().getModifier());
				target.getWave().getLane().addProjectile(p);
			}
		}
		
	}
	
	public AssaultDroneConfiguration()
	{
		super.setPropertyForName("projectile", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.ProjectileConfig, "The projectile to shoot"));
		super.setPropertyForName("cooldown", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.PositiveReal, "The time between firing in seconds"));
	}
	
	public ProjectileDefinition getProjectileToShoot()
	{
		return (ProjectileDefinition)super.getPropertyForName("projectile").getValue();
	}
	
	public double getCooldown()
	{
		return (Double)super.getPropertyForName("cooldown").getValue();
	}

	@Override
	public CombatStrategy createStrategy(MapItem m) {
		return new AssaultDrone((Unit) m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AssaultDroneConfiguration) &&
				((AssaultDroneConfiguration) obj).getCooldown() == getCooldown() &&
				((AssaultDroneConfiguration) obj).getProjectileToShoot().equals(getProjectileToShoot());
	}

}

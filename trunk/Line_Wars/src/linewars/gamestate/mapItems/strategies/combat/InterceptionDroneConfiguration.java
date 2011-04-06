package linewars.gamestate.mapItems.strategies.combat;

import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemModifier;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

public class InterceptionDroneConfiguration extends CombatStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2434189873804541835L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Interception Drone",
				InterceptionDroneConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class InterceptionDrone extends GeneralDroneCombatStrategy
	{
		private double lastShootTime;

		protected InterceptionDrone(Unit u) {
			super(u);
		}

		@Override
		public boolean isFinishedOnTarget() {
			return false;
		}

		@Override
		public Unit pickBestTarget(Unit[] targets) {
			return null;
		}

		@Override
		public String name() {
			return "Interception Drone Combat Strategy";
		}

		@Override
		public CombatStrategyConfiguration getConfig() {
			return InterceptionDroneConfiguration.this;
		}

		@Override
		protected void applyEffect(Unit target) {}
		
		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			super.fight(availableEnemies, availableAllies);
			//is it time to shoot?
			if(this.getDrone().getGameState().getTime() - lastShootTime >
				getCooldown()/this.getDrone().getModifier().getModifier(MapItemModifiers.fireRate))
			{
				//now we need to check if there is an enemy projectile in range
				Projectile proj = null;
				double dis = Double.POSITIVE_INFINITY;
				for(Projectile p : this.getDrone().getWave().getLane().getProjectiles())
				{
					double d = this.getDrone().getPosition().distanceSquared(p.getPosition());
					if(dis > d)
					{
						dis = d;
						proj = p;
					}
				}
				if(proj != null)
				{
					lastShootTime = this.getDrone().getGameState().getTime();
					double angle = proj.getPosition().subtract(this.getDrone().getPosition()).getAngle();
					Projectile spawn = getAnitProjectile().createMapItem(
							new Transformation(this.getDrone().getPosition(), angle), this.getDrone().getOwner(), 
							this.getDrone().getGameState());
					this.getDrone().getWave().getLane().addProjectile(spawn);
				}
				
			}
		}
		
	}
	
	public InterceptionDroneConfiguration()
	{
		super.setPropertyForName("antiProjectile", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.ProjectileConfig, "The anti-projectile to shoot"));
		super.setPropertyForName("cooldown", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The cool down in seconds between firing"));
	}
	
	public ProjectileDefinition getAnitProjectile()
	{
		return (ProjectileDefinition)super.getPropertyForName("antiProjectile").getValue();
	}
	
	public double getCooldown()
	{
		return (Double)super.getPropertyForName("cooldown").getValue();
	}



	@Override
	public CombatStrategy createStrategy(MapItem m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}
}

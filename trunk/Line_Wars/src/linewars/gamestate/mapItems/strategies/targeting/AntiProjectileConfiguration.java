package linewars.gamestate.mapItems.strategies.targeting;

import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import utility.AugmentedMath;
import utility.Observable;
import utility.Observer;

public class AntiProjectileConfiguration extends TargetingStrategyConfiguration implements Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6174791286606222739L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Anti Projectile",
				AntiProjectileConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private double speed;
	private double turningRadsPerSec;
	
	public class AntiProjectile implements TargetingStrategy
	{

		private Projectile projectile;
		private Projectile target;
		
		private AntiProjectile(Projectile p)
		{
			projectile = p;
		}
		
		@Override
		public String name() {
			return "Targets the closest projectile and then tries to hit it";
		}

		@Override
		public TargetingStrategyConfiguration getConfig() {
			return AntiProjectileConfiguration.this;
		}

		@Override
		public Transformation getTarget() {
			if(target == null)
			{
				double dis = Double.POSITIVE_INFINITY;
				for(Projectile p : projectile.getLane().getProjectiles())
				{
					double temp = p.getPosition().distanceSquared(projectile.getPosition()); 
					if(temp < dis)
					{
						dis = temp;
						target = p;
					}
				}
			}
			
			double desiredAngle = target.getPosition().subtract(projectile.getPosition()).getAngle();
			double maxTurn = turningRadsPerSec*projectile.getGameState().getLastLoopTime();
			double actualTurn;
			if(AugmentedMath.getAngleInPiToNegPi(desiredAngle - projectile.getRotation()) > maxTurn)
				actualTurn = maxTurn;
			else
				actualTurn = AugmentedMath.getAngleInPiToNegPi(desiredAngle - projectile.getRotation());
			
			Position actualChange = Position.getUnitVector(
					actualTurn + projectile.getRotation()).scale(
					speed * projectile.getGameState().getLastLoopTime());
			
			return new Transformation(actualChange, actualTurn);
			
		}
		
	}
	
	public AntiProjectileConfiguration()
	{
		super.setPropertyForName("speed", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The speed of the projectile per second"));
		super.setPropertyForName("turningRadsPerSec", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The number of radians this projectile can turn at per second"));
		super.addObserver(this);
	}

	@Override
	public TargetingStrategy createStrategy(MapItem m) {
		if(m instanceof Projectile)
			return new AntiProjectile((Projectile) m);
		else
			throw new IllegalArgumentException("Only projectiles may have targeting strategies");
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AntiProjectileConfiguration)
		{
			AntiProjectileConfiguration apc = (AntiProjectileConfiguration) obj;
			return apc.speed == speed &&
					apc.turningRadsPerSec == turningRadsPerSec;
		}
		else
			return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == this && arg.equals("speed"))
			speed = (double)(Double)super.getPropertyForName("speed").getValue();
		if(o == this && arg.equals("turningRadsPerSec"))
			turningRadsPerSec = (double)(Double)super.getPropertyForName("turningRadsPerSec").getValue();
	}

}

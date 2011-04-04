package linewars.gamestate.mapItems.strategies.turret;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.Turret;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategyConfiguration;
import linewars.gamestate.shapes.Shape;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class MeleeDamageConfiguration extends TurretStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3076791526314962293L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Melee Damage Around Turret",
				MeleeDamageConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class MeleeDamage implements TurretStrategy
	{
		
		private Turret turret;
		private double range = -1;
		private double lastScalingFactor = Double.NEGATIVE_INFINITY;

		private MeleeDamage(Turret t)
		{
			turret = t;
		}
		
		@Override
		public String name() {
			return "Inflicts melee damage around this turret";
		}

		@Override
		public TurretStrategyConfiguration getConfig() {
			return MeleeDamageConfiguration.this;
		}

		@Override
		public double getRange() {
			//if the scaling factor changed, the range needs to be recomputed
			if(lastScalingFactor != getScalingFactor())
			{
				lastScalingFactor = getScalingFactor();
				range = turret.getBody().scale(lastScalingFactor).boundingCircle().getRadius();
			}
			return range;
		}

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			Shape collisionBody = turret.getBody().scale(lastScalingFactor);
			
			double damageToDeal = getDamage()*turret.getGameState().getLastLoopTime()*
									turret.getModifier().getModifier(MapItemModifiers.damageDealt);
			for(Unit enemy : availableEnemies)
			{
				if(CollisionStrategyConfiguration.isAllowedToCollide(enemy, turret) &&
						enemy.getBody().isCollidingWith(collisionBody))
					enemy.setHP(enemy.getHP() - damageToDeal);
			}
		}
		
	}
	
	public MeleeDamageConfiguration()
	{
		this.setPropertyForName("damage", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, null, EditorUsage.PositiveReal,
				"The damage dealt per second"));
		this.setPropertyForName("scalingFactor", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, null, EditorUsage.PositiveReal,
				"The amount to scale this turret's body by to get the damage box"));
	}
	
	private double getDamage()
	{
		return (Double)super.getPropertyForName("damage").getValue();
	}
	
	private double getScalingFactor()
	{
		return (Double)super.getPropertyForName("scalingFactor").getValue();
	}

	@Override
	public TurretStrategy createStrategy(MapItem m) {
		return new MeleeDamage((Turret) m);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MeleeDamageConfiguration)
		{
			MeleeDamageConfiguration mdc = (MeleeDamageConfiguration) obj;
			return mdc.getDamage() == getDamage() &&
					mdc.getScalingFactor() == getScalingFactor();
		}
		else
			return false;
	}

}

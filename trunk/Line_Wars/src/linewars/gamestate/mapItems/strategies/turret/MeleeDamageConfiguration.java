package linewars.gamestate.mapItems.strategies.turret;

import java.util.List;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Turret;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategyConfiguration;
import linewars.gamestate.shapes.Rectangle;
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
		private int lastTick;
		private boolean dealtDamage;

		private MeleeDamage(Turret t)
		{
			turret = t;
			turret.addActiveAbility(new Ability() {
				@Override
				public void update() {
					//if we go out of combat, change the turret back to idle
					if(lastTick < turret.getGameState().getTimerTick())
					{
						turret.setState(MapItemState.Idle);
						dealtDamage = false;
					}
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
				range = turret.getWidth()*getScalingFactor();
			}
			if(dealtDamage)
				return Double.POSITIVE_INFINITY;
			else
				return 0.00001;
		}

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			double height = turret.getHeight()*getScalingFactor();
			Transformation tBody = new Transformation(turret.getPosition().add(
					Position.getUnitVector(turret.getRotation()).scale(turret.getHeight()/2 + height/2)), 
					turret.getRotation());
			Shape collisionBody = new Rectangle(tBody, turret.getWidth(), height);
			
			double damageToDeal = getDamage()*turret.getGameState().getLastLoopTime()*
									turret.getModifier().getModifier(MapItemModifiers.damageDealt)
									*turret.getModifier().getModifier(MapItemModifiers.fireRate);
			dealtDamage = false;
			List<Unit> units = turret.getWave().getLane().getUnitsIn(collisionBody.getAABB());
			for(Unit u : units)
			{
				if(!u.getState().equals(MapItemState.Dead) &&
						!u.getOwner().equals(turret.getOwner()) &&
						CollisionStrategyConfiguration.isAllowedToCollide(u, turret) &&
						u.getBody().isCollidingWith(collisionBody))
				{
					u.setHP(u.getHP() - damageToDeal);
					dealtDamage = true;
				}
			}
			if(dealtDamage && !turret.getState().equals(MapItemState.Firing))
				turret.setState(MapItemState.Firing);
			else if(!dealtDamage && turret.getState().equals(MapItemState.Firing))
				turret.setState(MapItemState.Idle);
			
			lastTick = turret.getGameState().getTimerTick();
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

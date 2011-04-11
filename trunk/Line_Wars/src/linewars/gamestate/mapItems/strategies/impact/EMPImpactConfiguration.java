package linewars.gamestate.mapItems.strategies.impact;

import java.util.List;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.combat.CombatStrategy;
import linewars.gamestate.mapItems.strategies.combat.CombatStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategy;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategyConfiguration;
import linewars.gamestate.shapes.AABB;
import linewars.gamestate.shapes.Circle;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class EMPImpactConfiguration extends ImpactStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5205150339618159410L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("EMP Impact",
				EMPImpactConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class EMPImpact implements ImpactStrategy
	{
		private Projectile proj;
		
		private EMPImpact(Projectile p)
		{
			proj = p;
		}

		@Override
		public String name() {
			return "EMP Impact";
		}

		@Override
		public ImpactStrategyConfiguration getConfig() {
			return EMPImpactConfiguration.this;
		}

		@Override
		public void handleImpact(MapItem m) {
			handleImpact(proj.getPosition());
		}

		@Override
		public void handleImpact(Position p) {
			double radius = getRadius();
			AABB box = new AABB(p.getX() - radius, p.getY() - radius, 
					p.getX() + radius, p.getY() + radius);
			Circle damageCircle = new Circle(new Transformation(p, 0), radius);
			List<Unit> possibles = proj.getLane().getUnitsIn(box);
			for(Unit u : possibles)
			{
				if(CollisionStrategyConfiguration.isAllowedToCollide(u, proj) &&
						damageCircle.isCollidingWith(u.getBody()))
				{
					EMPEffect ee = getEMPAbility(u);
					if(ee == null)
						u.addActiveAbility(new EMPEffect(u));
					else
						ee.startTime = u.getGameState().getTime();
				}
			}
			proj.setState(MapItemState.Dead);
		}
		
		private EMPEffect getEMPAbility(Unit u)
		{
			for(Ability a : u.getActiveAbilities())
				if(a instanceof EMPEffect)
					return (EMPEffect) a;
			
			return null;
		}
		
		private class EMPEffect implements Ability
		{
			private Unit unit;
			private MovementStrategy ms;
			private CombatStrategy cs;
			private double startTime;
			private boolean finished = false;
			
			public EMPEffect(Unit u)
			{
				unit = u;
				cs = unit.getCombatStrategy();
				ms = unit.getMovementStrategy();
				unit.setMovementStrategy(new MovementStrategy() {
					public String name() {
						return "";
					}
					public MovementStrategyConfiguration getConfig() {
						return null;
					}
					public double setTarget(Transformation t) {
						return 1;
					}
					public void notifyOfCollision(Position direction) {}
					public void move() {}
				});
				unit.setCombatStrategy(new CombatStrategy() {
					public String name() {
						return "";
					}
					public CombatStrategyConfiguration getConfig() {
						return null;
					}
					public double getRange() {
						return 1;
					}
					public void fight(Unit[] availableEnemies, Unit[] availableAllies) {}
				});
				startTime = u.getGameState().getTime();
				u.setState(MapItemState.Idle);
			}

			@Override
			public void update() {
				if(!finished && unit.getGameState().getTime() - startTime > getDuration())
				{
					finished = true;
					unit.setCombatStrategy(cs);
					unit.setMovementStrategy(ms);
				}
			}

			@Override
			public boolean killable() {
				return true;
			}

			@Override
			public boolean finished() {
				return finished;
			}
			
		}
		
	}
	
	public EMPImpactConfiguration()
	{
		super.setPropertyForName("duration", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.PositiveReal, "The duration of the emp effect in seconds"));
		super.setPropertyForName("radius", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The radius around the impact that units are affected"));
		
	}
	
	public double getDuration()
	{
		return (Double)super.getPropertyForName("duration").getValue();
	}
	
	public double getRadius()
	{
		return (Double)super.getPropertyForName("radius").getValue();
	}

	@Override
	public ImpactStrategy createStrategy(MapItem m) {
		return new EMPImpact((Projectile) m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof EMPImpactConfiguration) &&
				((EMPImpactConfiguration) obj).getDuration() == getDuration() &&
				((EMPImpactConfiguration) obj).getRadius() == getRadius();
	}

}

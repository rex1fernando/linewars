package linewars.gamestate.mapItems.strategies.movement;

import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class BomberMovementConfiguration extends MovementStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4069623136959125588L;
	
	private static final double FLYING_PUSH_APART_MODIFIER = 0.1;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Drone Movement",
				DroneMovementConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class BomberMovement implements MovementStrategy
	{
		private Unit bomber;
		private Transformation target;
		private CollisionResolutionNormal collisionResolutionStrategy = new CollisionResolutionNormal();
		
		private BomberMovement(Unit u)
		{
			bomber = u;
			collisionResolutionStrategy = new CollisionResolutionNormal();
		}

		@Override
		public String name() {
			return "Bomber Movement Strategy";
		}

		@Override
		public MovementStrategyConfiguration getConfig() {
			return BomberMovementConfiguration.this;
		}

		@Override
		public double setTarget(Transformation t) {
			double dis = t.getPosition().distanceSquared(bomber.getPosition());
			double maxDis = getMaxVelocity()*bomber.getGameState().getLastLoopTime()*
							bomber.getModifier().getModifier(MapItemModifiers.moveSpeed);
			target = t;
			return Math.min(dis/maxDis, 1.0);
		}

		@Override
		public void move() {
			// TODO add in collision resolution
			double maxDis = getMaxVelocity()*bomber.getGameState().getLastLoopTime()*
							bomber.getModifier().getModifier(MapItemModifiers.moveSpeed);
			double minDis = getMinVelocity()*bomber.getGameState().getLastLoopTime()*
							bomber.getModifier().getModifier(MapItemModifiers.moveSpeed);
			double dis = minDis;
			if(target != null)
				dis = Math.sqrt(bomber.getPosition().distanceSquared(target.getPosition()));
			if(dis < minDis)
				dis = minDis;
			else if(dis > maxDis)
				dis = maxDis;
			
			double angle = bomber.getRotation();
			if(target != null)
				target.getPosition().subtract(bomber.getPosition()).getAngle();
			
			Transformation toSet = new Transformation(bomber.getPosition().add(Position.getUnitVector(angle).scale(dis)), angle);
			if(bomber.getPosition().distanceSquared(toSet.getPosition()) <= 0.01 &&
					Math.abs(bomber.getRotation() - toSet.getRotation()) <= 0.01 &&
					target != null)
			{
				if(!bomber.getState().equals(MapItemState.Idle))
					bomber.setState(MapItemState.Idle);
				angle = target.getRotation();
			}
			else if(!bomber.getState().equals(MapItemState.Moving))
				bomber.setState(MapItemState.Moving);
			toSet = new Transformation(
					collisionResolutionStrategy.adjustTarget(
							toSet.getPosition(), bomber, getMinVelocity()*FLYING_PUSH_APART_MODIFIER),
					toSet.getRotation());
			
			bomber.setTransformation(toSet);
			
			target = null;
		}

		@Override
		public void notifyOfCollision(Position direction) {
			collisionResolutionStrategy.notifyOfCollision(direction);
		}
		
	}
	
	public BomberMovementConfiguration()
	{
		super.setPropertyForName("maxVelocity", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The maximum velocity the bomber may fly at per second"));
		super.setPropertyForName("minVelocity", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The minimum velocity the bomber must fly at per second"));
	}
	
	public double getMaxVelocity()
	{
		return (Double)super.getPropertyForName("maxVelocity").getValue();
	}
	
	public double getMinVelocity()
	{
		return (Double)super.getPropertyForName("minVelocity").getValue();
	}

	@Override
	public MovementStrategy createStrategy(MapItem m) {
		return new BomberMovement((Unit) m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof BomberMovementConfiguration) &&
				((BomberMovementConfiguration) obj).getMaxVelocity() == getMaxVelocity() &&
				((BomberMovementConfiguration) obj).getMinVelocity() == getMinVelocity();
	}

}

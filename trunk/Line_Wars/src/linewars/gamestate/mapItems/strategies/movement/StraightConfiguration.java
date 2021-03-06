package linewars.gamestate.mapItems.strategies.movement;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import utility.AugmentedMath;
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
 * This class defines a movement strategy that attempts to go in
 * a straight line to the target and stops the first time it
 * hits anything. Requires a movement speed.
 */
public strictfp class StraightConfiguration extends MovementStrategyConfiguration implements Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5498076165100056492L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Straight",
				StraightConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private double speed;
	
	public strictfp class Straight implements MovementStrategy
	{
	
		private Unit unit = null;
		private Transformation target = null;
		private CollisionResolutionNormal collisionResolutionStrategy = new CollisionResolutionNormal();
		
		private Straight(Unit u) 
		{
			unit = u;
			collisionResolutionStrategy = new CollisionResolutionNormal();
		}
	
		@Override
		public double setTarget(Transformation t) {
			target = t;
			double disSqaured = t.getPosition().distanceSquared(unit.getPosition());
			double scale = 1;
			if(disSqaured > Math.pow(speed*unit.getModifier().getModifier(MapItemModifiers.moveSpeed), 2))
			{
				Position p = t.getPosition().subtract(unit.getPosition());
				scale = unit.getModifier().getModifier(MapItemModifiers.moveSpeed)*speed/Math.sqrt(disSqaured);
				p = p.scale(scale);
				p = unit.getPosition().add(p);
				target = new Transformation(p, t.getRotation());
			}
			
			return scale;
		}
	
		@Override
		public void move() {
			
			if(target == null){
				target = new Transformation(collisionResolutionStrategy.adjustTargetFromNonmoving(unit, speed), unit.getRotation());
			}else{
				target = new Transformation(collisionResolutionStrategy.adjustTarget(target.getPosition(), unit, speed), target.getRotation());
			}
			//if we didn't really move this time, set us to no longer moving
			if(target.getPosition().distanceSquared(unit.getPosition()) <= 0.01 &&
					Math.abs(AugmentedMath.getAngleInPiToNegPi(target.getRotation() - unit.getRotation())) <= 0.01)
					unit.setStateIfInState(MapItemState.Moving, MapItemState.Idle);
			else if(unit.getState() != MapItemState.Moving)
				unit.setState(MapItemState.Moving);
			
			unit.setTransformation(target);
			target = null;
		}

		@Override
		public String name() {
			return "Straight-line Movement";
		}

		@Override
		public MovementStrategyConfiguration getConfig() {
			return StraightConfiguration.this;
		}

		@Override
		public void notifyOfCollision(Position direction) {
			collisionResolutionStrategy.notifyOfCollision(direction);
		}
	}
	
	public StraightConfiguration()
	{
		this.setPropertyForName("speed", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, null, EditorUsage.PositiveReal, "The speed at which the unit moves"));
		this.addObserver(this);
	}

	@Override
	public MovementStrategy createStrategy(MapItem m) {
		if(!(m instanceof Unit))
			throw new IllegalArgumentException("Only units are allowed to move straight");
		
		return new Straight((Unit)m);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StraightConfiguration)
			return speed == ((StraightConfiguration)obj).speed;
		else
			return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == this && arg.equals("speed"))
		{
			speed = (Double)this.getPropertyForName("speed").getValue();
		}
	}

}

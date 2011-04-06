package linewars.gamestate.mapItems.strategies.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	
	public class Straight implements MovementStrategy
	{
	
		private Unit unit = null;
		private Transformation target = null;
		private List<Position> collisionsFromLastTick;
		private boolean hadToGoBackwardsLastTick = false;
		private static final double resolveCollisionAttemptMoveFactor = 0.1;
		
		private Straight(Unit u) 
		{
			unit = u;
			collisionsFromLastTick = new ArrayList<Position>();
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
			if(target == null)
				target = unit.getTransformation();
			boolean collisions = false;
			if(collisionsFromLastTick.size() != 0){
				collisions = true;
				target = new Transformation(adjustTarget(), target.getRotation());
			}
			
			//if we are just now free of collisions
			if(!collisions && hadToGoBackwardsLastTick){
				//then we need to move slowly since we're possibly just going to jump back into collision anyways
				Position relativeTarget = target.getPosition().subtract(unit.getPosition());
				relativeTarget = relativeTarget.scale(resolveCollisionAttemptMoveFactor);
				target = new Transformation(unit.getPosition().add(relativeTarget), target.getRotation());
			}
			
			//if we didn't really move this time, set us to no longer moving
			if(target.getPosition().distanceSquared(unit.getPosition()) <= 0.01 &&
					Math.abs(AugmentedMath.getAngleInPiToNegPi(target.getRotation() - unit.getRotation())) <= 0.01)
					unit.setStateIfInState(MapItemState.Moving, MapItemState.Idle);
			else if(unit.getState() != MapItemState.Moving)
				unit.setState(MapItemState.Moving);
			
			unit.setTransformation(target);
			target = null;
			
			if(collisions == false){
				hadToGoBackwardsLastTick = false;
			}
		}

		private Position adjustTarget() {

			//this unit might be colliding with some mapitems
			//if this is the case, we probably need to change target
			Position currentTarget = target.getPosition();
			Position relativeTarget = currentTarget.subtract(unit.getPosition());
			
			Position adjustedClockwise = relativeTarget;
			Position adjustedCounterClockwise = relativeTarget;
			
			boolean modified = false;
			boolean giveUpClockwise = false;
			boolean giveUpCounterClockwise = false;
			do{
				modified = false;
				for(Position toConsider : collisionsFromLastTick){
					if(!giveUpClockwise && adjustedClockwise.dot(toConsider) > 0){//if the angle between these two vectors is acute
						//then this means we have to adjust further in a clockwise direction
						adjustedClockwise = toConsider.orthogonal();//this always returns a vector rotated clockwise from the original
						if(adjustedClockwise.dot(relativeTarget) <= 0){//if the adjusted target would take us farther away from target
							//we should just give up
							giveUpClockwise = true;
						}else{
							modified = true;
						}
					}
					if(!giveUpCounterClockwise && adjustedCounterClockwise.dot(toConsider) > 0){
						modified = true;
						adjustedCounterClockwise = toConsider.orthogonal().scale(-1);
						if(adjustedCounterClockwise.dot(relativeTarget) <= 0){
							giveUpCounterClockwise = true;
						}else{
							modified = true;
						}
					}
					if(modified){//if either of the vectors were modified
						break;//then we have to recheck their compatibility with all of the collisions
					}
				}
			}while(modified);
			
			if(!giveUpClockwise && !giveUpCounterClockwise){//if both vectors will still get us closer
				adjustedClockwise = adjustedClockwise.normalize();
				adjustedCounterClockwise = adjustedCounterClockwise.normalize();
				//if the angle between relativeTarget and adjustedClockwise is more obtuse than the angle 
				//between relativeTarget and adjustedCounterClockwise
				if(relativeTarget.dot(adjustedClockwise) < relativeTarget.dot(adjustedCounterClockwise)){
					giveUpClockwise = true;
				}else{
					giveUpCounterClockwise = true;
				}
			}
			
			if(!giveUpClockwise){
				relativeTarget = adjustedClockwise.normalize().scale(speed);
			}else if(!giveUpCounterClockwise){
				relativeTarget = adjustedCounterClockwise.normalize().scale(speed);
			}
			
			if(giveUpClockwise && giveUpCounterClockwise){
				hadToGoBackwardsLastTick = true;
				relativeTarget = relativeTarget.scale(-1 * resolveCollisionAttemptMoveFactor);
				//add a tiny bit of random noise to make infinite oscillations impossible
				Position noise = relativeTarget.orthogonal();
				double noiseFactor = new Random(unit.getGameState().getTimerTick() + unit.getID()).nextDouble();
				noiseFactor -= 0.5;
				noiseFactor = noiseFactor * relativeTarget.length() * .1;
				noise = noise.scale(noiseFactor);
				relativeTarget = relativeTarget.add(noise);
			}
			
			collisionsFromLastTick.clear();
			return relativeTarget.add(unit.getPosition());
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
			collisionsFromLastTick.add(direction);
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

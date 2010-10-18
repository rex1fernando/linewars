package linewars.gamestate.mapItems.strategies.movement;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Unit;

/**
 * 
 * @author cschenck
 *
 * This class defines a movement strategy that attempts to go in
 * a straight line to the target and stops the first time it
 * hits anything. Requires a movement speed.
 */
public class Straight implements MovementStrategy {
	
	private static final int MOVEMENT_ACCURACY = 5;
	
	private Unit unit = null;
	private Transformation target = null;
	private boolean ignoreCollision = true;
	private double speed;
	
	public Straight(double speed) 
	{
		this.speed = speed;
	}

	@Override
	public void setUnit(Unit u) {
		unit = u;
	}

	@Override
	public MovementStrategy copy() {
		Straight s = new Straight(speed);
		s.unit = unit;
		s.target = target;
		s.ignoreCollision = ignoreCollision;
		return s;
	}

	@Override
	public double setTarget(Transformation t) {
		target = t;
		double disSqaured = t.getPosition().distanceSquared(unit.getPosition());
		double scale = 1;
		if(disSqaured > speed*speed)
		{
			Position p = t.getPosition().subtract(unit.getPosition());
			scale = speed/Math.sqrt(disSqaured);
			p = p.scale(scale);
			p = unit.getPosition().add(p);
			target = new Transformation(p, t.getRotation());
		}
		
		return scale;
	}

	@Override
	public void setIgnoreCollision(boolean ignore) {
		ignoreCollision = ignore; 
	}

	@Override
	public void move(Unit[] possibleCollisions) {
		if(ignoreCollision)
			unit.setTransformation(target);
		else
		{
			//first try to move to the position
			Position upperBound = target.getPosition();
			Position lowerBound = unit.getPosition();
			Position current = upperBound;
			//repeat, each time cutting the distance between upper and lower bound in half
			for(int i = 0; i < MOVEMENT_ACCURACY && !upperBound.equals(lowerBound); i++)
			{
				boolean collided = false;
				//TODO figure out some way to see if the unit would collide
				//with any units on its way to current
				
				//if there was a collision, then that is the new upper bound
				if(collided)
					upperBound = current;
				else //if not then that is the new lower bound
					lowerBound = current;
				current = new Position((lowerBound.getX() + upperBound.getX())/2, 
						(lowerBound.getY() + upperBound.getY())/2);
			}
			
			//now do the same thing for the rotation
			double uBound = target.getRotation();
			double lBound = unit.getRotation();
			double rot = uBound;
			for(int i = 0; i < MOVEMENT_ACCURACY && Double.compare(uBound, lBound) != 0; i++)
			{
				boolean collided = false;
				//TODO figure out some way to see if the unit would collide
				//with any units on its way to the current rotation
				
				//if there was a collision, then that is the new upper bound
				if(collided)
					uBound = rot;
				else //if not then that is the new lower bound
					lBound = rot;
				rot = (uBound + lBound)/2;
			}
			
			Transformation newTrans = new Transformation(lowerBound, lBound);
			unit.setTransformation(newTrans);
		}
		
		if(unit.getState() != MapItemState.Moving)
			unit.setState(MapItemState.Moving);
	}

}

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
	
	
	private Unit unit = null;
	private Transformation target = null;
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
	public void move() {

		if(target != null)
			unit.setTransformation(target);
		if(unit.getState() != MapItemState.Moving)
			unit.setState(MapItemState.Moving);		
		target = null;
	}

}

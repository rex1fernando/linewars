package linewars.gamestate.shapes.collisionStrategies;

import linewars.gamestate.shapes.Circle;
import linewars.gamestate.shapes.LineSegment;
import linewars.gamestate.shapes.Shape;

public class LineSegmentCircleStrategy extends ShapeCollisionStrategy {

	static{
		ShapeCollisionStrategy.addStrategy(new LineSegmentCircleStrategy(), LineSegment.class, Circle.class);
	}
	
	@Override
	public boolean collides(Shape first, Shape second) {
		if(first.getClass() == LineSegment.class && second.getClass() == Circle.class){
			return collidesHelper((LineSegment) first, (Circle) second);
		}else if(first.getClass() == Circle.class && second.getClass() == LineSegment.class){
			return collidesHelper((LineSegment) second, (Circle) first);
		}
		throw new UnsupportedOperationException(getClass() + " does not support collision detection between " + first.getClass() + " and " + second.getClass() + ".");
	}

	private boolean collidesHelper(LineSegment first, Circle second){
		throw new UnsupportedOperationException("Yell at Taylor to actually implement LineSegmentCircleStrategy!");
		//return false;
	}
}

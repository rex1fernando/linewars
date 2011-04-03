package linewars.gamestate.shapes.collisionStrategies;

import linewars.gamestate.Position;
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
		if(second.positionIsInShape(first.start()) || second.positionIsInShape(first.end())){//if either endpoint is in the circle
			return true;//the shapes are colliding
		}
		//Now the only way they can be colliding is if the segment cuts all the way through the triangle
		//We can test for this by checking whether the segment intersects with the diameter orthogonal to it
		Position segmentDirectionVector = first.end().subtract(first.start());
		Position orthogonalToSegment = segmentDirectionVector.orthogonal();
		
		//we have to scale this so its length is equal to the circle's radius so we can construct the diameter
		orthogonalToSegment = orthogonalToSegment.scale(orthogonalToSegment.length()).scale(second.getRadius());
		Position circleCenter = second.position().getPosition();
		LineSegment circleDiameter = new LineSegment(circleCenter.subtract(orthogonalToSegment), circleCenter.add(orthogonalToSegment));
		
		//now we just have to fetch an object that can test these guys for collision and be done with this
		ShapeCollisionStrategy tester = ShapeCollisionStrategy.getStrategyForShapes(LineSegment.class, LineSegment.class);
		return tester.collides(first, circleDiameter);
	}
}

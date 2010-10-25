package linewars.gamestate.shapes;

import linewars.gamestate.Position;

public class RectangleCircleStrategy extends ShapeCollisionStrategy {

	static{
		ShapeCollisionStrategy.addStrategy(new RectangleCircleStrategy(), Rectangle.class, Circle.class);
	}
	
	@Override
	public boolean collides(Shape first, Shape second) {
		if(first.getClass() == Rectangle.class && second.getClass() == Circle.class){
			return collidesHelper((Rectangle) first, (Circle) second);
		}else if(first.getClass() == Circle.class && second.getClass() == Rectangle.class){
			return collidesHelper((Rectangle) second, (Circle) first);
		}else{
			throw new UnsupportedOperationException(getClass() + " does not support collision detection between " + first.getClass() + " and " + second.getClass() + ".");
		}
	}

	private boolean collidesHelper(Rectangle first, Circle second){
		//two cases where they intersect
		
		//center of circle is in the Rectangle
		if(first.positionIsInShape(second.position().getPosition())){
			return true;
		}
		
		//or one of the edges of the Rectangle intersects with the circle
		Position[] vertices = first.getVertexPositions();
		
		//for each edge
		for(int i = 0; i < vertices.length; i++){
			if(segmentCircleIntersection(vertices[i], vertices[(i + 1)%4], second)){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean segmentCircleIntersection(Position start, Position end, Circle toTest){
		Position dir = end.subtract(start);
		Position diff = toTest.position().getPosition().subtract(start);
		
		//how far, in segment-length units, from start the closest pt is
		double proportion = diff.dot(dir) / dir.dot(dir);
		
		//clamp onto segment
		if(proportion < 0) proportion = 0;
		if(proportion > 1) proportion = 1;
		
		//find actual point
		Position closestPt = start.add(dir.scale(proportion));
		
		//if it is within the circle, intersection!
		return closestPt.distanceSquared(toTest.position().getPosition()) <= toTest.getRadius() * toTest.getRadius();
	}
	
}

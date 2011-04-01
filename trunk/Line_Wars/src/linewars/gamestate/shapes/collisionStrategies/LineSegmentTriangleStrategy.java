package linewars.gamestate.shapes.collisionStrategies;

import linewars.gamestate.shapes.LineSegment;
import linewars.gamestate.shapes.Shape;
import linewars.gamestate.shapes.Triangle;

public class LineSegmentTriangleStrategy extends ShapeCollisionStrategy {
	
	static {
		//Adds this Strategy to the map of Strategies for lookup
		ShapeCollisionStrategy.addStrategy(new LineSegmentTriangleStrategy(), LineSegment.class, Triangle.class);
	}
	
	@Override
	public boolean collides(Shape first, Shape second) {
		if(first.getClass() == LineSegment.class && second.getClass() == Triangle.class){
			return collidesHelper((LineSegment) first, (Triangle) second);
		}else if(second.getClass() == LineSegment.class && first.getClass() == Triangle.class){
			return collidesHelper((LineSegment)second, (Triangle) first);
		}
		throw new UnsupportedOperationException(getClass() + " does not support collision detection between " + first.getClass() + " and " + second.getClass() + ".");
	}
	
	private boolean collidesHelper(LineSegment segment, Triangle triangle){
		//this should catch all of the cases where the line segment is entirely in the triangle
		if(triangle.positionIsInShape(segment.start())){
			return true;
		}
		
		//so at this point, we know that the segment and the triangle are colliding iff
		//the segment and one of the triangle's edges are colliding
		LineSegment[] edges = triangle.getEdges();
		ShapeCollisionStrategy strategy = ShapeCollisionStrategy.getStrategyForShapes(LineSegment.class, LineSegment.class);
		for(LineSegment currentEdge : edges){
			//if these segments are colliding, the segment and the triangle are colliding
			if(strategy.collides(segment, currentEdge)){
				return true;
			}
		}
		
		//if the segment is not entirely in the triangle,
		//and the segment is not colliding with any of the triangle's edges,
		//the segment and the triangle are not colliding
		return false;
	}
}

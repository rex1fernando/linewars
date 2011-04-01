package linewars.gamestate.shapes.collisionStrategies;

import linewars.gamestate.shapes.LineSegment;
import linewars.gamestate.shapes.Shape;
import linewars.gamestate.shapes.Triangle;

public class TriangleTriangleStrategy extends ShapeCollisionStrategy {

	static {
		//Adds this Strategy to the map of Strategies for lookup
		ShapeCollisionStrategy.addStrategy(new TriangleTriangleStrategy(), Triangle.class, Triangle.class);
	}
	
	@Override
	public boolean collides(Shape first, Shape second) {
		if(!(first instanceof Triangle) || !(second instanceof Triangle)){
			throw new IllegalArgumentException("TriangleTriangleStrategy cannot compute whether a " + first.getClass() + " and a " + second.getClass() + " can collide.");
		}
		Triangle f = (Triangle) first;
		Triangle s = (Triangle) second;
		
		//this should catch every case where one triangle is entirely inside the other
		if(f.positionIsInShape(s.position().getPosition()) || s.positionIsInShape(f.position().getPosition())){
			return true;
		}
		
		//so at this point we know that either some of their line segments are colliding or the triangles are not colliding
		LineSegment[] fSegments = f.getEdges();
		LineSegment[] sSegments = s.getEdges();
		
		for(LineSegment fSegment : fSegments){
			for(LineSegment sSegment : sSegments){
				//if these line segments collide, the triangles collide
				if(ShapeCollisionStrategy.getStrategyForShapes(LineSegment.class, LineSegment.class).collides(fSegment, sSegment)){
					return true;
				}
			}
		}

		//none of the line segments collide, and neither triangle is entirely in the other, so they do not collide
		return false;
	}

}

package linewars.gamestate.shapes.collisionStrategies;

import linewars.gamestate.shapes.Circle;
import linewars.gamestate.shapes.LineSegment;
import linewars.gamestate.shapes.Shape;
import linewars.gamestate.shapes.Triangle;

public strictfp class TriangleCircleStrategy extends ShapeCollisionStrategy {

	static{
		ShapeCollisionStrategy.addStrategy(new TriangleCircleStrategy(), Triangle.class, Circle.class);
	}

	@Override
	public boolean collides(Shape first, Shape second) {
		if(first.getClass() == Triangle.class && second.getClass() == Circle.class){
			return collidesHelper((Triangle) first, (Circle) second);
		}else if(first.getClass() == Circle.class && second.getClass() == Triangle.class){
			return collidesHelper((Triangle) second, (Circle) first);
		}else{
			throw new UnsupportedOperationException(getClass() + " does not support collision detection between " + first.getClass() + " and " + second.getClass() + ".");
		}
	}

	private boolean collidesHelper(Triangle triangle, Circle circle) {
		//if either shape is entirely inside the other, then they must be colliding
		//since both shapes are convex, this test will catch at least those cases
		if(triangle.positionIsInShape(circle.position().getPosition())){
			return true;
		}
		if(circle.positionIsInShape(triangle.position().getPosition())){
			return true;
		}
		
		//Now that we know that neither one is entirely inside the other,
		//the only way the shapes can possibly be colliding is if one of the
		//edges of the triangle is colliding with the circle
		
		ShapeCollisionStrategy tester = ShapeCollisionStrategy.getStrategyForShapes(LineSegment.class, Circle.class);
		
		for(LineSegment currentEdge : triangle.getEdges()){
			if(tester.collides(currentEdge, circle)){
				return true;
			}
		}

		//So neither shape is entirely in the other, and none of the triangle's
		//edges are intersecting with the circle. The two
		//Shapes are not colliding.
		return false;
	}
}

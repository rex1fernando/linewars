package linewars.gamestate.shapes.collisionStrategies;

import java.util.ArrayList;

import linewars.gamestate.Position;
import linewars.gamestate.shapes.LineSegment;
import linewars.gamestate.shapes.Rectangle;
import linewars.gamestate.shapes.Shape;
import linewars.gamestate.shapes.Triangle;

public class TriangleRectangleStrategy extends ShapeCollisionStrategy {

	static {
		//Adds this Strategy to the map of Strategies for lookup
		ShapeCollisionStrategy.addStrategy(new TriangleRectangleStrategy(), Triangle.class, Rectangle.class);
	}
	
	@Override
	public boolean collides(Shape first, Shape second) {
		if(first.getClass() == Rectangle.class && second.getClass() == Triangle.class){
			return collidesHelper((Triangle) second, (Rectangle) first);
		}else if(first.getClass() == Triangle.class && second.getClass() == Rectangle.class){
			return collidesHelper((Triangle)first, (Rectangle) second);
		}else{
			throw new UnsupportedOperationException(getClass() + " does not support collision detection between " + first.getClass() + " and " + second.getClass() + ".");
		}
	}

	private boolean collidesHelper(Triangle triangle, Rectangle rectangle) {
		//If either Shape is entirely inside the other, then they are colliding
		if(triangle.positionIsInShape(rectangle.position().getPosition())){
			return true;
		}
		if(rectangle.positionIsInShape(triangle.position().getPosition())){
			return true;
		}
		
		//now they are colliding iff one of the triangle's edges is colliding
		//with the rectangle.
		
		ArrayList<Position> axes = new ArrayList<Position>();
		
		//add two of the sides of the rectangle... we need only two of these
		//because the other two are parallel
		Position[] rectAxes = rectangle.getEdgeVectors();
		axes.add(rectAxes[0]);
		axes.add(rectAxes[1]);
		
		for(LineSegment currentEdge : triangle.getEdges()){
			Position edgeDirection = currentEdge.end().subtract(currentEdge.start());
			Position separatingAxis = edgeDirection.orthogonal();
			axes.add(separatingAxis);
		}
		
		
		Position[] tVertices = triangle.getVertices();
		Position[] rVertices = rectangle.getVertexPositions();
		for(Position potentialSeparatingAxis : axes){
			//if the shapes can be separated along this axis,
			//they are not colliding
			if(SeparatingAxisHelper.separatedByAxis(tVertices, rVertices, potentialSeparatingAxis)){
				return false;
			}
		}
		
		//if there is no axis along which the shapes can be separated,
		//they are colliding
		return true;
	}
}

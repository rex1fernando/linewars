package linewars.gamestate.shapes.collisionStrategies;

import linewars.gamestate.Position;
import linewars.gamestate.shapes.LineSegment;
import linewars.gamestate.shapes.Rectangle;
import linewars.gamestate.shapes.Shape;

public class LineSegmentRectangleStrategy extends ShapeCollisionStrategy {

	static {
		//Adds this Strategy to the map of Strategies for lookup
		ShapeCollisionStrategy.addStrategy(new LineSegmentRectangleStrategy(), LineSegment.class, Rectangle.class);
	}
	
	@Override
	public boolean collides(Shape first, Shape second) {
		if(first.getClass() == LineSegment.class && second.getClass() == Rectangle.class){
			return collidesHelper((LineSegment) first, (Rectangle) second);
		}else if(second.getClass() == LineSegment.class && first.getClass() == Rectangle.class){
			return collidesHelper((LineSegment)second, (Rectangle) first);
		}
		throw new UnsupportedOperationException(getClass() + " does not support collision detection between " + first.getClass() + " and " + second.getClass() + ".");
	}

	private boolean collidesHelper(LineSegment segment, Rectangle rectangle) {
		//quick, cheap test to rule out the majority of cases
		if(!segment.boundingCircle().isCollidingWith(rectangle.boundingCircle())){
			return false;
		}
		
		Position[] segmentVertices = new Position[2];
		segmentVertices[0] = segment.start();
		segmentVertices[1] = segment.end();
		
		Position[] rectangleVertices = rectangle.getVertexPositions();
		
		Position segmentAxis = segment.end().subtract(segment.start()).orthogonal();
		
		//the shapes are intersecting if they cannot be separated by any axis
		return !(SeparatingAxisHelper.separatedByAxis(segmentVertices, segmentVertices, segmentAxis)
				|| SeparatingAxisHelper.separatedByAxis(segmentVertices, rectangleVertices, rectangle.getEdgeVectors()[0])
				|| SeparatingAxisHelper.separatedByAxis(segmentVertices, rectangleVertices, rectangle.getEdgeVectors()[0].orthogonal()));
	}

}

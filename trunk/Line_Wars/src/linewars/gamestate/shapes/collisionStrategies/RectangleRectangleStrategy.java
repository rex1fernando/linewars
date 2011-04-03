package linewars.gamestate.shapes.collisionStrategies;

import linewars.gamestate.Position;
import linewars.gamestate.shapes.Rectangle;
import linewars.gamestate.shapes.Shape;

/**
 * Encapsulates an algorithm for computing whether two Rectangles collide.
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp class RectangleRectangleStrategy extends ShapeCollisionStrategy{
	static {
		//Adds this Strategy to the map of Strategies for lookup
		ShapeCollisionStrategy.addStrategy(new RectangleRectangleStrategy(), Rectangle.class, Rectangle.class);
	}

	
	@Override
	public boolean collides(Shape first, Shape second) {
		if(first.getClass() != Rectangle.class || second.getClass() != Rectangle.class){
			throw new UnsupportedOperationException(getClass() + " does not support collision detection between " + first.getClass() + " and " + second.getClass() + ".");
		}
		
		//if the bounding circles don't collide, the rectangles don't either
		//and this is much cheaper than comparing the rectangles!
		if(!first.boundingCircle().isCollidingWith(second.boundingCircle())){
			return false;
		}
		
		Rectangle f = (Rectangle) first;
		Rectangle s = (Rectangle) second;
		Position[] fVertices = f.getVertexPositions();
		Position[] sVertices = s.getVertexPositions();
				
		return !(SeparatingAxisHelper.separatedByAxis(fVertices, sVertices, f.getEdgeVectors()[0])
			|| SeparatingAxisHelper.separatedByAxis(fVertices, sVertices, s.getEdgeVectors()[0])
			|| SeparatingAxisHelper.separatedByAxis(fVertices, sVertices, f.getEdgeVectors()[0].orthogonal())
			|| SeparatingAxisHelper.separatedByAxis(fVertices, sVertices, s.getEdgeVectors()[0].orthogonal()));
	}
}

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
		
		//separating axis
		//works for all convex polygons - should we define such a Shape?  Would reduce the number of ShapeCollisionStrategies that must be implemented.
		Rectangle f = (Rectangle) first;
		Rectangle s = (Rectangle) second;
				
		return !(separatedByAxis(f, s, f.getEdgeVectors()[0])
			|| separatedByAxis(f, s, s.getEdgeVectors()[0])
			|| separatedByAxis(f, s, f.getEdgeVectors()[0].orthogonal())
			|| separatedByAxis(f, s, s.getEdgeVectors()[0].orthogonal()));
	}
	
	
	private boolean separatedByAxis(Rectangle first, Rectangle second, Position axis){
		Position[] fVertices = first.getVertexPositions();
		Position[] sVertices = second.getVertexPositions();
		
		//TODO optimize
		boolean fGreater = true;
		boolean sGreater = true;
		for(Position f : fVertices){
			for(Position s : sVertices){
				if(f.scalarProjection(axis) > s.scalarProjection(axis)){
					sGreater = false;
				}else{
					fGreater = false;
				}
			}
		}
		return fGreater || sGreater;
	}
}

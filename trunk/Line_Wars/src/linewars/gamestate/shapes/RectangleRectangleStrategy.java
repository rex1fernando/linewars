package linewars.gamestate.shapes;

import linewars.gamestate.Position;

public strictfp class RectangleRectangleStrategy extends ShapeCollisionStrategy{
	static {
		ShapeCollisionStrategy.addStrategy(new RectangleRectangleStrategy(), Rectangle.class, Rectangle.class);
	}

	//TODO test
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
		return separatedByAxis(f, s, f.position().getPosition())
			|| separatedByAxis(f, s, s.position().getPosition())
			|| separatedByAxis(f, s, f.position().getPosition().orthogonal())
			|| separatedByAxis(f, s, s.position().getPosition().orthogonal());
	}
	
	//TODO document
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

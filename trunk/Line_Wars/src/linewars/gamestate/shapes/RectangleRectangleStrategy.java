package linewars.gamestate.shapes;

public class RectangleRectangleStrategy extends ShapeCollisionStrategy{
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
		
		//TODO if the bounding circles do collide, test the rectangles directly
		//separating axis
		//works for all convex polygons - should we define such a Shape?  Would reduce the number of ShapeCollisionStrategies that must be implemented.
		return true;
	}
}

package linewars.gamestate.shapes;

/**
 * Encapsulates an algorithm for computing whether two Circles are colliding.
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp class CircleCircleStrategy extends ShapeCollisionStrategy {

	static {
		//Adds this Strategy to the map of Strategies for lookup
		ShapeCollisionStrategy.addStrategy(new CircleCircleStrategy(), Circle.class, Circle.class);
	}
	
	
	@Override
	public boolean collides(Shape first, Shape second) {
		if(first.getClass() != Circle.class || second.getClass() != Circle.class){
			throw new UnsupportedOperationException(getClass() + " does not support collision detection between " + first.getClass() + " and " + second.getClass() + ".");
		}
		
		Circle f = (Circle) first;
		Circle s = (Circle) second;
		double squaredDistance = f.position().getPosition().distanceSquared(s.position().getPosition());
		return squaredDistance < (f.getRadius() + s.getRadius()) * (f.getRadius() + s.getRadius());
	}

}

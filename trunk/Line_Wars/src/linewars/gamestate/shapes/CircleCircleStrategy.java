package linewars.gamestate.shapes;

/**
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp class CircleCircleStrategy extends ShapeCollisionStrategy {

	static {
		ShapeCollisionStrategy.addStrategy(new CircleCircleStrategy(), Circle.class, Circle.class);
	}
	
	//TODO test
	@Override
	public boolean collides(Shape first, Shape second) {
		if(first.getClass() != Circle.class || second.getClass() != Circle.class){
			throw new UnsupportedOperationException(getClass() + " does not support collision detection between " + first.getClass() + " and " + second.getClass() + ".");
		}
		
		Circle f = (Circle) first;
		Circle s = (Circle) second;
		double squaredDistance = f.position().getPosition().distanceSquared(s.position().getPosition());
		return squaredDistance < f.getRadius() * f.getRadius() + s.getRadius() * s.getRadius();
	}

}

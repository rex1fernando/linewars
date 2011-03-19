package linewars.gamestate.shapes;

/**
 * Encapsulates an algorithm for determining whether an aggregate of Shapes collides with another aribtrary shape.
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp class AggregateAnythingStrategy extends ShapeCollisionStrategy {
	
	static {
		//adds this Strategy to the map for lookup
		ShapeCollisionStrategy.addStrategy(new AggregateAnythingStrategy(), ShapeAggregate.class, ShapeAggregate.class);
	}

	@Override
	public boolean collides(Shape first, Shape second) {
		if(first.getClass() == ShapeAggregate.class){
			return collidesAg((ShapeAggregate) first, second);
		} else if(second.getClass() == ShapeAggregate.class){
			return collidesAg((ShapeAggregate) second, first);
		} else {
			throw new UnsupportedOperationException(getClass() + " does not support collision detection between " + first.getClass() + " and " + second.getClass() + ".");
		}
	}
	
	//helper for cleaner code
	private boolean collidesAg(ShapeAggregate first, Shape second) {
		for(int i = 0; i < first.getMembers().length; i++){
			ShapeCollisionStrategy detector = ShapeCollisionStrategy.getStrategyForShapes(first.getMembers()[i].getClass(), second.getClass());
			if(detector.collides(first.getMembers()[i], second)){
				return true;
			}
		}
		return false;
	}
}

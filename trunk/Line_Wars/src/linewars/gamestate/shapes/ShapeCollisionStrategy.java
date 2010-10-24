package linewars.gamestate.shapes;

import java.util.HashMap;

public abstract class ShapeCollisionStrategy {
	
	protected static HashMap<Class<? extends Shape>, HashMap<Class<? extends Shape>, ShapeCollisionStrategy>> definedStrategies;
	{
		definedStrategies = new HashMap<Class<? extends Shape>, HashMap<Class<? extends Shape>, ShapeCollisionStrategy>>();
	}
	
	/**
	 * Returns a ShapeCollisionStrategy that can compute whether the two given shapes are colliding.
	 * @param first
	 * One Class<? extends Shape> which is to be tested for collision.
	 * @param second
	 * One Class<? extends Shape> which is to be tested for collision.
	 * @return A ShapeCollisionStrategy which can compute whether two given Shapes of the specified types can collide.
	 */
	public static final ShapeCollisionStrategy getStrategyForShapes(Class<? extends Shape> first, Class<? extends Shape> second){
		HashMap<Class<? extends Shape>, ShapeCollisionStrategy> subMap = definedStrategies.get(first);
		//No HashMap in that spot means collision is not supported
		if(subMap == null) throw new UnsupportedOperationException("Collision detection between " + first + " and " + second + " is not supported.");
		ShapeCollisionStrategy detector = subMap.get(second);
		//No ShapeCollision in that spot also means collision is not supported
		if(detector == null) throw new UnsupportedOperationException("Collision detection between " + first + " and " + second + " is not supported.");
		return detector;
	}
	
	public abstract boolean collides(Shape first, Shape second);
}

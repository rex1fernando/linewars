package linewars.gamestate.shapes.collisionStrategies;

import java.util.HashMap;

import linewars.gamestate.shapes.Shape;
import linewars.gamestate.shapes.ShapeAggregate;

import utility.ForceLoadPackage;

/**
 * 
 * @author Taylor Bergquist
 * 
 * This class encapsulates an algorithm for computing whether two given shapes intersect.  
 *
 */
public strictfp abstract class ShapeCollisionStrategy {
	
	//a map that allows runtime lookup of Strategies for detecting collisions
	private static HashMap<Class<? extends Shape>, HashMap<Class<? extends Shape>, ShapeCollisionStrategy>> definedStrategies;
	
	static{
		ForceLoadPackage.forceLoadClassesInSamePackage(ShapeCollisionStrategy.class);
	}	
	
	/**
	 * Call this method to add your Strategy to the pool of usable strategies
	 */
	protected static void addStrategy(ShapeCollisionStrategy toAdd, Class<? extends Shape> first, Class<? extends Shape> second){
		//instantiate if this hasn't been (singleton)
		if(definedStrategies == null){
			definedStrategies = new HashMap<Class<? extends Shape>, HashMap<Class<? extends Shape>, ShapeCollisionStrategy>>();
		}
		
		//add strat to map both ways (symetrically)
		addStrategyHelper(toAdd, first, second);
		addStrategyHelper(toAdd, second, first);
		
		//add entry for agg-anything strat to first and second symmetrically
		addStrategyHelper(new AggregateAnythingStrategy(), first, ShapeAggregate.class);
		addStrategyHelper(new AggregateAnythingStrategy(), ShapeAggregate.class, first);
		addStrategyHelper(new AggregateAnythingStrategy(), second, ShapeAggregate.class);
		addStrategyHelper(new AggregateAnythingStrategy(), ShapeAggregate.class, second);
	}
	

	private static void addStrategyHelper(ShapeCollisionStrategy toAdd, Class<? extends Shape> first, Class<? extends Shape> second){
		if(definedStrategies.get(first) == null){
			definedStrategies.put(first, new HashMap<Class<? extends Shape>, ShapeCollisionStrategy>());
		}
		HashMap<Class<? extends Shape>, ShapeCollisionStrategy> subMap = definedStrategies.get(first);
		if(subMap.get(second) == null){
			subMap.put(second, toAdd);
		}
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
	
	/**
	 * Computes whether the given Shapes are colliding.  Two Shapes are defined to be colliding if the area of their intersection is nonzero.
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public abstract boolean collides(Shape first, Shape second);
}

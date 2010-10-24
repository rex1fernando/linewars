package linewars.gamestate.shapes;

import java.util.ArrayList;

import linewars.gamestate.Transformation;
import linewars.parser.Parser;

public abstract class Shape {
	
	/**
	 * Computes whether this Shape and the given Shape intersect.  Two Shapes are defined to intersect if the area of their intersection is nonzero.
	 * @param other
	 * The Shape with which this Shape may be colliding.
	 * @return
	 */
	public final boolean isCollidingWith(Shape other){
		//get correct ShapeCollisionStrategy
		ShapeCollisionStrategy detector = ShapeCollisionStrategy.getStrategyForShapes(this.getClass(), other.getClass());
		//compute the collision
		return detector.collides(this, other);
	}
	
	/**
	 * Returns a new Shape object which contains all the space occupied by this Shape as it moves from its current location to the new location
	 * specified by the given Transformation.
	 * @param change
	 * Defines the difference between the current and final positions, such that adding this Transformation to the current position 
	 * computes the new position.
	 * @return
	 */
	public abstract Shape stretch(Transformation change);
	
	/**
	 * Returns a new Shape which is a transformed instance of this shape.
	 * @param change
	 * Defines the difference between the current and final positions, such that adding this Transformation to the current position 
	 * computes the new position.
	 * @return
	 */
	public abstract Shape transform(Transformation change);
	
	/**
	 * Returns a Transformation which defines the current position of this Shape.
	 * @return
	 */
	public abstract Transformation position();

	public static Shape buildFromParser(Parser parser) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//TODO document
	public abstract Circle boundingCircle();
	//TODO document
	public abstract Rectangle boundingRectangle();
}

package linewars.gamestate.shapes;

import java.io.Serializable;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.collisionStrategies.ShapeCollisionStrategy;

/**
 * Represents an area in 2D space.
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp abstract class Shape implements Serializable {
	
	private AABB aabb = null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5336545043378845718L;

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
	
	/**
	 * Returns a Circle which bounds the Shape, such that anything which intersects the Shape also
	 * intersects the Circle and any point which is contained in the Shape is also
	 * contained in the Circle.
	 * 
	 * May not compute the smallest possible bounding circle.
	 * 
	 * @return A Circle bounding the object.
	 */
	public abstract Circle boundingCircle();
	
	/**
	 * Returns a Rectangle which bounds the Shape, such that anything which intersects the Shape also
	 * intersects the Rectangle and any point which is contained in the Shape is also
	 * contained in the Rectangle.
	 * 
	 * May not compute the smallest possible bounding rectangle.
	 * 
	 * @return A Rectangle bounding the object.
	 */
	public abstract Rectangle boundingRectangle();
	
	/**
	 * Computes whether a given position is contained in the Shape.
	 * The result of this method may be undefined for Positions
	 * exactly on the boundary of the Shape.
	 * 
	 * @param toTest The position to be tested.
	 * @return true if the Position is contained within the Shape, false otherwise.
	 */
	public abstract boolean positionIsInShape(Position toTest);
	
	public abstract AABB calculateAABB();
	
	public abstract Shape scale(double scaleFactor);
	
	public AABB getAABB()
	{
		if (aabb == null) aabb = calculateAABB();
		
		return aabb;
	}

}

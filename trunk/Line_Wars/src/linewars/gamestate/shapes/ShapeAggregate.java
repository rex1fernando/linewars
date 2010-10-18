package linewars.gamestate.shapes;

import linewars.gamestate.Transformation;
import linewars.parser.Parser;

public class ShapeAggregate {
	
	//TODO takes in a parser and creates itself
	public ShapeAggregate(Parser p){}
	
	/**
	 * This method takes in a transformation that this shape aggregate is at, another
	 * shape aggregate, and the transformation that shape aggregate is at, and calculates
	 * whether or not the two are colliding.
	 * 
	 * @param t			the transformation this shape aggregate is at
	 * @param other		the other shape aggregate
	 * @param tOther	the transformation the other shape aggregate is at
	 * @return			true if they are colliding, false otherwise
	 */
	public boolean isCollidingWith(Transformation t, ShapeAggregate other, Transformation tOther) 
	{
		return false;
	}
	
	/**
	 * This method takes in the current rotation of this shape aggregate and the change in
	 * position and rotation and creates a new shape aggregate that that covers this shape
	 * aggregate moving from where it is to the new transformation and all places in between,
	 * centered on where this shape aggregate is centered.
	 * 
	 * @param rotation	the current rotation of the shape aggregate
	 * @param change	how much the shape aggregate is changing by
	 * @return			a new shape aggregate that is stretched from here to change
	 */
	public ShapeAggregate stretch(double rotation, Transformation change)
	{
		return null;
	}
	
	/**
	 * 
	 * @return	the maximum width of this shape aggregate
	 */
	public int getWidth()
	{
		return 0;
	}
	
	/**
	 * 
	 * @return	the maximum height of this shape aggregate
	 */
	public int getHeight()
	{
		return 0;
	}

}

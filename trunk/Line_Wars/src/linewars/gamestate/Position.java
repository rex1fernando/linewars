package linewars.gamestate;

import java.io.Serializable;
import java.util.Scanner;

/**
 * 
 * @author Connor Schenck, Taylor Bergquist
 * 
 * This class represents a 2-dimensional vector. It is
 * immutable.
 *
 */
public strictfp class Position implements Serializable {
	
	public static final Position ORIGIN = new Position(0, 0);
	
	private double x;
	private double y;
	
	/**
	 * Creates a position with given x and y values
	 * 
	 * @param x
	 * @param y
	 */
	public Position(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a position by parsing it from the string. Find the first
	 * two doubles in the string and sets x and y to those. Ignores all
	 * other characters
	 * 
	 * @param toParse	the string to get the position from.
	 */
	public Position(String toParse)
	{
		for(int i = 0; i < toParse.length(); i++)
			if ((toParse.charAt(i) < '0' || toParse.charAt(i) > '9')
					&& toParse.charAt(i) != '.' && toParse.charAt(i) != ' ')
				toParse = toParse.replace(toParse.charAt(i), ' ');
		Scanner s = new Scanner(toParse);
		x = s.nextDouble();
		y = s.nextDouble();
	}

	/**
	 * 
	 * @return	the x value of this position
	 */
	public double getX()
	{
		return x;
	}
	
	/**
	 * 
	 * @return	the y value of this position
	 */
	public double getY()
	{
		return y;
	}
	
	/**
	 * Adds the given x and y values to this position and returns the result
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Position add(double x, double y)
	{
		return new Position(this.x + x, this.y + y);
	}
	
	/**
	 * Adds the given position to this position and retuns result
	 * 
	 * @param p
	 * @return
	 */
	public Position add(Position p)
	{
		return add(p.getX(), p.getY());
	}
	
	/**
	 * Subtracts the given x and y values from the position and returns the result
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Position subtract(double x, double y)
	{
		return add(-x, -y);
	}
	
	/**
	 * subtracts the given position from this position and returns the result
	 * 
	 * @param p
	 * @return
	 */
	public Position subtract(Position p)
	{
		return subtract(p.getX(), p.getY());
	}
	
	/**
	 * Multplies s by each of the values in this position and returns the result
	 * 
	 * @param s
	 * @return
	 */
	public Position scale(double s)
	{
		return new Position(x*s, y*s);
	}
	
	/**
	 * Returns the distance squared between this position and p
	 * 
	 * @param p
	 * @return
	 */
	public double distanceSquared(Position p)
	{
		return Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2);
	}
	
	//very strict, to detect desync
	@Override
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(!(o instanceof Position)) return false;
		Position other = (Position) o;
		if(other.x != x || other.y != y) return false;
		return true;
		/*
		if(o instanceof Position)
			return Double.compare(x, ((Position)o).x) == 0 &&
				Double.compare(y, ((Position)o).y) == 0;
		else
			return false;*/
	}
	
	/**
	 * 
	 * @return	the normalized version of this position
	 */
	public Position normalize()
	{
		double length = Math.sqrt((x * x) + (y * y));
		
		if(length == 0)
			return scale(1);
		else
			return scale(1 / length);
	}

	/**
	 * 
	 * @return	a position orthogonal to this vector
	 */
	public Position orthogonal()
	{
		return new Position(y, -x);
	}
	
	/**
	 * Returns the dot product of this position with other
	 * 
	 * @param other
	 * @return
	 */
	public double dot(Position other){
		return x * other.x + y * other.y;
	}
	
	/**
	 * Returns the scaler projection of this position along the given
	 * axis.
	 * 
	 * @param axis
	 * @return
	 */
	public double scalarProjection(Position axis){
		Position axisHitler = axis.normalize();
		return this.dot(axisHitler);
	}
	
	/**
	 * returns the vector projection of this position along the
	 * given axis
	 * 
	 * @param axis
	 * @return
	 */
	public Position vectorProjection(Position axis){
		Position axisHitler = axis.normalize();
		return axisHitler.scale(scalarProjection(axis));
	}
	
	public double getAngle()
	{
		return Math.atan2(getY(), getX());
	}
	
	public Position rotateAboutPosition(Position p, double rot)
	{
		Position temp = this.subtract(p);
		double angle = temp.getAngle() + rot;
		return p.add(Position.getUnitVector(angle).scale(Math.sqrt(temp.dot(temp))));
	}
	
	@Override
	public int hashCode()
	{
		return (int) (x + 31 * y);
	}
	
	@Override
	public String toString(){
		return "(" + (int)x + ", " + (int)y + ")";
	}

	/**
	 * Computes the length of this Position (if it is considered to be a vector/relative position).
	 * This is equal to its distance from the origin, if the Position is interpreted as an absolute position.
	 * @return the length of the vector represented by this Position.
	 */
	public double length() {
		return Math.sqrt(x * x + y * y);
	}
	
	/**
	 * Returns a unit vector in the direction of theta
	 * 
	 * @param theta
	 * @return
	 */
	static public Position getUnitVector(double theta)
	{
		return new Position(Math.cos(theta), Math.sin(theta));
	}

	public double crossProduct(Position other) {
		return this.x * other.y - this.y * other.x;
	}
}

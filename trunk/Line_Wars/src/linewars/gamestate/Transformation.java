package linewars.gamestate;


/**
 * 
 * @author Connor Schenck
 * 
 * This class represents a transformation, or a position
 * and a direction. It is immutable.
 *
 */
public strictfp class Transformation {
	
	public static final Transformation ORIGIN = new Transformation(Position.ORIGIN, 0);
	
	private Position pos;
	//in radians
	private double rotation;
	
	/**
	 * Constructs a transformation with p as its position and
	 * rot as its direction
	 * 
	 * @param p
	 * @param rot
	 */
	public Transformation(Position p, double rot)
	{
		pos = p;
		rotation = rot;
	}
	
	/**
	 * 
	 * @return	the position of this transformation
	 */
	public Position getPosition()
	{
		return pos;
	}
	
	/**
	 * 
	 * @return	the rotation of this transformation
	 */
	public double getRotation()
	{
		return rotation;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(!(o instanceof Transformation)) return false;
		Transformation other = (Transformation) o;
		if(other.rotation != rotation) return false;
		if(!other.pos.equals(pos)) return false;
		return true;
		/*
		if(o instanceof Transformation)
		{
			return pos.equals(((Transformation)o)) && 
				Double.compare(rotation, ((Transformation)o).rotation) == 0;
		}
		else
			return false;*/
	}
	
	@Override
	public String toString(){
		return pos.toString() + " @ " + rotation + " radians";
	}

}

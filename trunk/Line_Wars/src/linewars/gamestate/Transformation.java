package linewars.gamestate;

import linewars.parser.*;

public class Transformation {
	
	private Position pos;
	//in radians
	private double rotation;
	
	public Transformation(Position p, double rot)
	{
		pos = p;
		rotation = rot;
	}
	
	public Transformation(Parser p)
	{
		pos = new Position(p.getNumericValue(ParserKeys.x), p.getNumericValue(ParserKeys.y));
		rotation = p.getNumericValue(ParserKeys.rotation);
	}
	
	public Position getPosition()
	{
		return pos;
	}
	
	public double getRotation()
	{
		return rotation;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Transformation)
		{
			return pos.equals(((Transformation)o)) && 
				Double.compare(rotation, ((Transformation)o).rotation) == 0;
		}
		else
			return false;
	}

}

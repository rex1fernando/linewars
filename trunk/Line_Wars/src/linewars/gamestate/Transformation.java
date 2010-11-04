package linewars.gamestate;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

public class Transformation {
	
	private Position pos;
	//in radians
	private double rotation;
	
	public Transformation(Position p, double rot)
	{
		pos = p;
		rotation = rot;
	}
	
	public Transformation(ConfigData configData)
	{
		pos = new Position(configData.getNumber(ParserKeys.x), configData.getNumber(ParserKeys.y));
		rotation = configData.getNumber(ParserKeys.rotation);
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
	
	@Override
	public String toString(){
		return pos.toString() + " @ " + rotation + " radians";
	}

}

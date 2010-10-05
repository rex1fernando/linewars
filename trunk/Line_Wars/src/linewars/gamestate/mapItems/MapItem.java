package linewars.gamestate.mapItems;

import linewars.gamestate.ConfigFileParser;
import linewars.gamestate.Position;

public abstract class MapItem {
	
	//the position of this map item in map coordinates
	private Position pos;
	
	//the rotation of this map item where 0 radians is facing directly right
	private double rotation;
	
	//the state of the map item
	private MapItemState state;
	//the time at which the map item entered its state
	private long stateStart;
	
	//a reference to the class that defines the parameters of this class
	MapItemDefinition definition;
	
	//TODO add state variable for collision dection
	
	
	public MapItem(Position p, double rot, MapItemDefinition def)
	{
		definition = def;
		pos = p;
		rotation = rot;
		state = MapItemState.Idle;
		stateStart = System.currentTimeMillis();
	}
	
	public Position getPosition()
	{
		return pos;
	}
	
	public double getRotation()
	{
		return rotation;
	}
	
	public void setPosition(Position p)
	{
		pos = p;
	}
	
	public void setRotation(double rot)
	{
		rotation = rot;
	}
	
	public MapItemState getState()
	{
		return state;
	}
	
	public void setState(MapItemState m)
	{
		if(definition.isValidState(m))
		{
			state = m;
			stateStart = System.currentTimeMillis();
		}
		else
			throw new IllegalStateException(m.toString() + " is not a valid state for a " + definition.getName());
	}
	
	public ConfigFileParser getParser()
	{
		return definition.getParser();
	}
	
	//TODO
	public boolean isCollidingWith(MapItem m)
	{
		return false;
	}

}

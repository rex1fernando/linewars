package linewars.gamestate.mapItems;

import java.util.ArrayList;

import linewars.gamestate.ConfigFileParser;
import linewars.gamestate.Position;

public abstract class MapItem {
	
	//the position of this map item in map coordinates
	protected Position pos;
	
	//the rotation of this map item where 0 radians is facing directly right
	protected double rotation;
	
	//the state of the map item
	private MapItemState state;
	//the time at which the map item entered its state
	private long stateStart;
	
	//the owner of this map item
	protected Player owner;
	
	//all the current abilities active on this map item
	private ArrayList<Ability> activeAbilities;
	
	//TODO add state variable for collision dection
	
	
	public MapItem(Position p, double rot, Player owner)
	{
		pos = p;
		rotation = rot;
		state = MapItemState.Idle;
		stateStart = System.currentTimeMillis();
		this.owner = owner;
		
		AbilityDefinition[] ads = this.getDefinition().getAbilityDefinitions();
		for(AbilityDefinition ad : ads)
		{
			if(ad.startsActive())
				this.addActiveAbility(ad);
		}
	}
	
	protected abstract MapItemDefinition getDefinition();
	
	public abstract void update();
	
	public void addActiveAbility(AbilityDefinition ad)
	{
		activeAbilities.add(ad.createAbility(this));
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
		if(this.getDefinition().isValidState(m))
		{
			state = m;
			stateStart = System.currentTimeMillis();
		}
		else
			throw new IllegalStateException(m.toString() + " is not a valid state for a " + this.getDefinition().getName());
	}
	
	public ConfigFileParser getParser()
	{
		return this.getDefinition().getParser();
	}
	
	//TODO
	public boolean isCollidingWith(MapItem m)
	{
		return false;
	}

}

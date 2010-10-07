package linewars.gamestate.mapItems;

import java.util.ArrayList;

import linewars.gamestate.ConfigFileParser;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.strategies.CollisionStrategy;

public abstract class MapItem {
	
	//the position of this map item in map coordinates.
	//THIS IS THE CENTER OF THE MAP ITEM
	protected Position pos;
	
	// the dimensions of the map item
	protected double width, height;
	
	//the rotation of this map item where 0 radians is facing directly right
	protected double rotation;
	
	//the state of the map item
	private MapItemState state;
	//the time at which the map item entered its state
	private long stateStart;
	
	//all the current abilities active on this map item
	protected ArrayList<Ability> activeAbilities;
	
	//TODO add state variable for collision dection
	
	
	public MapItem(Position p, double rot)
	{
		pos = p;
		rotation = rot;
		state = MapItemState.Idle;
		stateStart = System.currentTimeMillis();
		
		AbilityDefinition[] ads = this.getDefinition().getAbilityDefinitions();
		for(AbilityDefinition ad : ads)
		{
			if(ad.startsActive())
				this.addActiveAbility(ad.createAbility(this));
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected abstract MapItemDefinition getDefinition();
	
	public void update()
	{
		for(int i = 0; i < activeAbilities.size(); i++)
			if(!state.equals(MapItemState.Dead) || !activeAbilities.get(i).killable())
				activeAbilities.get(i).update();
	}
	
	public void addActiveAbility(Ability a)
	{
		activeAbilities.add(a);
	}
	
	public Position getPosition()
	{
		return pos;
	}
	
	public double getRotation()
	{
		return rotation;
	}
	
	public double getWidth()
	{
		return width;
	}
	
	public double getHeight()
	{
		return height;
	}
	
	public void setPosition(Position p)
	{
		pos = p;
	}
	
	public void setWidth(double w)
	{
		width = w;
	}
	
	public void setHeight(double h)
	{
		height = h;
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
	
	public double getStateStartTime()
	{
		return stateStart;
	}
	
	public ConfigFileParser getParser()
	{
		return this.getDefinition().getParser();
	}
	
	public AbilityDefinition[] getAvailableAbilities()
	{
		return this.getDefinition().getAbilityDefinitions();
	}
	
	public Player getOwner()
	{
		return this.getDefinition().getOwner();
	}
	
	public abstract CollisionStrategy getCollisionStrategy();
	
	//TODO
	public boolean isCollidingWith(MapItem m)
	{
		if(!this.getCollisionStrategy().canCollideWith(m))
			return false;
		
		return false;
	}
	
	public String getURI()
	{
		return this.getDefinition().getParser().getURI();
	}

}

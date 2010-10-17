package linewars.gamestate.mapItems;

import java.util.ArrayList;

import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.strategies.CollisionStrategy;
import linewars.parser.Parser;

public abstract class MapItem {
	
	//the position of this map item in map coordinates
	//and the rotation of this map item where 0 radians is facing directly right
	//THIS IS THE CENTER OF THE MAP ITEM
	protected Transformation transformation;
	
	// the dimensions of the map item
	protected double width, height;
	
	//the state of the map item
	private MapItemState state;
	//the time at which the map item entered its state
	private long stateStart;
	
	//all the current abilities active on this map item
	protected ArrayList<Ability> activeAbilities;
	
	//TODO add state variable for collision dection
	
	
	public MapItem(Transformation trans)
	{
		transformation = trans;
		state = MapItemState.Idle;
		stateStart = System.currentTimeMillis();
	}
	
	protected abstract MapItemDefinition getDefinition();
	
	public void update()
	{
		for(int i = 0; i < activeAbilities.size();)
		{
			//only update this ability if the mapItem isn't dead or if the
			//ability isn't killable
			if(!state.equals(MapItemState.Dead) || !activeAbilities.get(i).killable())
				activeAbilities.get(i).update();
			
			//remove finished abilities
			if(activeAbilities.get(i).finished())
				activeAbilities.remove(i);
			else
				i++;
					
		}
	}
	
	public void addActiveAbility(Ability a)
	{
		activeAbilities.add(a);
	}
	
	public Position getPosition()
	{
		return transformation.getPosition();
	}
	
	public double getRotation()
	{
		return transformation.getRotation();
	}
	
	public Transformation getTransformation()
	{
		return transformation;
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
		transformation = new Transformation(p, transformation.getRotation());
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
		transformation = new Transformation(transformation.getPosition(), rot);
	}
	
	public void setTransformation(Transformation t)
	{
		transformation = t;
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
	
	public Parser getParser()
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
	
	public String getName()
	{
		return this.getDefinition().getName();
	}
	
	//TODO implement colliding with method
	public boolean isCollidingWith(MapItem m)
	{
		if(!this.getCollisionStrategy().canCollideWith(m))
			return false;
		
		return false;
	}
	
	public String getURI()
	{
		return this.getDefinition().getParser().getConfigFile().getURI();
	}

}

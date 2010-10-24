package linewars.gamestate.mapItems;

import java.util.ArrayList;

import linewars.display.Animation;
import linewars.gameLogic.GameTimeManager;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.shapes.Shape;
import linewars.parser.Parser;

/**
 * 
 * @author cschenck
 *
 * This class represents the basic item on the map. Any agent
 * on the map is a mapItem. This mapItem knows where it is,
 * how it's oriented, what state it's in and how long its been
 * in that state, and it knows what abilities are currently active
 * on itself.
 */
public abstract class MapItem {
	
	//the position of this map item in map coordinates
	//and the rotation of this map item where 0 radians is facing directly right
	//THIS IS THE CENTER OF THE MAP ITEM
	private Shape body;
	
	//the state of the map item
	private MapItemState state;
	//the time at which the map item entered its state
	private long stateStart;
	
	//all the current abilities active on this map item
	private ArrayList<Ability> activeAbilities;
	
	public MapItem(Transformation trans, MapItemDefinition def)
	{
		Shape b = def.getBody();
		body = def.getBody().transform(trans);
		state = MapItemState.Idle;
		stateStart = GameTimeManager.currentTimeMillis();
	}
	
	protected abstract MapItemDefinition getDefinition();
	
	/**
	 * This method updates all the map item's currently active abilities
	 * and handles any other tasks that need to be accomplished by the map
	 * item in one loop of the game thread and are not handles elsewhere.
	 */
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
	
	/**
	 * Adds the given ability to the list of active abilities for this map item
	 * 
	 * @param a	the ability to add
	 */
	public void addActiveAbility(Ability a)
	{
		activeAbilities.add(a);
	}
	
	/**
	 * 
	 * @return	the abilities currently active on this map item
	 */
	public Ability[] getActiveAbilities()
	{
		return activeAbilities.toArray(new Ability[0]);
	}
	
	/**
	 * 
	 * @return	the position of the map item
	 */
	public Position getPosition()
	{
		return body.position().getPosition();
	}
	
	/**
	 * 
	 * @return	the rotation of the map item
	 */
	public double getRotation()
	{
		return body.position().getRotation();
	}
	
	/**
	 * 
	 * @return	the transformation (position and rotation) of the map item
	 */
	public Transformation getTransformation()
	{
		return body.position();
	}
	
	/**
	 * 
	 * @param p	the position to set the map item at
	 */
	public void setPosition(Position p)
	{
		body = body.transform(new Transformation(p, body.position().getRotation()));
	}
	
	/**
	 * 
	 * @param rot	the new rotation of the map item
	 */
	public void setRotation(double rot)
	{
		body = body.transform(new Transformation(body.position().getPosition(), rot));
	}
	
	/**
	 * 
	 * @param t the new transformation of the map item
	 */
	public void setTransformation(Transformation t)
	{
		body = body.transform(t);
	}
	
	/**
	 * 
	 * @return	the current state of the map item
	 */
	public MapItemState getState()
	{
		return state;
	}
	
	/**
	 * 
	 * @param m	the state to set the map item to
	 */
	public void setState(MapItemState m)
	{
		if(this.getDefinition().isValidState(m))
		{
			state = m;
			stateStart = GameTimeManager.currentTimeMillis();
		}
		else
			throw new IllegalStateException(m.toString() + " is not a valid state for a " + this.getDefinition().getName());
	}
	
	/**
	 * 
	 * @return	the time at which the map item entered its current state
	 */
	public double getStateStartTime()
	{
		return stateStart;
	}
	
	/**
	 * 
	 * @return	the parser associated with this map item
	 */
	public Parser getParser()
	{
		return this.getDefinition().getParser();
	}
	
	/**
	 * 
	 * @return	the list of ability definitions available to this map item
	 */
	public AbilityDefinition[] getAvailableAbilities()
	{
		return this.getDefinition().getAbilityDefinitions();
	}
	
	/**
	 * 
	 * @return	the owner of this map item
	 */
	public Player getOwner()
	{
		return this.getDefinition().getOwner();
	}
	
	/**
	 * 
	 * @return	the collision strategy associated with this map item
	 */
	public abstract CollisionStrategy getCollisionStrategy();
	
	/**
	 * 
	 * @return	the name of this map item
	 */
	public String getName()
	{
		return this.getDefinition().getName();
	}
	
	/**
	 * This method takes in a map item and checks to see if the two are colliding.
	 * It first checks their collision strategies to see if the can collide, and if
	 * they can, checks to see if each of their bodies are colliding.
	 * 
	 * @param m		the map item to check collision with
	 * @return		true if this and m can collide and are colliding, false otherwise
	 */
	public boolean isCollidingWith(MapItem m)
	{
		if(!this.getCollisionStrategy().canCollideWith(m))
			return false;
		
		return this.getDefinition().getBody().isCollidingWith(m.body);
	}
	
	/**
	 * 
	 * @return	the URI associated with the parser associated with this map item
	 */
	public String getURI()
	{
		return this.getDefinition().getParser().getConfigFile().getURI();
	}

	public double getWidth() {
		return this.getDefinition().getBody().boundingRectangle().getWidth();
	}

	public double getHeight() {
		return this.getDefinition().getBody().boundingRectangle().getHeight();
	}
		
	//TODO
	/**
	 * 
	 * @return	the radius of the bounding circle of this unit
	 */
	public double getRadius() {
		return body.boundingCircle().getRadius();
	}
	
	public Shape getBody()
	{
		return body;
	}
}

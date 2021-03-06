package linewars.network.messages;

import java.io.Serializable;

import linewars.gamestate.GameState;

/**
 * Encapsulates a single command.
 * 
 * Each Message knows which Player it is associated with by their ID
 * 
 * Each Message can be serialized and deserialized to facilitate
 * transmission over the network - thus its members must be primitives
 * only.
 * 
 * Subtypes
 * 		- upgrademessage
 * 			- URI of upgrade to apply
 * 			- works the same (no special cases) for
 * 				- building
 * 				- unit
 * 				- projectile
 * 				- ability
 * 				- everything else :D
 * 
 * 		- buildmessage
 * 			- Node id
 * 			- URI of something to build
 * 			- Slot id to build it in/coordinates of where to build
 * 			  it/whatever is needed (TBD)
 * 		- adjustflowdistributionmessage
 * 			- Node id
 * 			- New flow distribution
 * 				- Ordered list of floating point numberz
 * 				- The order is important, used to map to specific Lanes in the same order
 * 		- supdawgmessage
 * 			- Simply indicates that no orders were given on this tick.
 * 			  Used so the server knows nothing was dropped.
 * 
 * @author Titus Klinge
 * 
 */
public abstract class Message implements Serializable
{
	private static final long serialVersionUID = 690292317101282722L;
	
	private int playerID;
	private int timeStep;
	
	public Message(int pID)
	{
		playerID = pID;
	}
	
	public int getPlayerId()
	{
		return playerID;
	}
	
	public int getTimeStep()
	{
		return timeStep;
	}
	
	public void setTimeStep(int step)
	{
		timeStep = step;
	}
	
	public abstract void apply(GameState gameState);
	
}

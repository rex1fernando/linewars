package linewars.gamestate.mapItems;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.combat.NoCombatConfiguration.NoCombat;
import linewars.gamestate.mapItems.strategies.movement.ImmovableConfiguration.Immovable;

/**
 * 
 * @author , Connor Schenck
 *
 * This class represents the gate unit, a unit that sits
 * at the end of a lane and must be destroyed to enter a node.
 */
public strictfp class Gate extends Unit {
	
	/**
	 * Constructs a gate at t, with definition def, movement strategy
	 * ms, and combat strategy cs.
	 * 
	 * @param t		the transformation of this gate
	 * @param def	the map item definition that created this gate
	 * @param ms	the movement strategy for this gate
	 * @param cs	the combat strategy for this gate
	 */
	public Gate(Transformation t, GateDefinition def, Player owner, GameState gameState) {
		super(t, def, owner, gameState);
	}

	/**
	 * Creates a dummy gate at t.
	 * 
	 * @param t
	 */
	public Gate(Transformation t, GameState gameState) {
		super(t, new GateDefinition(), null, gameState);
	}
	
	@Override
	public void setTransformation(Transformation t)
	{
		
	}
	
	@Override 
	public void setRotation(double rot)
	{
		
	}
	
	public void setPosition(double pos)
	{
		
	}
	
}

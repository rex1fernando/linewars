package linewars.gamestate.mapItems;

import linewars.gamestate.Node;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.combat.CombatStrategy;
import linewars.gamestate.mapItems.strategies.combat.NoCombat;
import linewars.gamestate.mapItems.strategies.movement.Immovable;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategy;

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
	public Gate(Transformation t, GateDefinition def, MovementStrategy ms,
			CombatStrategy cs) {
		super(t, def, ms, cs);
	}

	/**
	 * Creates a dummy gate at t.
	 * 
	 * @param t
	 */
	public Gate(Transformation t) {
		super(t, null, new Immovable(), new NoCombat());
	}
	
	@Override
	public void setState(MapItemState m)
	{
		super.setState(m);
	}
	
}

package linewars.gamestate.mapItems;

import linewars.gamestate.Node;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.combat.CombatStrategy;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategy;

/**
 * 
 * @author cschenck
 *
 * This class represents the gate unit, a unit that sits
 * at the end of a lane and must be destroyed to enter a node.
 */
public strictfp class Gate extends Unit {
	
	public Gate(Transformation t, GateDefinition def, MovementStrategy ms,
			CombatStrategy cs) {
		super(t, def, ms, cs);
	}
	
}

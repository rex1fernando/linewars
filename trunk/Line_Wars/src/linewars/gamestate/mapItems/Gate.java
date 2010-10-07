package linewars.gamestate.mapItems;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.strategies.CombatStrategy;
import linewars.gamestate.mapItems.strategies.MovementStrategy;

public class Gate extends Unit {

	Gate(Position p, double rot, GateDefinition def, MovementStrategy ms,
			CombatStrategy cs) {
		super(p, rot, def, ms, cs);
	}

}

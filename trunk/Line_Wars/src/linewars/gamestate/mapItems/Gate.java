package linewars.gamestate.mapItems;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.CombatStrategy;
import linewars.gamestate.mapItems.strategies.MovementStrategy;

public class Gate extends Unit {

	Gate(Transformation t, GateDefinition def, MovementStrategy ms,
			CombatStrategy cs) {
		super(t, def, ms, cs);
	}

}

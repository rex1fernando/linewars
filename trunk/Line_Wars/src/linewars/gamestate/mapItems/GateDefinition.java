package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.combat.NoCombat;
import linewars.gamestate.mapItems.strategies.movement.Immovable;
import linewars.parser.Parser.InvalidConfigFileException;

public class GateDefinition extends UnitDefinition {

	public GateDefinition(String URI, Player owner)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
	}
	
	@Override
	public Unit createUnit(Transformation t)
	{
		return createGate(t);
	}
	
	public Gate createGate(Transformation t)
	{
		return new Gate(t, this, new Immovable(), new NoCombat());
	}

}

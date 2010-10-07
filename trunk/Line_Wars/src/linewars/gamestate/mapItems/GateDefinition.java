package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.ConfigFileParser.InvalidConfigFileException;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.strategies.Immovable;
import linewars.gamestate.mapItems.strategies.NoCombat;

public class GateDefinition extends UnitDefinition {

	public GateDefinition(String URI, Player owner)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
	}
	
	@Override
	public Unit createUnit(Position p, double rotation)
	{
		return createGate(p, rotation);
	}
	
	public Gate createGate(Position p, double rotation)
	{
		return new Gate(p, rotation, this, new Immovable(), new NoCombat());
	}

}

package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.ConfigFileParser.InvalidConfigFileException;
import linewars.gamestate.Position;

public class UnitDefinition extends MapItemDefinition<Unit> {
	
	private double maxHp;

	public UnitDefinition(String URI) throws FileNotFoundException, InvalidConfigFileException {
		super(URI);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Unit createMapItem(Position p, double rotation) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public double getMaxHP()
	{
		return maxHp;
	}

}

package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.ConfigFileParser.InvalidConfigFileException;
import linewars.gamestate.Position;

public class UnitDefinition extends MapItemDefinition {
	
	private double maxHp;
	private double maxRange;
	
	private CombatStrategy cStrat;
	private MovementStrategy mStrat;
	
	//TODO add template variables for collision

	public UnitDefinition(String URI, Player owner) throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
		
		maxHp = super.getParser().getNumericValue("maxHp");
		maxRange = super.getParser().getNumericValue("range");
		
		String cs = super.getParser().getStringValue("combatStrategy");
		//TODO convert string to combat strategy
		
		String ms = super.getParser().getStringValue("movementStrategy");
		//TODO convert string to movement strategy
		
		//TODO parse collision from the file
	}

	public Unit createUnit(Position p, double rotation) {
		
		Unit u = new Unit(p, rotation, this, mStrat.copy(), cStrat.copy());
		
		return u;
	}
	
	public double getMaxHP()
	{
		return maxHp;
	}
	
	public double getRange()
	{
		return maxRange;
	}

}

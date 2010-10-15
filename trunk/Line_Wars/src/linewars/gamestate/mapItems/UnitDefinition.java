package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.mapItems.strategies.CombatStrategy;
import linewars.gamestate.mapItems.strategies.MovementStrategy;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.parser.ParserKeys;

public class UnitDefinition extends MapItemDefinition {
	
	private double maxHp;
	private double maxRange;
	
	private CombatStrategy cStrat;
	private MovementStrategy mStrat;
	
	//TODO add template variables for collision

	public UnitDefinition(String URI, Player owner) throws FileNotFoundException {
		super(URI, owner);
		
		maxHp = super.getParser().getNumericValue(ParserKeys.maxHP);
		
//		String cs = super.getParser().getStringValue("combatStrategy");
		//TODO convert string to combat strategy
		
//		String ms = super.getParser().getStringValue("movementStrategy");
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

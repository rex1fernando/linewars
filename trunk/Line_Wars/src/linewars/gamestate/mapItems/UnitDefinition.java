package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.mapItems.strategies.combat.CombatStrategy;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategy;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.parser.Parser.InvalidConfigFileException;
import linewars.parser.ParserKeys;

/**
 * 
 * @author cschenck
 *
 * This class defines a unit. It is a type of MapItemDefinition.
 * It knows the maximum health points allowed for the units it
 * creates, what type of combat strategy to use, and what type
 * of movement strategy to use.
 */
public class UnitDefinition extends MapItemDefinition {
	
	private double maxHp;
	
	private CombatStrategy cStrat;
	private MovementStrategy mStrat;

	public UnitDefinition(String URI, Player owner) throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
		
		maxHp = super.getParser().getNumericValue(ParserKeys.maxHP);
		
//		String cs = super.getParser().getStringValue("combatStrategy");
		//TODO convert string to combat strategy
		
//		String ms = super.getParser().getStringValue("movementStrategy");
		//TODO convert string to movement strategy
		
	}

	/**
	 * Creates a unit of the type defined by this class
	 * 
	 * @param t	the tranformation of the unit to be created
	 * @return	the created unit
	 */
	public Unit createUnit(Transformation t) {
		
		Unit u = new Unit(t, this, mStrat.copy(), cStrat.copy());
		
		return u;
	}
	
	/**
	 * 
	 * @return	the maximum hp allowed for units of this type.
	 */
	public double getMaxHP()
	{
		return maxHp;
	}

}

package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.mapItems.strategies.combat.CombatStrategy;
import linewars.gamestate.mapItems.strategies.combat.NoCombat;
import linewars.gamestate.mapItems.strategies.combat.ShootClosestTarget;
import linewars.gamestate.mapItems.strategies.movement.Immovable;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategy;
import linewars.gamestate.mapItems.strategies.movement.Straight;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.parser.Parser;
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
	
	private CombatStrategy combatStrat;
	private MovementStrategy mStrat;

	public UnitDefinition(String URI, Player owner) throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
		
//		maxHp = super.getParser().getNumericValue(ParserKeys.maxHP);
//		
//		Parser cs = super.getParser().getParser(ParserKeys.combatStrategy);
//		if(cs.getStringValue(ParserKeys.type).equalsIgnoreCase("ShootClosestTarget"))
//		{
//			combatStrat = new ShootClosestTarget(this);
//		}
//		else if(cs.getStringValue(ParserKeys.type).equalsIgnoreCase("NoCombat"))
//		{
//			combatStrat = new NoCombat();
//		}
//		else
//			throw new IllegalArgumentException("Invalid combat strategy for " + this.getName());
//		
//		Parser ms = super.getParser().getParser(ParserKeys.movementStrategy);
//		if(ms.getStringValue(ParserKeys.type).equalsIgnoreCase("Straight"))
//		{
//			mStrat = new Straight(ms.getNumericValue(ParserKeys.speed));
//		}
//		else if(ms.getStringValue(ParserKeys.type).equalsIgnoreCase("immovable"))
//		{
//			mStrat = new Immovable();
//		}
//		else
//			throw new IllegalArgumentException("Invalid movement strategy for " + this.getName());
	}

	/**
	 * Creates a unit of the type defined by this class
	 * 
	 * @param t	the tranformation of the unit to be created
	 * @return	the created unit
	 */
	public Unit createUnit(Transformation t) {
		
		Unit u = new Unit(t, this, mStrat.copy(), combatStrat.copy());
		
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
	
	/**
	 * 
	 * @param maxHp	the new max hp of the unit
	 */
	public void setMaxHP(double maxHp)
	{
		this.maxHp = maxHp;
	}

}

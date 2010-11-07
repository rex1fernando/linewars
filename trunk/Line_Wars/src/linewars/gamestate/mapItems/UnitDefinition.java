package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.mapItems.strategies.combat.CombatStrategy;
import linewars.gamestate.mapItems.strategies.combat.NoCombat;
import linewars.gamestate.mapItems.strategies.combat.ShootClosestTarget;
import linewars.gamestate.mapItems.strategies.movement.Immovable;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategy;
import linewars.gamestate.mapItems.strategies.movement.Straight;
import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;

/**
 * 
 * @author cschenck
 *
 * This class defines a unit. It is a type of MapItemDefinition.
 * It knows the maximum health points allowed for the units it
 * creates, what type of combat strategy to use, and what type
 * of movement strategy to use.
 */
public strictfp class UnitDefinition extends MapItemDefinition implements upgradable{
	
	private double maxHp;
	
	private CombatStrategy combatStrat;
	private MovementStrategy mStrat;

	public UnitDefinition(String URI, Player owner, GameState gameState) throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner, gameState);
		
		maxHp = super.getParser().getNumber(ParserKeys.maxHP);
		
		ConfigData cs = super.getParser().getConfig(ParserKeys.combatStrategy);
		if(cs.getString(ParserKeys.type).equalsIgnoreCase("ShootClosestTarget"))
		{
			Double d = cs.getNumber(ParserKeys.shootCoolDown);
			double dd = d;
			combatStrat = new ShootClosestTarget(this, ((long)dd));
		}
		else if(cs.getString(ParserKeys.type).equalsIgnoreCase("NoCombat"))
		{
			combatStrat = new NoCombat();
		}
		else
			throw new IllegalArgumentException("Invalid combat strategy for " + this.getName());
		
		ConfigData ms = super.getParser().getConfig(ParserKeys.movementStrategy);
		if(ms.getString(ParserKeys.type).equalsIgnoreCase("Straight"))
		{
			mStrat = new Straight(ms.getNumber(ParserKeys.speed));
		}
		else if(ms.getString(ParserKeys.type).equalsIgnoreCase("immovable"))
		{
			mStrat = new Immovable();
		}
		else
			throw new IllegalArgumentException("Invalid movement strategy for " + this.getName());
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

	@Override
	public void forceReloadConfigData() {
		// TODO Auto-generated method stub
		
	}

}

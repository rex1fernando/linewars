package linewars.gamestate.mapItems;


import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.strategies.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.CombatStrategy;
import linewars.gamestate.mapItems.strategies.MovementStrategy;

public class Unit extends MapItem {
	
	private MovementStrategy mStrat;
	private CombatStrategy cStrat;
	
	private UnitDefinition definition;
	
	private double hp;

	Unit(Transformation t, UnitDefinition def, MovementStrategy ms, CombatStrategy cs) {
		super(t);
		definition = def;
		hp = definition.getMaxHP();
		mStrat = ms;
		mStrat.setUnit(this);
		cStrat = cs;
		cStrat.setUnit(this);
	}
	
	public double getHP()
	{
		return hp;
	}
	
	public double getMaxHP()
	{
		return definition.getMaxHP();
	}
	
	public double getRange()
	{
		return definition.getRange();
	}
	
	public CombatStrategy getCombatStrategy()
	{
		return cStrat;
	}
	
	public MovementStrategy getMovementStrategy()
	{
		return mStrat;
	}
	
	public boolean finished()
	{
		for(Ability a : activeAbilities)
			if(!a.killable())
				return false;
		return true;
	}

	@Override
	protected MapItemDefinition getDefinition() {
		return definition;
	}

	@Override
	public CollisionStrategy getCollisionStrategy() {
		return definition.getCollisionStrategy();
	}

}

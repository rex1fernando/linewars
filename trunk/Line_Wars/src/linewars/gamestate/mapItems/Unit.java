package linewars.gamestate.mapItems;


import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.combat.CombatStrategy;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategy;

public class Unit extends MapItem {
	
	private MovementStrategy mStrat;
	private CombatStrategy cStrat;
	
	private UnitDefinition definition;
	
	private double hp;
	
	private Wave currentWave = null;

	Unit(Transformation t, UnitDefinition def, MovementStrategy ms, CombatStrategy cs) {
		super(t);
		definition = def;
		hp = definition.getMaxHP();
		mStrat = ms;
		mStrat.setUnit(this);
		cStrat = cs;
		cStrat.setUnit(this);
	}
	
	public void setHP(double h)
	{
		hp = h;
		if(hp <= 0)
		{
			hp = 0;
			this.setState(MapItemState.Dead);
		}
		else if(hp > this.getMaxHP())
			hp = this.getMaxHP();
	}
	
	public double getHP()
	{
		return hp;
	}
	
	public double getMaxHP()
	{
		return definition.getMaxHP();
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
	
	public Wave getWave()
	{
		return currentWave;
	}
	
	public void setWave(Wave w)
	{
		currentWave = w;
	}

}

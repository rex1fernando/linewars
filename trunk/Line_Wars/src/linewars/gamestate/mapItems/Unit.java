package linewars.gamestate.mapItems;

import java.util.ArrayList;

import linewars.gamestate.Position;

public class Unit extends MapItem {
	
	private MovementStrategy mStrat;
	private CombatStrategy cStrat;
	
	private UnitDefinition definition;
	
	private double hp;

	Unit(Position p, double rot, UnitDefinition def, Player owner, MovementStrategy ms, CombatStrategy cs) {
		super(p, rot, owner);
		definition = def;
		hp = definition.getMaxHP();
		mStrat = ms;
		cStrat = cs;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	public double getHP()
	{
		return hp;
	}
	
	public double getMaxHP()
	{
		return definition.getMaxHP();
	}

	@Override
	protected MapItemDefinition getDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

}

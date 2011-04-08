package linewars.gamestate.mapItems.strategies.combat;

import java.util.Queue;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Turret;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import editor.abilitiesstrategies.AbilityStrategyEditor;

public class FocusOnTargetConfiguration extends CombatStrategyConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1067747175667086777L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Focus on Target",
				FocusOnTargetConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private static final long MINIMUM_PATH_WAIT_TIME = 100;
	private static final double MIN_TARGET_SWITCH_TIME = 5;
	
	public class FocusOnTarget implements CombatStrategy 
	{
		
		private Unit unit = null;
		private Unit target = null;
		private double timeOnTargetSinceLastInRange;
		
		private double minRange;
		private double maxRange = -1;
		
		
		/**
		 * Constructs a ShootClosestTarget object. The UnitDefinition parameter
		 * allows this strategy to get the reference to the ShootDefinition in
		 * the UnitDefinition.
		 * 
		 * @param ud	the UnitDefinition that owns the unit that owns this strategy.
		 */
		private FocusOnTarget(Unit u) 
		{
			unit = u;
		}
	
		@Override
		public double getRange() {
			calcRanges();
			return maxRange;
		}
		
		private void calcRanges()
		{
			minRange = Double.MAX_VALUE;
			maxRange = -1;
			for(Turret t : unit.getTurrets())
			{
				if(t.getTurretStrategy().getRange() > maxRange)
					maxRange = t.getTurretStrategy().getRange();
				if(t.getTurretStrategy().getRange() < minRange)
					minRange = t.getTurretStrategy().getRange();
			}
		}
		
		private void acquireTarget(Unit[] availableEnemies)
		{
			double dis = Double.POSITIVE_INFINITY;
			int marks = Integer.MAX_VALUE;
			target = null;
			for(Unit u : availableEnemies)
			{
				int count = 0;
				for(Ability a : u.getActiveAbilities())
				{
					if(a instanceof MarkTarget)
						++count;
				}
				if(count < marks)
				{
					target = u;
					marks = count;
					dis = unit.getPosition().distanceSquared(target.getPosition());
				}
				else if(count == marks)
				{
					double d = unit.getPosition().distanceSquared(u.getPosition());
					if(d < dis)
					{
						dis = d;
						target = u;
					}
				}
			}
			timeOnTargetSinceLastInRange = unit.getGameState().getTime();
			target.addActiveAbility(new MarkTarget(target));
		}

		@Override
		public String name() {
			return "Move to the closest target";
		}

		@Override
		public CombatStrategyConfiguration getConfig() {
			return FocusOnTargetConfiguration.this;
		}
		
		private class MarkTarget implements Ability
		{
			private Unit unit;
			
			private MarkTarget(Unit u)
			{
				unit = u;
			}

			@Override
			public void update() {}

			@Override
			public boolean killable() {
				return true;
			}

			@Override
			public boolean finished() {
				return unit != target;
			}
			
		}

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			//first tell my turrets to fight
			for(Turret t : unit.getTurrets())
				t.getTurretStrategy().fight(availableEnemies, availableAllies);
			
			//next update my ranges
			calcRanges();
			
			double timeSinceInRange = unit.getGameState().getTime() - timeOnTargetSinceLastInRange;
			//if i have no target, and i can't pick one yet, return
			if(target == null && timeSinceInRange < MIN_TARGET_SWITCH_TIME)
				return;
			
			//if i have no target, and i can pick one, do so
			if(target == null && timeSinceInRange >= MIN_TARGET_SWITCH_TIME)
				acquireTarget(availableEnemies);
			
			//if i have a dead target, ill need a new one
			if(target.getState().equals(MapItemState.Dead))
				acquireTarget(availableEnemies);
			
			double disSquared = unit.getPosition().distanceSquared(target.getPosition());
			//if i have a target, but i am not in range, move towards it
			if(disSquared > minRange*minRange)
			{
				Position pos = target.getPosition();
				double angle = pos.subtract(unit.getPosition()).getAngle();
				unit.getMovementStrategy().setTarget(new Transformation(pos, angle));
			}
			//if i have a target and i am in range, stop
			else
				timeOnTargetSinceLastInRange = unit.getGameState().getTime();
			
			//if i have a target and i havent been in range of it in a while, get a new target (don't force it)
			if(timeSinceInRange >= MIN_TARGET_SWITCH_TIME)
				acquireTarget(availableEnemies);
		}
	}

	@Override
	public CombatStrategy createStrategy(MapItem m) {
		if(!(m instanceof Unit))
			throw new IllegalArgumentException("Only units may have combat strategies");
		
		return new FocusOnTarget((Unit)m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof FocusOnTargetConfiguration);
	}

}

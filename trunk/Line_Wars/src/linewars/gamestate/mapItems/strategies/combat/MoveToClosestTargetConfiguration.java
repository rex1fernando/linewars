package linewars.gamestate.mapItems.strategies.combat;

import java.util.Queue;

import editor.abilitiesstrategies.AbilityStrategyEditor;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemAggregate;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Turret;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.collision.AllEnemiesConfiguration;

public class MoveToClosestTargetConfiguration extends CombatStrategyConfiguration {
	
	static {
		StrategyConfiguration.setStrategyConfigMapping("Move To Closest Target",
				MoveToClosestTargetConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private static final long MINIMUM_PATH_WAIT_TIME = 100;
	
	public class MoveToClosestTarget implements CombatStrategy 
	{
		
		private Unit unit = null;
		private Queue<Position> path = null;
		private double averageMove = 0;
		private Position lastPosition = null;
		private int numMoves = 0;
		
		private double minRange;
		private double maxRange = -1;
		
		//this variable specifies the last time the algorithm asked for a path
		private long pathLockout = -1;
		
		/**
		 * Constructs a ShootClosestTarget object. The UnitDefinition parameter
		 * allows this strategy to get the reference to the ShootDefinition in
		 * the UnitDefinition.
		 * 
		 * @param ud	the UnitDefinition that owns the unit that owns this strategy.
		 */
		private MoveToClosestTarget(Unit u) 
		{
			unit = u;
		}
	
		@Override
		public double getRange() {
			calcRanges();
			return maxRange;
		}
	
		@Override
		public void fight(Unit[] availableTargets) {
			if(availableTargets.length == 0)
				throw new IllegalArgumentException("Why are you asking me to fight when there is no one to fight?");
			
			//first tell the turrets to fight
			for(Turret t : unit.getTurrets())
				t.getTurretStrategy().fight(availableTargets);
			
			//first get the closest target
			double dis = unit.getPosition().distanceSquared(availableTargets[0].getPosition());
			Unit closest = availableTargets[0];
			for(Unit u : availableTargets)
			{
				double nd = unit.getPosition().distanceSquared(u.getPosition());
				if(nd < dis && u.getState() != MapItemState.Dead)
				{
					dis = nd;
					closest = u;
				}
			}
			
			calcRanges();
			
			//if the target is in range, turn to face it
			if(Math.sqrt(dis) <= minRange)
			{
				//now calculate the angle the unit needs to face to shoot the target
				Position p = closest.getPosition().subtract(unit.getPosition());
				double angle = Math.atan2(p.getY(), p.getX());
				//if we're already facing the correct angle (or close enough) then don't do anything
				//(used cosine and sine so that it doesn't matter the exact value of the rotation)
				if(Math.abs(Math.cos(angle) - Math.cos(unit.getRotation())) + 
						Math.abs(Math.sin(angle) - Math.sin(unit.getRotation())) >= 0.01)
				{
					unit.setRotation(angle);
				}
			}
			else //move in range
			{
				//if we dont have a path, get one
				if(path == null)
					updatePath(closest.getPosition());
				
				//if we haven't been moving, get a new path
				if(averageMove < 0.01)
				{
					numMoves = 0;
					updatePath(closest.getPosition());
				}
				
				if(path.isEmpty()) //can't do anything if there's no path at this point
					return;
				
				// how far did we move last time?
				if(lastPosition != null)
				{
					averageMove = (numMoves * averageMove + Math.sqrt(unit
							.getPosition().distanceSquared(lastPosition)))
							/ (numMoves + 1);
					numMoves++;
				}
				
				//if we've made it to this point in the path
				if(path.peek().equals(unit.getPosition()))
				{
					numMoves = 0;
					averageMove = Double.MAX_VALUE;
					path.poll();
					if(path.isEmpty()) //if the path is empty get a new one
						updatePath(closest.getPosition());
				}
				
				//calculate the angle from here to the next position
				Position diff = path.peek().subtract(unit.getPosition());
				double angle = Math.sin(diff.getY()/diff.getX());
				
				//now move to the next position in the path
				unit.getMovementStrategy().setTarget(new Transformation(path.peek(), angle));
				
				lastPosition = unit.getPosition();
			}
		}
		
		private void updatePath(Position target)
		{
			path = unit.getWave().getLane().findPath(unit, target, minRange);
		}
		
		private void calcRanges()
		{
			if(minRange < 0 || maxRange < 0 || MapItemAggregate.checkForContainedItemsChange(unit))
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
		}

		@Override
		public String name() {
			return "Move to the closest target";
		}

		@Override
		public CombatStrategyConfiguration getConfig() {
			return MoveToClosestTargetConfiguration.this;
		}
	}

	@Override
	public CombatStrategy createStrategy(MapItem m) {
		if(!(m instanceof Unit))
			throw new IllegalArgumentException("Only units may have combat strategies");
		
		return new MoveToClosestTarget((Unit)m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof MoveToClosestTargetConfiguration);
	}

}

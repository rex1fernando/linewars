package linewars.gamestate.mapItems.strategies.turret;

import java.util.Queue;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Turret;
import linewars.gamestate.mapItems.TurretDefinition;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.abilities.ShootDefinition;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines a combat strategy that finds the closest
 * unit, moves within range of it, and shoots. Any unit using
 * this strategy must have the ability shoot.
 */
public strictfp class ShootClosestTarget implements TurretStrategy {
	
	private static final long MINIMUM_PATH_WAIT_TIME = 100;
	
	private Turret turret = null;
	private ShootDefinition shootDefinition = null;
	private Queue<Position> path = null;
	private double averageMove = 0;
	private Position lastPosition = null;
	private int numMoves = 0;
	
	private long lastShootTime = 0;
	private long shootCoolDown;
	
	//this variable specifies the last time the algorithm asked for a path
	private long pathLockout = -1;
	
	/**
	 * Constructs a ShootClosestTarget object. The UnitDefinition parameter
	 * allows this strategy to get the reference to the ShootDefinition in
	 * the UnitDefinition.
	 * 
	 * @param ud	the UnitDefinition that owns the unit that owns this strategy.
	 */
	public ShootClosestTarget(TurretDefinition ud, long shootCoolDown) 
	{
		this.shootCoolDown = shootCoolDown;
		AbilityDefinition[] ads = ud.getAbilityDefinitions();
		for(int i = 0; i < ads.length && shootDefinition == null; i++)
			if(ads[i] instanceof ShootDefinition)
				shootDefinition = (ShootDefinition) ads[i];
		if(shootDefinition == null)
			throw new IllegalArgumentException(ud.getName() + " must have the ability to shoot to " +
					"use the Shoot Closest Target combat strategy.");
	}
	
	private ShootClosestTarget() {}

	@Override
	public void setTurret(Turret u) {
		turret = u;
	}

	@Override
	public TurretStrategy copy() {
		ShootClosestTarget sct = new ShootClosestTarget();
		sct.turret = turret;
		sct.shootDefinition = shootDefinition;
		sct.shootCoolDown = this.shootCoolDown;
		return sct;
	}

	@Override
	public double getRange() {
		return shootDefinition.getRange();
	}

	@Override
	public void fight(Unit[] availableTargets) {
		if(availableTargets.length == 0)
			throw new IllegalArgumentException("Why are you asking me to fight when there is no one to fight?");
		//first get the closest target
		double dis = turret.getPosition().distanceSquared(availableTargets[0].getPosition());
		Unit closest = availableTargets[0];
		for(Unit u : availableTargets)
		{
			double nd = turret.getPosition().distanceSquared(u.getPosition());
			if(nd < dis && u.getState() != MapItemState.Dead)
			{
				dis = nd;
				closest = u;
			}
		}
		
		//if the target is in range, turn to face it
		if(Math.sqrt(dis) <= shootDefinition.getRange())
		{
			long currentTime = (long) (turret.getDefinition().getGameState().getTime()*1000);
			if(currentTime - lastShootTime < shootCoolDown)
				return;
			//now calculate the angle the unit needs to face to shoot the target
			Position p = closest.getPosition().subtract(turret.getPosition());
			double angle = Math.atan2(p.getY(), p.getX());
			//if we're already facing the correct angle (or close enough) then FIRE!!!!!!
			//(used cosine and sine so that it doesn't matter the exact value of the rotation)
			if(Math.abs(Math.cos(angle) - Math.cos(turret.getRotation())) + 
					Math.abs(Math.sin(angle) - Math.sin(turret.getRotation())) < 0.01)
			{
				turret.addActiveAbility(shootDefinition.createAbility(turret));
				lastShootTime = currentTime;
			}
			else //face that way
			{
				turret.setRotation(angle);
			}
		}
		else //move in range
		{
//			//if we dont have a path, get one
//			if(path == null)
//				updatePath(closest.getPosition());
//			
//			//if we haven't been moving, get a new path
//			if(averageMove < 0.01)
//			{
//				numMoves = 0;
//				updatePath(closest.getPosition());
//			}
//			
//			if(path.isEmpty()) //can't do anything if there's no path at this point
//				return;
//			
//			// how far did we move last time?
//			if(lastPosition != null)
//			{
//				averageMove = (numMoves * averageMove + Math.sqrt(turret
//						.getPosition().distanceSquared(lastPosition)))
//						/ (numMoves + 1);
//				numMoves++;
//			}
//			
//			//if we've made it to this point in the path
//			if(path.peek().equals(turret.getPosition()))
//			{
//				numMoves = 0;
//				averageMove = Double.MAX_VALUE;
//				path.poll();
//				if(path.isEmpty()) //if the path is empty get a new one
//					updatePath(closest.getPosition());
//			}
//			
//			//calculate the angle from here to the next position
//			Position diff = path.peek().subtract(turret.getPosition());
//			double angle = Math.sin(diff.getY()/diff.getX());
//			
//			//now move to the next position in the path
//			turret.getMovementStrategy().setTarget(new Transformation(path.peek(), angle));
//			
//			lastPosition = turret.getPosition();
		}
	}
	
//	private void updatePath(Position target)
//	{
//			path = turret.getWave().getLane().findPath(turret, target, shootDefinition.getRange());
//	}

}
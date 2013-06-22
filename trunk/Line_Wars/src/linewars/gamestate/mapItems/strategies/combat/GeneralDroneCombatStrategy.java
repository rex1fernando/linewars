package linewars.gamestate.mapItems.strategies.combat;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Unit;

public abstract class GeneralDroneCombatStrategy implements
		DroneCombatStrategy, CombatStrategy {
	
	private static final double DEGREES_ROTATION_PER_TICK = Math.PI/10;
	
	private Unit unit;
	private Unit beingHealed = null;
	private Unit carrier;
	private int lastFightTick = 0;
	
	protected GeneralDroneCombatStrategy(Unit u)
	{
		unit = u;
	}
	
	public Unit getDrone()
	{
		return unit;
	}

	@Override
	public double getRange() {
		return carrier.getCombatStrategy().getRange();
	}

	@Override
	public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
		lastFightTick = unit.getGameState().getTimerTick();
		
		boolean hasTarget = (beingHealed != null);
		
		if(!hasTarget) //temporarily set the target to the carrier so we can orbit that
			beingHealed = this.getDroneCarrier();
		
		//move the drone
		Transformation toMove = null;
		//if we're at the orbiting range, orbit the target
		if(Math.abs(unit.getPosition().distanceSquared(beingHealed.getPosition()) - beingHealed.getRadius()) <= 0.01)
		{
			Position pos = unit.getPosition().rotateAboutPosition(beingHealed.getPosition(), DEGREES_ROTATION_PER_TICK);
			double rot = beingHealed.getPosition().subtract(pos).getAngle();
			toMove = new Transformation(pos, rot);
		}
		else //otherwise move to orbiting range
		{
			double rot = unit.getPosition().subtract(beingHealed.getPosition()).getAngle();
			Position pos = beingHealed.getPosition().add(Position.getUnitVector(rot).scale(beingHealed.getRadius()));
			rot = pos.subtract(unit.getPosition()).getAngle();
			toMove = new Transformation(pos, rot);
		}
		
		//apply the effect
		if(hasTarget)
		{
			applyEffect(beingHealed);
			unit.setState(MapItemState.Firing);
		}
		else
		{
			beingHealed = null;
			unit.setState(MapItemState.Idle);
		}
		this.getDrone().getMovementStrategy().setTarget(toMove);
	}
	
	protected abstract void applyEffect(Unit target);

	@Override
	public void setTarget(Unit target) {
		beingHealed = target;
	}
	
	public Unit getTarget()
	{
		return beingHealed;
	}

	@Override
	public void setDroneCarrier(Unit carrier) {
		this.carrier = carrier;
	}

	@Override
	public Unit getDroneCarrier() {
		return carrier;
	}

	@Override
	public int getLastFightTick() {
		return lastFightTick;
	}
	
}

package linewars.gamestate.mapItems;


import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.Wave;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.combat.CombatStrategy;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategy;

/**
 * 
 * @author , Connor Schenck
 *
 * This class represents a unit. It owns a movementStrategy and
 * a combatStrategy for the unit. It also knows how many health
 * points it has and what wave it is currently in.
 */
public strictfp class Unit extends MapItem {
	private MovementStrategy mStrat;
	private CombatStrategy cStrat;
	private CollisionStrategy colStrat;
	
	private UnitDefinition definition;
	
	private double hp;
	
	private Wave currentWave = null;
	
	private double positionOnCurve;
	private int lastTickPositionMarker = -1;

	/**
	 * Creates a unit at t, with definition def, movement strategy ms,
	 * and combat strategy cs.
	 * 
	 * @param t		the transformation this unit starts at
	 * @param def	the definition that created this unit
	 * @param ms	the movement strategy for this unit
	 * @param cs	the combat strategy for this unit
	 */
	public Unit(Transformation t, UnitDefinition def, MovementStrategy ms, CombatStrategy cs) {
		super(t, def);
		definition = def;
		hp = definition.getMaxHP();
		mStrat = ms;
		mStrat.setUnit(this);
		cStrat = cs;
		cStrat.setUnit(this);
		colStrat = def.getCollisionStrategy().createInstanceOf(this);
		
	}
	
	/**
	 * Sets the units hp. If the hp <= 0, then it sets the unit's
	 * state to dead. It also caps the hp at the max hp defined for
	 * the unit.
	 * 
	 * @param h	the hp to set the unit to.
	 */
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
	
	/**
	 * 
	 * @return	the current amount of hp of the unit
	 */
	public double getHP()
	{
		return hp;
	}
	
	/**
	 * 
	 * @return	the maximum amount of hp this unit may have
	 */
	public double getMaxHP()
	{
		return definition.getMaxHP();
	}
	
	/**
	 * 
	 * @return	the combat strategy associated with this unit
	 */
	public CombatStrategy getCombatStrategy()
	{
		return cStrat;
	}
	
	/**
	 * 
	 * @return	the movement strategy associated with this unit
	 */
	public MovementStrategy getMovementStrategy()
	{
		return mStrat;
	}
	
	@Override
	protected MapItemDefinition getDefinition() {
		return definition;
	}

	@Override
	public CollisionStrategy getCollisionStrategy() {
		return colStrat;
	}
	
	/**
	 * 
	 * @return	the wave that this unit is in
	 */
	public Wave getWave()
	{
		return currentWave;
	}
	
	/**
	 * @param w	the wave that this unit is in.
	 */
	public void setWave(Wave w)
	{
		currentWave = w;
	}
	
	/**
	 * gets the position along the curve of the lane this unit is. This positions
	 * is represented as the parameter to the bezier curve equation to calculate
	 * the coordinates of the closest point along the center line of the lane to
	 * the unit. Only calculates the position once per tick, and stores it for
	 * the entirety of the tick.
	 * 
	 * @return
	 */
	public double getPositionAlongCurve()
	{
		if(lastTickPositionMarker != definition.getGameState().getTimerTick())
		{
			lastTickPositionMarker = definition.getGameState().getTimerTick();
			positionOnCurve = currentWave.getLane().getClosestPointRatio(this.getPosition());
		}
		return positionOnCurve;
	}

	//This should be very strict; true iff the two objects are bit-identical
//	@Override
//	public boolean equals(Object o){
//		if(o == null) return false;
//		if(!(o instanceof Unit)) return false;
//		Unit other = (Unit) o;
//		if(!other.getBody().equals(getBody())) return false;
//		//TODO test other things in here
//		return true;
//	}
}

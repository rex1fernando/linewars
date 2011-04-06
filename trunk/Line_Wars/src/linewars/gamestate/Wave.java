package linewars.gamestate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import linewars.gamestate.mapItems.Gate;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.shapes.Circle;

/**
 * 
 * @author John George, Connor Schenck
 *
 * This class represents a wave of units. It knows which Lane it's in, where its center is, and what units it contains.
 */
public strictfp class Wave {
	private Lane owner;
	private Node origin;
	private ArrayList<Unit> units;
	private Wave opponent;
	
	/**
	 * Gets the lane that owns this wave
	 * 
	 * @return	the owning lane
	 */
	public Lane getLane()
	{
		return owner;
	}
	
	/**
	 * Gets the Node that this wave is walking away from.
	 * @return The node that this wave is walking away from.
	 */
	public Node getOrigin()
	{
		return origin;
	}
	
	/**
	 * Creates a Wave with a single Unit in it.
	 * @param owner
	 * 		The Lane that contains this Wave.
	 * @param u
	 * 		The Unit to start with Wave with.
	 * @param origin
	 * 		The Node that generated this Wave.
	 */
	public Wave(Lane owner, Unit u, Node origin)
	{
		this.owner = owner;
		opponent = null;
		units = new ArrayList<Unit>();
		this.addUnit(u);
		this.origin = origin;
	}
	
	/**
	 * Creates an empty Wave in the given Lane.
	 * @param owner
	 * 		The Lane that contains this Wave.
	 */
	public Wave(Lane owner)
	{
		this.owner = owner;
		units = new ArrayList<Unit>();
		opponent = null;
	}
	
	/**
	 * 
	 * @return
	 * 		An array containing all of the Units in this Wave.
	 */
	public Unit[] getUnits()
	{
		return units.toArray(new Unit[0]);
	}

	/**
	 * Adds the given unit to this wave, making sure that it's owned by the same player as the current owner of this wave.
	 * @param u The unit to be added.
	 * @return true if the unit was added and false otherwise.
	 */
	public boolean addUnit(Unit u)
	{
		//Check if the wave is empty and if it's not, make sure the unit you're trying to add belongs to the same player as the wave.
		if(!units.isEmpty() && u.getOwner().getPlayerID() != units.get(0).getOwner().getPlayerID()){
			return false;
		}
		u.setWave(this);
		return units.add(u);
	}
	
	/**
	 * Adds all of the units in u to the units in this wave.
	 * @param u The array of units to be added to this wave.
	 */
	public void addUnits(Unit[] u)
	{
		for(int i = 0; i < u.length; i++)
		{
			this.addUnit(u[i]);
		}
	}
	
	/**
	 * Gets the position of the wave within the lane.
	 * @param includeGates TODO
	 * @return a double that represents the percentage of the lane that is between the wave and p0 in the lane.
	 */
	public double getPositionToP0(boolean includeGates)
	{
		double min = 1;
		for(Unit u : units)
		{
			if(u instanceof Gate && !includeGates)
				continue;
			double d = u.getPositionAlongCurve() - u.getRadius()/owner.getLength();
			if(d < min)
				min = d;
		}
		if(min < 0)
			min = 0;
		if(min > 1)
			min = 1;
		return min;
	}
	
	/**
	 * Gets the position of the wave within the lane.
	 * @return a double that represents the percentage of the lane that is between the wave and p3 in the lane.
	 */
	public double getPositionToP3()
	{
		double max = 0;
		for(Unit u : units)
		{
			if(u instanceof Gate)
				continue;
			double d = u.getPositionAlongCurve() + u.getRadius()/owner.getLength();
			if(d > max)
				max = d;
		}
		if(max < 0)
			max = 0;
		if(max > 1)
			max = 1;
		return 1 - max;
	}
	
	
	/**
	 * Updates all of the units of this wave according to the movement and combat strategies of the units in it.
	 */
	public void update()
	{
		List<Unit> deadButNotFinished = new ArrayList<Unit>();
		//first check for dead units
		for(int i = 0; i < units.size();)
			if(units.get(i).getState() == MapItemState.Dead && units.get(i).finished()) {
				units.remove(i);
				owner.notifySweepAndPruneStructuresNeedUpdate();
			} else if(units.get(i).getState().equals(MapItemState.Dead)) {
				deadButNotFinished.add(units.remove(i));
			} else {
				i++;
			}
		
		//don't do anything if there are no units
		if(units.size() <= 0)
			return;
		

		//if(!(units.get(0) instanceof Gate))
			//System.out.println("Unit position == " + units.get(0).getPosition().getX() + ", " + units.get(0).getPosition().getY());
		
		//first get the max radius
		double maxRad = 0;
		Position center = this.getCenter();
		for(Unit u : units)
		{
			double rad = Math.sqrt(center.distanceSquared(u.getPosition())) + u.getCombatStrategy().getRange();
			if(rad > maxRad)
				maxRad = rad;
		}
		
		//this is to prevent units from doing a run by on accident
		if(maxRad < owner.getWidth())
			maxRad = owner.getWidth();
		
		List<Unit> unitsInRange = owner.getUnitsIn(new Circle(new Transformation(center, 0), maxRad));
		List<Unit> alliesInRange = new ArrayList<Unit>();
		//remove friendly units
		for(int i = 0; i < unitsInRange.size();)
			if(unitsInRange.get(i).getOwner().equals(units.get(0).getOwner()))
				alliesInRange.add(unitsInRange.remove(i));
			else
				i++;
		
		//if there are units, we're in combat!
		if(unitsInRange.size() > 0)
		{
			//for efficiency reasons
			Unit[] unitsInRangeArray = unitsInRange.toArray(new Unit[unitsInRange.size()]);
			Unit[] alliesInRangeArray = alliesInRange.toArray(new Unit[alliesInRange.size()]);
			for(Unit u : units)
				u.getCombatStrategy().fight(unitsInRangeArray, alliesInRangeArray);
		}
		else
		{
			//figure out which direction we're going
			boolean forward = true;
			if (origin.getTransformation().getPosition().distanceSquared(
					owner.getPosition(1).getPosition()) < origin.getTransformation().getPosition()
					.distanceSquared(owner.getPosition(0).getPosition()))
				forward = false;
			
			//we're gonna move straight forward because I said so -Connor
			double wayTheFuckOutThere = 100000000;
			double min = 1;
			
			//for efficiency reasons
			HashMap<Unit, Transformation> closestPoints = new HashMap<Unit, Transformation>();
			
			//go through each unit and see how far it's going to go
			for(int i = 0; i < units.size();)
			{
				Unit u = units.get(i);
				double pos = u.getPositionAlongCurve();
				Node target = owner.getNodes()[0];
				if(target.equals(origin))
					target = owner.getNodes()[1];
				//check to see if we've made it to the node we're going for
				//also, don't allow entering the node if the gate is still up
				if(((forward && Math.abs(1 - pos) <= 0.01) || (!forward && Math.abs(pos) <= 0.01)) && 
						(owner.getGate(target) == null || owner.getGate(target).getState().equals(MapItemState.Dead) 
								|| u.getOwner().equals(owner.getGate(target).getOwner())))
				{
					if(target.getOwner() == null || target.isContested() || !target.getOwner().equals(u.getOwner()))
						target.setInvader(u.getOwner());
					u.setTransformation(target.getTransformation());
					target.addUnit(u);
					units.remove(i);
					owner.notifySweepAndPruneStructuresNeedUpdate();
					continue;
				}
				//if we're close enough but the gate isn't down
				else if((forward && Math.abs(1 - pos) <= 0.01) || (!forward && Math.abs(pos) <= 0.01))
				{
					Transformation t = owner.getPosition(pos);
					closestPoints.put(u, t);
					u.setState(MapItemState.Idle);
					u.getMovementStrategy().setTarget(u.getTransformation());
					i++;
					continue;
				}
				else
					i++;
				
				Transformation t = owner.getPosition(pos);
//				if(Math.abs(t.getRotation()) < .001){
//					System.out.println("t's rotation is 0");
//				}
				closestPoints.put(u, t);
				double angle = t.getRotation();
				if(!forward)
					angle -= Math.PI;
				double m = u.getMovementStrategy().setTarget(
						new Transformation(u.getPosition().add(
								wayTheFuckOutThere * Math.cos(angle),
								wayTheFuckOutThere * Math.sin(angle)), angle));
				if(m < min)
					min = m;
			}
			
			//go through each unit and set its actual target
			double dis = wayTheFuckOutThere*min;
			for(int i = 0; i < units.size(); i++)
			{
				double angle = closestPoints.get(units.get(i)).getRotation();
				if(!forward)
					angle -= Math.PI;
//				Transformation t = owner.getPosition(owner.getClosestPointRatio(closestPoints.get(i)
//								.getPosition().add(dis * Math.cos(angle),dis * Math.sin(angle))));                                                                                                                                                                                                                                                                                                                                                                                                           
				Transformation t = new Transformation(units.get(i).getPosition().add(dis*Math.cos(angle), dis*Math.sin(angle)), angle);
				units.get(i).getMovementStrategy().setTarget(t);
			}
		}
		
		for(Unit u : units)
		{
			u.getMovementStrategy().move();
			u.updateMapItem();
		}
		
		//add the dead but not finished units back in
		//also call update for them so they may finish their abilities
		for(Unit u : deadButNotFinished)
		{
			u.updateMapItem();
			units.add(u);
		}
	}
	

	/**
	 * Helper method for the update() method. Sets the targets for all the units in this wave based on the scale and returns the lowest move.
	 * @param destGate The gate the units are using as their target.
	 * @param gatePos The position of the target gate.
	 * @param scale The modifier to the movement of the units.
	 * @return The lowest move speed found.
	 */
	private double setTransformations(Gate destGate, Position gatePos, double scale) {
		double ret = 1;
		for(int i = 0; i < units.size(); i++)
		{
			Unit u = units.get(i);
			
			double destX = 0;
			Position currentPos = u.getPosition();
			double plus = currentPos.getX() - (gatePos.getX()+(destGate.getWidth()/2));
			double minus = currentPos.getX() - (gatePos.getX()-(destGate.getWidth()/2));
			if(Math.abs(plus) < Math.abs(minus))
			{
				destX = plus;
			}else{
				destX = minus;
			}
			
			Position destPos = new Position((scale * destX), currentPos.getY());
			Transformation target = new Transformation(destPos, u.getTransformation().getRotation());
			double currentSpeed = u.getMovementStrategy().setTarget(target);
			if(currentSpeed < ret)
			{
				ret = currentSpeed;
			}
		}
		return ret;
	}
	
	/**
	 * Checks whether or not this Wave contains a given Unit.
	 * @param u
	 * 		The Unit to check for.
	 * @return
	 * 		True if u is in this Wave, false otherwise.
	 */
	public boolean contains(Unit u)
	{
		return units.contains(u);
	}
	
	
	/**
	 * Removes the specified Unit from this Wave
	 * @param u
	 * 		The Unit to be removed.
	 */
	public void remove(Unit u)
	{
		units.remove(u);
	}
	
	/**
	 * 
	 * @return
	 * 		The Position of the center of this Wave.
	 */
	public Position getCenter()
	{
		Position average = new Position(0,0);
		int num = 0;
		for(Unit u : units)
		{
			average = average.scale(num).add(u.getPosition()).scale(1.0/(num + 1.0));
			num++;
		}
		return average;
	}
	
	public boolean equals(Object o){
		if(o == null) return false;
		if(!(o instanceof Wave)) return false;
		Wave other = (Wave) o;
		if(!other.units.equals(units)) return false;
		return true;
	}
}

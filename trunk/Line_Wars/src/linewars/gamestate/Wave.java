package linewars.gamestate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import linewars.gamestate.mapItems.Gate;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.shapes.Circle;
public class Wave {
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
	
	public Wave(Lane owner, Unit u, Node origin)
	{
		this.owner = owner;
		opponent = null;
		units = new ArrayList<Unit>();
		units.add(u);
		this.origin = origin;
	}
	
	public Wave(Lane owner)
	{
		this.owner = owner;
		units = new ArrayList<Unit>();
		opponent = null;
	}
	
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
	 * @return a double that represents the percentage of the lane that is between the wave and p0 in the lane.
	 */
	public double getPosition()
	{
		double min = 1;
		for(Unit u : units)
		{
			double d = owner.getClosestPointRatio(u.getPosition()) - u.getRadius()/owner.getLength();
			if(d < min)
				min = d;
		}
		return min;
	}
	
	/**
	 * Fixes all of the current collisions in this wave.
	 */
	private void fixCollisions()
	{
		HashMap<Unit, Position> collisionVectors = new HashMap<Unit, Position>();
		
		for(int i = 0; i < units.size(); i++)
		{
			Unit currentUnit = units.get(i);
			if(currentUnit.getState() == MapItemState.Moving)
			{
				Position collisionVector;
				MapItem[] collisions = owner.getCollisions(currentUnit);
				Position totalVector = new Position(0,0);
				for(int j = 0; j < collisions.length; j++)
				{
					double d = Math.sqrt(currentUnit.getPosition().distanceSquared(collisions[j].getPosition()));
					double dPrime = currentUnit.getRadius() + collisions[j].getRadius();
					double deltaD = dPrime - d;
					if(collisions[j].getState() == MapItemState.Moving)
					{
						collisionVector = currentUnit.getPosition().subtract(collisions[j].getPosition());
						collisionVector = collisionVector.scale(deltaD/2);
					}else{
						collisionVector = currentUnit.getPosition().subtract(collisions[j].getPosition());
						collisionVector = collisionVector.scale(deltaD);
					}
					totalVector.add(collisionVector);
				}
				collisionVectors.put(currentUnit, totalVector);
			}else{
				collisionVectors.put(currentUnit, currentUnit.getPosition());
			}
		}
		
		for(int i = 0; i < units.size(); i++)
		{
			if(units.get(i).getState() == MapItemState.Moving){
				if(collisionVectors.get(units.get(i)).distanceSquared(new Position(0, 0)) > 0)
					units.get(i).setPosition(collisionVectors.get(units.get(i)));
			}
		}
	}
	
	
	/**
	 * Updates all of the units of this wave according to the movement and combat strategies of the units in it.
	 */
	public void update()
	{
		//first check for dead units
		for(int i = 0; i < units.size();)
			if(units.get(i).getState() == MapItemState.Dead)
				units.remove(i);
			else
				i++;
		
		//don't do anything if there are no units
		if(units.size() <= 0)
			return;
		

		if(!(units.get(0) instanceof Gate))
			System.out.println("Unit position == " + units.get(0).getPosition().getX() + ", " + units.get(0).getPosition().getY());
		
		//first get the max radius
		double maxRad = 0;
		Position center = this.getCenter();
		for(Unit u : units)
		{
			double rad = Math.sqrt(center.distanceSquared(u.getPosition())) + u.getCombatStrategy().getRange();
			if(rad > maxRad)
				maxRad = rad;
		}
		
		List<Unit> unitsInRange = owner.getUnitsIn(new Circle(new Transformation(center, 0), maxRad));
		//remove friendly units
		for(int i = 0; i < unitsInRange.size();)
			if(unitsInRange.get(i).getOwner().equals(units.get(0).getOwner()))
				unitsInRange.remove(i);
			else
				i++;
		
		//if there are units, we're in combat!
		if(unitsInRange.size() > 0)
		{
			//for efficiency reasons
			Unit[] unitsInRangeArray = unitsInRange.toArray(new Unit[0]);
			for(Unit u : units)
				u.getCombatStrategy().fight(unitsInRangeArray);
		}
		else
		{
			//figure out which direction we're going
			int dir = 1;
			if (origin.getPosition().getPosition().distanceSquared(
					owner.getPosition(1).getPosition()) < origin.getPosition().getPosition()
					.distanceSquared(owner.getPosition(0).getPosition()))
				dir = -1;
			
			//we're gonna move straight forward because I said so -Connor
			double wayTheFuckOutThere = 100000000;
			double min = 1;
			
			//for efficiency reasons
			ArrayList<Transformation> closestPoints = new ArrayList<Transformation>();
			
			//go through each unit and see how far it's going to go
			for(Unit u : units)
			{
				Transformation t = owner.getPosition(owner.getClosestPointRatio(u.getPosition()));
				closestPoints.add(t);
				double angle = t.getRotation();
				if(dir < 0)
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
				double angle = closestPoints.get(i).getRotation();
				if(dir < 0)
					angle -= Math.PI;
				Transformation t = owner.getPosition(owner.getClosestPointRatio(closestPoints.get(i).getPosition().add(dis*Math.cos(angle), dis*Math.sin(angle))));
				units.get(i).getMovementStrategy().setTarget(t);
			}
		}
		
		for(Unit u : units)
			u.getMovementStrategy().move();
		
		fixCollisions();
		
//		Gate destGate = null;
//		for(int i = 0; i < owner.getNodes().length; i++)
//		{
//			if(owner.getNodes()[i] != origin){
//				destGate = owner.getGate(owner.getNodes()[i]);
//			}
//		}
//		double lowestMove = 1;
//		if(destGate == null){
//			return;//TODO haxed together a NPE fix here, prob still a logical error here
//		}
//		Position gatePos = destGate.getPosition();
//		if(opponent == null)
//		{
//			lowestMove = setTransformations(destGate, gatePos, 1);
//			setTransformations(destGate, gatePos, lowestMove);
//		}else{
//			Unit[] enemies = opponent.getUnits();
//			for(int i = 0; i < units.size(); i++)
//			{
//				units.get(i).getCombatStrategy().fight(enemies);
//			}
//		}
//		for(int j = 0; j < units.size(); j++)
//		{
//			units.get(j).getMovementStrategy().move();
//			units.get(j).update();
//		}
//		
//		fixCollisions();
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
	
	public boolean contains(Unit u)
	{
		return units.contains(u);
	}
	
	public void remove(Unit u)
	{
		units.remove(u);
	}
	
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
}

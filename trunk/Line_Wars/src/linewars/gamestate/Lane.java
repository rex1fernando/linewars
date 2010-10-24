package linewars.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;


import linewars.gamestate.Position;
import linewars.gamestate.mapItems.Gate;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Node;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.Wave;

public class Lane
{
	/*
	 * Feel free to change these names. These points represent the 4 control
	 * points in a bezier curve, with p0 and p3 being the end points.
	 * 
	 * -Ryan Tew
	 */
	private Position p0;
	private Position p1;
	private Position p2;
	private Position p3;
		
	private GameState gameState;
	private HashMap<Node, ArrayList<Wave>> pendingWaves;
	private ArrayList<Wave> waves;
	private Gate[] gates;
	private ArrayList<Node> nodes;
	
	/**
	 * The width of the lane.
	 */
	private double width;
	
	private PathFinding pathFinder = new PathFinding();
	
	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	
	public Lane(Position p0, Position p1, Position p2, Position p3, double width)
	{
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.width = width;
	}

	public Position getP0()
	{
		return p0;
	}

	public Position getP1()
	{
		return p1;
	}
	
	
	public Position getP2()
	{
		return p2;
	}

	public Position getP3()
	{
		return p3;
	}

	public void mergeWaves(Wave waveOne, Wave waveTwo) throws IllegalArgumentException{
		if(!waves.contains(waveOne) || !waves.contains(waveTwo)){
			throw new IllegalArgumentException("Could not merge waves because one or both of the waves is not in this lane. ");
		}
		waveOne.addUnits(waveTwo.getUnits());
		waves.remove(waveTwo);
	}
	
	/**
	 * Finds a path as a series of positions from the current position to
	 * the target within the range (i.e. the path doesn't have to get to the
	 * target, it just has to get within range of the target)
	 * 
	 * @param unit	the position to start from
	 * @param target	the target position	
	 * @param range		the minimum distance away from the target the path needs to get
	 * @return			a queue of positions that represent the path
	 */
	public Queue<Position> findPath(Unit unit, Position target, double range)
	{
		double c = 2;
		double top = Math.min(unit.getPosition().getY(), target.getY()) - c*this.width;
		double bottom = Math.max(unit.getPosition().getY(), target.getY()) + c*this.width;
		double left = Math.min(unit.getPosition().getX(), target.getX()) - c*this.width;
		double right = Math.max(unit.getPosition().getX(), target.getX()) + c*this.width;
		MapItem[] os = this.getMapItemsIn(new Position(left, top), right - left, bottom - top);
		ArrayList<MapItem> obstacles = new ArrayList<MapItem>();
		for(MapItem m : os)
			if (!(m instanceof Projectile || m instanceof Building)
					&& unit.getCollisionStrategy().canCollideWith(m)
					&& m.getState() != MapItemState.Moving)
				obstacles.add(m);
		//TODO this path finder is kinda crappy
		return pathFinder.findPath(unit, target, range, obstacles.toArray(new MapItem[0]), new Position(left, top), right - left, bottom - top);
	}
	
	/**
	 * Gets all map items colliding with the given map item. Uses the isCollidingWith method
	 * in map item to determine collisions.
	 * 
	 * @param m		the item to get collisions with
	 * @return		the list of items colliding with m
	 */
	public MapItem[] getCollisions(MapItem m)
	{
		ArrayList<MapItem> collisions = new ArrayList<MapItem>();
		MapItem[] ms = this.getMapItemsIn(
				m.getPosition().subtract(m.getRadius() / 2, m.getRadius() / 2),
				m.getRadius(), m.getRadius());
		for(MapItem mapItem : ms)
			if(m.isCollidingWith(mapItem))
				collisions.add(mapItem);
		return collisions.toArray(new MapItem[0]);
	}
	
	/**
	 * 
	 * @param p	the projectile to be added to the lane
	 */
	public void addProjectile(Projectile p)
	{
		projectiles.add(p);
	}
	
	/**
	 * 
	 * @return	the list of projectiles in the lane
	 */
	public Projectile[] getProjectiles()
	{
		return projectiles.toArray(new Projectile[0]);
	}

	public double getWidth()
	{
		return width;
	}
	
	public Wave[] getWaves()
	{
		return (Wave[])waves.toArray();
	}
	
	public Node[] getNodes()
	{
		return (Node[])nodes.toArray();
	}
	
	public ArrayList<Node> getNodesList()
	{
		return nodes;
	}

	/**
	 * Gets the position along the bezier curve represented by the percentage
	 * pos. This follows the equation found at
	 * 		<a href="http://en.wikipedia.org/wiki/Bezier_curve#Cubic_B.C3.A9zier_curves">http://en.wikipedia.org/wiki/Bezier_curve</a>
	 * B(t)= (1-t)^3 * P0 + 3(1-t)^2 * t * P1 + 3(1-t) * t^2 * P 2 + t^3 * P3 where t = [0,1].
	 * 
	 * @param pos
	 *            The percentage along the bezier curve to get a position.
	 * 
	 * @return The position along the bezier curve represented by the percentage
	 *         pos.
	 */
	public Position getPosition(double pos)
	{
		/*
		 * TODO this method has been implemented to find the position within
		 * one bezier curve. It needs to be implemented to handle a lane that
		 * is composed of multiple curves.
		 */
		double term0 = Math.pow((1 - pos), 3);
		double term1 = 3 * Math.pow(1 - pos, 2) * pos;
		double term2 = 3 * (1 - pos) * Math.pow(pos, 2);
		double term3 = Math.pow(pos, 3);

		double posX = term0 * getP0().getX() + term1 * getP1().getX()
				+ term2 * getP2().getX() + term3 * getP3().getX();
		double posY = term0 * getP0().getY() + term1 * getP1().getY()
				+ term2 * getP2().getY() + term3 * getP3().getY();

		return new Position(posX, posY);
	}

	public void addToPending(Node n, Unit u) 
	{
		int playerID = u.getOwner().getPlayerID();
		if(pendingWaves.get(playerID) == null)
		{
			pendingWaves.get(n).add(playerID, new Wave(this, u));
		}else{
			pendingWaves.get(n).get(playerID).addUnit(u);
		}
	}
	
	/**
	 * For the given node, add all of the waves from that node to this lane.
	 * @param n The node the units/waves are coming from.
	 */
	public void addPendingWaves(Node n)
	{
		int numBuckets = pendingWaves.get(n).size();
		double bucketWidth = width/numBuckets;
		double forwardBound = findForwardBound(n);
		
		//If the lane is backed up to the node, just destroy all of the pending units.
		if(forwardBound == 0)
		{
			pendingWaves.get(n).clear();
		}
		
		//For every bucket (A.K.A. every player with units coming from this node)
		for(int i = 0; i < numBuckets; i++)
		{
			double currentCloseBound = 0;
			double pendingCloseBound = 0;
			double yTopBound = (bucketWidth * i);
			double yBottomBound = yTopBound + bucketWidth;
			double yCurrentBound = yTopBound;
			
			//For every unit going into the current bucket
			for(int j = 0; j < pendingWaves.get(n).get(i).getUnits().length; j++)
			{
				Unit currentUnit = pendingWaves.get(n).get(i).getUnits()[j];
				boolean placed = false;
				
				//Make sure the unit can fit in the current player's bucket.
				if(currentUnit.getHeight() >= bucketWidth)
				{
					placed = true;
				}
				
				while(!placed)
				{
					//Make sure the unit can fit width-wise
					if(currentCloseBound + currentUnit.getWidth() <= forwardBound)
					{
						//If the unit can fit height-wise in the current "column" put it in. Otherwise advance to the next "column".
						if(yCurrentBound + currentUnit.getHeight() <= yBottomBound)
						{
							currentUnit.setPosition(new Position(currentCloseBound, yCurrentBound));
							
							//If this unit's width will push the pending bound farther, advance the pendingCloseBound.
							if(pendingCloseBound < currentCloseBound + currentUnit.getWidth())
							{
								pendingCloseBound = currentCloseBound + currentUnit.getWidth();
							}
							yCurrentBound = yCurrentBound + currentUnit.getHeight();
							placed = true;
						}else{
							currentCloseBound = pendingCloseBound;
							yCurrentBound = yTopBound;
						}
					}else{
						placed = true;
					}
				}
				placed = false;
			}
		}
		
		//Destroy any units that got skipped because they couldn't be fit.
		pendingWaves.get(n).clear();
		
	}	

	/**
	 * Finds the farthest point at which units can be spawned, defined as the position of the unit closest to the starting node. (Currently the gate)
	 * Currently assumes that the gate is straight up and down.
	 * TODO Later make it general.
	 */
	private double findForwardBound(Node n)
	{
		Gate closestGate = getClosestGate(n);
		Position gatePos = closestGate.getPosition();
		double ret;
		double plus = gatePos.getX()+(closestGate.getWidth()/2);
		double minus = gatePos.getX()-(closestGate.getWidth()/2);
		if(Math.abs(plus) < Math.abs(minus))
		{
			ret = plus;
		}else{
			ret = minus;
		}
		return ret;
	}
	
	private Gate getClosestGate(Node n)
	{
		Gate ret = gates[0];
		Position closePos = ret.getPosition();
		double currentDistance = closePos.distanceSquared(n.getPosition());
		if(gates[1].getPosition().distanceSquared(n.getPosition()) < currentDistance)
		{
			ret = gates[1];
		}
		return ret;
	}	
	/**
	 * Gets the map items intersecting with the rectangle
	 * 
	 * @param upperLeft	the upper left of the rectangle
	 * @param width		the width of the rectangle	
	 * @param height	the height of the rectangle
	 * @return			the list of map items in the rectangle
	 */
	public MapItem[] getMapItemsIn(Position upperLeft, double width, double height)
	{
		ArrayList<MapItem> items = new ArrayList<MapItem>();
		for(Wave w : waves)
		{
			Unit[] us = w.getUnits();
			for(Unit u : us)
			{
				Position p = upperLeft.subtract(u.getRadius(), u.getRadius());
				if(u.getPosition().getX() >= p.getX() &&
						u.getPosition().getY() >= p.getY() &&
						u.getPosition().getX() <= p.getX() + width + u.getRadius() &&
						u.getPosition().getY() <= p.getY() + height + u.getRadius())
					items.add(u);
			}
		}
		
		for(Projectile prj : this.getProjectiles())
		{
			Position p = upperLeft.subtract(prj.getRadius(), prj.getRadius());
			if(prj.getPosition().getX() >= p.getX() &&
					prj.getPosition().getY() >= p.getY() &&
					prj.getPosition().getX() <= p.getX() + width + prj.getRadius() &&
					prj.getPosition().getY() <= p.getY() + height + prj.getRadius())
				items.add(prj);
		}
		
		return items.toArray(new MapItem[0]);
	}
	
	/**
	 * This method updates all the projectiles and waves in the lane
	 */
	public void update()
	{
		//TODO
	}
}

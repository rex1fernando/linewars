package linewars.gamestate;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;


import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.Gate;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.LaneBorder;
import linewars.gamestate.mapItems.LaneBorderDefinition;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.combat.NoCombat;
import linewars.gamestate.mapItems.strategies.movement.Immovable;
import linewars.gamestate.shapes.Circle;

public class Lane
{
	private static final double LANE_SPAWN_DISTANCE = 0.1;
	static final double LANE_BORDER_RESOLUTION = 0.05;
	
	private String name;
	
	private BezierCurve curve;
	private HashMap<Node, ArrayList<Wave>> pendingWaves;
	private ArrayList<Wave> waves;
	private HashMap<Node, Gate> gates;
	private ArrayList<Node> nodes;
	private double gatePos;
	private GameState gameState;
	
	/**
	 * The width of the lane.
	 */
	private double width;
	
	private PathFinding pathFinder;
	
	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	private ArrayList<LaneBorder> borders = new ArrayList<LaneBorder>();
	
		
	public Lane(GameState gameState, ConfigData parser)
	{
		this.name = parser.getString(ParserKeys.name);
		//TODO
		curve = new BezierCurve(new Position(parser.getString(ParserKeys.p0)), new Position(parser.getString(ParserKeys.p1)),
					new Position(parser.getString(ParserKeys.p2)), new Position(parser.getString(ParserKeys.p3)));
		
		this.width = parser.getNumber(ParserKeys.width);
		this.nodes = new ArrayList<Node>();
		this.waves = new ArrayList<Wave>();
		this.gates = new HashMap<Node, Gate>();
		
		this.pendingWaves = new HashMap<Node, ArrayList<Wave>>();
		this.gameState = gameState;
		pathFinder = new PathFinding(gameState);
		
		double size = LANE_BORDER_RESOLUTION*this.getLength();
		try {
			LaneBorderDefinition lbd = new LaneBorderDefinition(gameState, size);
			double dis = this.getWidth() + size;
			for(double i = 0; i < 1; i += LANE_BORDER_RESOLUTION)
			{
				Transformation t = this.getPosition(i);
				Transformation t1 = new Transformation(t.getPosition().subtract(
						dis*Math.cos(t.getRotation()), 
						dis*Math.sin(t.getRotation())), 0);
				borders.add(lbd.createLaneBorder(t1));
				Transformation t2 = new Transformation(t.getPosition().add(
						dis*Math.cos(t.getRotation()), 
						dis*Math.sin(t.getRotation())), 0);
				borders.add(lbd.createLaneBorder(t2));
			}
		} catch (FileNotFoundException e) {
		} catch (InvalidConfigFileException e) {}
		
		nodes = new ArrayList<Node>();
		
	}

	/**
	 * Removes the gate guarding n from this Lane.
	 * @param n The node the gate to remove is guarding.
	 */
	public void removeGate(Node n)
	{
		gates.remove(n);
	}
	
	/**
	 * Replaces the gate guarding the node n with g.
	 * @param n The Node the gate to be changed is guarding.
	 * @param g The new gate to guard n.
	 */
	public void replaceGate(Node n, Gate g)
	{
		gates.remove(n);
		gates.put(n, g);
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
			if(!(mapItem == m))
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
		return waves.toArray(new Wave[0]);
	}
	
	public Node[] getNodes()
	{
		return nodes.toArray(new Node[0]);
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
	public Transformation getPosition(double pos)
	{
		return curve.getPosition(pos);
	}

	
	
	public void addToPending(Node n, Unit u) 
	{
		int playerID = u.getOwner().getPlayerID();
		if(pendingWaves.get(n) == null)
			pendingWaves.put(n, new ArrayList<Wave>());
		
		ArrayList<Wave> waves = pendingWaves.get(n);
		for(int i = 0; i < waves.size(); i++)
			if(waves.get(i).getUnits()[0].getOwner().getPlayerID() == playerID)
				waves.get(i).addUnit(u);
		
		waves.add(new Wave(this, u, n));
	}
	
	/**
	 * For the given node, add all of the waves from that node to this lane.
	 * @param n The node the units/waves are coming from.
	 * TODO this is actually a fairly tricky problem, since we have no particular constraints on the size of anything (except nonnegativity ofc).  Seems fun. - Taylor
	 */
//	public void addPendingWaves(Node n)
//	{
//		if(pendingWaves.isEmpty())
//		{
//			return;
//		}
//		int numBuckets = pendingWaves.get(n).size();
//		double bucketWidth = width/numBuckets;
//		double forwardBound = findForwardBound(n);
//		
//		//If the lane is backed up to the node, just destroy all of the pending units.
//		if(forwardBound == 0)//TODO checking if a double is equal to 0 here instead of within rounding errors; is this intended?
//		{
//			pendingWaves.get(n).clear();
//		}
//		
//		//For every bucket (A.K.A. every player with units coming from this node)
//		for(int i = 0; i < numBuckets; i++)
//		{
//			double currentCloseBound = 0;
//			double pendingCloseBound = 0;
//			double yTopBound = (bucketWidth * i);
//			double yBottomBound = yTopBound + bucketWidth;
//			double yCurrentBound = yTopBound;
//			
//			//For every unit going into the current bucket
//			for(int j = 0; j < pendingWaves.get(n).get(i).getUnits().length; j++)
//			{
//				Unit currentUnit = pendingWaves.get(n).get(i).getUnits()[j];
//				boolean placed = false;
//				
//				//Make sure the unit can fit in the current player's bucket.
//				if(currentUnit.getHeight() >= bucketWidth)
//				{
//					placed = true;
//				}
//				
//				while(!placed)
//				{
//					//Make sure the unit can fit width-wise
//					if(currentCloseBound + currentUnit.getWidth() <= forwardBound)
//					{
//						//If the unit can fit height-wise in the current "column" put it in. Otherwise advance to the next "column".
//						if(yCurrentBound + currentUnit.getHeight() <= yBottomBound)
//						{
//							currentUnit.setPosition(new Position(currentCloseBound, yCurrentBound));
//							waves.add(new Wave(this, currentUnit));
//							
//							//If this unit's width will push the pending bound farther, advance the pendingCloseBound.
//							if(pendingCloseBound < currentCloseBound + currentUnit.getWidth())
//							{
//								pendingCloseBound = currentCloseBound + currentUnit.getWidth();
//							}
//							yCurrentBound = yCurrentBound + currentUnit.getHeight();
//							placed = true;
//						}else{
//							currentCloseBound = pendingCloseBound;
//							yCurrentBound = yTopBound;
//						}
//					}else{
//						placed = true;
//					}
//				}
//				placed = false;
//			}
//		}
//		
//		//Destroy any units that got skipped because they couldn't be fit.
//		pendingWaves.get(n).clear();
//		for(int i = 0; i < gameState.getNumPlayers(); i++){
//			pendingWaves.get(n).add(new Wave(this));
//		}
//		
//	}	
	
	public void addPendingWaves(Node n)
	{
		if(pendingWaves.isEmpty())
			return;
		
		ArrayList<Wave> waves = pendingWaves.get(n);
		ArrayList<Unit> units = new ArrayList<Unit>();
		for(Wave w : waves)
			for(Unit u : w.getUnits())
				units.add(u);
		
		//sort units in descending order by radius
		Collections.sort(units, new Comparator<Unit>() {
			public int compare(Unit u1, Unit u2){
				if(u1.getRadius() - u2.getRadius() < 0)
					return 1;
				else if((u1.getRadius() - u2.getRadius() > 0))
					return -1;
				else
					return 0;
			}
		});
		
		Gate closestGate = gates.get(n);
		//start represents if we're going up the lane (0 -> 1) or down (1 -> 0)
		double start = 0;
		if (closestGate.getPosition().distanceSquared(
				this.getPosition(1).getPosition()) < closestGate.getPosition()
				.distanceSquared(this.getPosition(0).getPosition()))
			start = 1;
		
		//represents the minimum forward position on the curve [0,1] that a unit must be placed (ie the back of the current row) 
		double minForward = (start == 0 ? closestGate.getRadius()/this.getLength() : start - closestGate.getRadius()/this.getLength());
		//the place to put the next min forward, is calculated as this line is placed based off the largest radius unit, (ie the next row)
		double nextMinForward = minForward;
		//this is the farthest forward from the node [0,1] along the curve units are allowed to spawn
		double forwardBound = findForwardBound(n);
		//this represents the position along the lateral part of the lane a unit must be placed below
		double startWidth = width/2;
		ArrayList<Unit> deletedUnits = new ArrayList<Unit>();
		//while minForward hasn't reached the forward bound (depending on if we're going up or down the lane) and there are still units to place
		while(((minForward < forwardBound && start == 0) || (minForward > forwardBound && start == 1)) && !units.isEmpty())
		{
			for(int i = 0; i < units.size() && startWidth > -width/2;) //look for the biggest unit that will fit
			{
				Unit u = units.get(i);
				
				if((start == 0 && minForward + 2*u.getRadius()/this.getLength() > forwardBound) //if this unit will never fit
					|| (start == 1 && minForward - 2*u.getRadius()/this.getLength() < forwardBound)
					|| (2*u.getRadius() > width))
				{
					units.remove(i); //get rid of it
					deletedUnits.add(u);
					u.getOwner().removeUnit(u);
				}
				
				if(startWidth - 2*u.getRadius() > -width/2) //if there's enough room to fit the unit
				{
					double pos = u.getRadius()/this.getLength(); //get the radius in terms of length along the curve
					pos = minForward + (start == 0 ? pos : -pos); //now figure out where the exact position along the lane the unit should go
					Transformation tpos = this.getPosition(pos); //use the position to get the exact transformation of that position
					if(start == 1) tpos = new Transformation(tpos.getPosition(), Math.PI);
					double w = startWidth - u.getRadius(); //figure out the lateral translation from the center line of the curve for the unit
					tpos = new Transformation(
							new Position(tpos.getPosition().getX() + w*Math.cos(tpos.getRotation()),
										tpos.getPosition().getY() + w*Math.sin(tpos.getRotation())),
							tpos.getRotation() - Math.PI); //now translate the position from the curve itself to w off the curve
					u.setTransformation(tpos);
					startWidth -= 2*u.getRadius(); //update the startWidth so the next placed unit will be moved laterally from this unit
					
					units.remove(i); //the unit has been placed, get it out of here
					if(start == 0) //if we're going up the lane
						if(2*u.getRadius()/this.getLength() + minForward > nextMinForward) //if this unit has a bigger radius than any other unit
							nextMinForward = 2*u.getRadius()/this.getLength() + minForward; //that's already been placed in this row
					else //if we're going down the lane
						if(minForward - 2*u.getRadius()/this.getLength() < nextMinForward) //same as above
							nextMinForward = minForward - 2*u.getRadius()/this.getLength();
				}
				else //if there's not enough room, check the next biggest unit
					i++;
			}
			minForward = nextMinForward; //no more units can fit in this row, go to the next row
			startWidth = this.getWidth()/2; //restart the lateral placement
		}
		
		for(Wave w : waves)
		{
			for(Unit u : deletedUnits)
				if(w.contains(u))
					w.remove(u);
			this.waves.add(w);
		}
		
		pendingWaves.get(n).clear();
		
	}

	/**
	 * Finds the farthest point at which units can be spawned, defined as the position
	 * farthest away from the gate that units are allowed to spawn
	 */
	private double findForwardBound(Node n)
	{
		Gate closestGate = gates.get(n);
		double start = 0;
		if (closestGate.getPosition().distanceSquared(
				this.getPosition(1).getPosition()) < closestGate.getPosition()
				.distanceSquared(this.getPosition(0).getPosition()))
			start = 1;
		
		if(start == 0)
			return LANE_SPAWN_DISTANCE;
		else 
			return 1 - LANE_SPAWN_DISTANCE;
	}	
	
	/**
	 * Gets the map items intersecting with the rectangle
	 * TODO Refactor this to use Shapes?
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
		
		for(LaneBorder lb : borders)
		{
			Position p = upperLeft.subtract(lb.getRadius(), lb.getRadius());
			if(lb.getPosition().getX() >= p.getX() &&
					lb.getPosition().getY() >= p.getY() &&
					lb.getPosition().getX() <= p.getX() + width + lb.getRadius() &&
					lb.getPosition().getY() <= p.getY() + height + lb.getRadius())
				items.add(lb);
		}
		
		return items.toArray(new MapItem[0]);
	}
	
	public List<Unit> getUnitsIn(Circle c)
	{
		ArrayList<Unit> units = new ArrayList<Unit>();
		for(Wave w : waves)
		{
			Unit[] us = w.getUnits();
			for(Unit u : us)
			{
				if(u.getPosition().distanceSquared(c.position().getPosition()) <= Math.pow(c.getRadius() + u.getRadius(), 2))
					units.add(u);
			}
		}
		
		return units;
	}
	
	/**
	 * This method updates all the projectiles and waves in the lane
	 */
	public void update()
	{
		if(nodes.size() != 2)
			throw new IllegalStateException("This lane doesn't know about both its end point nodes");
		
		for(int i = 0; i < waves.size();)
		{
			Wave w = waves.get(i);
			w.update();
			//get rid of dead waves
			if(w.getUnits().length == 0)
				waves.remove(i);
			else
				i++;
		}
		
		for(int i = 0; i < projectiles.size();)
		{
			Projectile p = projectiles.get(i);
			p.move();
			p.update();
			//get rid of dead projectiles
			if(p.getState() == MapItemState.Dead && p.finished())
				projectiles.remove(i);
			else
				i++;
		}
		
		//TODO is this the right spot?
		fixCollisions();
	}
	
	/**
	 * Finds and fixes all of the current collisions in this Lane.
	 */
	private void fixCollisions()
	{
		//First find all the collisions
		HashMap<Unit, Position> collisionVectors = new HashMap<Unit, Position>();
		List<Unit> allUnits = getUnits();
		for(Unit first : allUnits){//for each unit in the lane
			collisionVectors.put(first, new Position(0, 0));//doesn't have to move yet
			if(first.getState() != MapItemState.Moving) continue;//if this Unit isn't moving, it isn't going to get shoved
			
			for(Unit second : allUnits){//for each unit it could be colliding with
				if(first == second) continue;//units can't collide with themselves
				if(first.getCollisionStrategy().canCollideWith(second)){//if this type of unit can collide with that type of unit
					if(first.isCollidingWith(second)){//if the two units are actually colliding
						Position offsetVector = first.getPosition().subtract(second.getPosition());//The vector from first to second
						
						//TODO verify/test these calculations
						double distanceApart = offsetVector.length();
						double radSum = first.getRadius() + second.getRadius();
						double overlap = distanceApart - radSum;
						offsetVector = offsetVector.scale(overlap / distanceApart);
						
						if(second.getState() == MapItemState.Moving){
							//move first by -offsetvector/2
							Position newPosition = collisionVectors.get(first).add(offsetVector.scale(-.5));
							collisionVectors.put(first, newPosition);
						}
						else{
							//move first by -offsetvector
							Position newPosition = collisionVectors.get(first).add(offsetVector.scale(-1));
							collisionVectors.put(first, newPosition);
						}
					}
				}
			}
		}
		
		//Then resolve them by shifting stuff around
		for(Unit toMove : allUnits){
			if(collisionVectors.get(toMove).length() > 0){
				toMove.getBody().transform(new Transformation(collisionVectors.get(toMove), 0));				
			}
		}
	}
	
	private List<Unit> getUnits() {
		ArrayList<Unit> ret = new ArrayList<Unit>();
		for(Wave toCheck : waves){
			for(Unit toAdd : toCheck.getUnits()){
				ret.add(toAdd);
			}
		}
		return ret;
	}

	public Gate getGate(Node n)
	{
		return gates.get(n);
	}
	
	public void addGate(Node n, Player p)
	{
		Transformation t = null;
		if (n.getPosition().getPosition().distanceSquared(
				this.getPosition(1).getPosition()) < n.getPosition().getPosition()
				.distanceSquared(this.getPosition(0).getPosition()))
			t = this.getPosition(1);
		else
			t = this.getPosition(0);
		Gate g = p.getGateDefinition().createGate(t);
		Gate oldG = gates.get(n);
		gates.put(n, g);
		//if there is already a gate here, find the wave that contains it and remove it
		for(Wave w : waves)
			if(w.contains(oldG))
			{
				w.remove(oldG);
				w.addUnit(g);
				return;
			}
		
		//if there isn't a gate, then make a new wave that contains it
		waves.add(new Wave(this, g, n));
	}
	
	public void addNode(Node n)
	{
		if(nodes.size() == 2)
			throw new IllegalArgumentException("Can't add more than 2 nodes to a lane");
		nodes.add(n);
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getLength()
	{
		return curve.getLength();
	}
	
	public double getClosestPointRatio(Position p) 
	{
		return curve.getClosestPointRatio(p);
	}
}

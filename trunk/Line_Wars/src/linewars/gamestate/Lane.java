package linewars.gamestate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.Gate;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategyConfiguration;
import linewars.gamestate.shapes.AABB;
import linewars.gamestate.shapes.Circle;

/**
 * 
 * @author John George, Connor Schenck, Taylor Bergquist
 * 
 * This class represents a lane in the map.
 *
 */
public strictfp class Lane
{
	private static final double LANE_GATE_DISTANCE = 0.1;
	private static final int NUM_COLLISION_FIXES = 1;
	
	private HashMap<Node, HashMap<Player, Wave>> pendingWaves;
	private ArrayList<Wave> waves;
	private HashMap<Node, Gate> gates;
	private ArrayList<Node> nodes;
	private GameState gameState;
	
	private LinkedList<Unit> horizontallySortedUnits, verticallySortedUnits;
	
	/**
	 * The width of the lane.
	 */
	
	
	private PathFinding pathFinder;
	
	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	
	private LaneConfiguration config;
		
	public Lane(GameState gameState, LaneConfiguration config)
	{
		
		this.horizontallySortedUnits = new LinkedList<Unit>();
		this.verticallySortedUnits = new LinkedList<Unit>();
		
		this.nodes = new ArrayList<Node>();
		this.waves = new ArrayList<Wave>();
		this.gates = new HashMap<Node, Gate>();
		
		this.pendingWaves = new HashMap<Node, HashMap<Player, Wave>>();
		this.gameState = gameState;
		pathFinder = new PathFinding(gameState);
		
		this.config = config;
	}
	
	public boolean isInLane(MapItem m)
	{
		AABB box = m.getBody().getAABB();
		List<Unit> units = this.getUnitsIn(box);
		for(Unit u : units)
			if(u.equals(m) || u.containsRecursively(m))
				return true;
		
		for(Projectile p : projectiles)
			if(p.equals(m) || p.containsRecursively(m))
				return true;
		
		return false;
	}
	
	public LaneConfiguration getConfig()
	{
		return config;
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
	 * Merges the second wave given into the first wave.
	 * 
	 * @param waveOne
	 * @param waveTwo
	 * @throws IllegalArgumentException
	 */
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
		double top = Math.min(unit.getPosition().getY(), target.getY()) - c*this.getWidth();
		double bottom = Math.max(unit.getPosition().getY(), target.getY()) + c*this.getWidth();
		double left = Math.min(unit.getPosition().getX(), target.getX()) - c*this.getWidth();
		double right = Math.max(unit.getPosition().getX(), target.getX()) + c*this.getWidth();
		AABB box = new AABB(left, top, right, bottom);
		List<Unit> os = this.getUnitsIn(box);
		ArrayList<MapItem> obstacles = new ArrayList<MapItem>();
		for(MapItem m : os)
			if (!(m instanceof Projectile || m instanceof Building)
					&& CollisionStrategyConfiguration.isAllowedToCollide(m, unit)
					&& m.getState() != MapItemState.Moving)
				obstacles.add(m);
		//TODO this path finder is kinda crappy
		return pathFinder.findPath(unit, target, range, obstacles.toArray(new MapItem[0]), new Position(left, top), right - left, bottom - top);
	}
	
	/**
	 * 
	 * @param p	the projectile to be added to the lane
	 */
	public void addProjectile(Projectile p)
	{
		p.setLane(this);
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

	/**
	 * 
	 * @return	 the width of the lane
	 */
	public double getWidth()
	{
		return config.getWidth();
	}
	
	/**
	 * 
	 * @return	all the waves currently in this lane
	 */
	public Wave[] getWaves()
	{
		return waves.toArray(new Wave[0]);
	}
	
	/**
	 * 
	 * @return	the nodes attached to either end of this lane
	 */
	public Node[] getNodes()
	{
		return nodes.toArray(new Node[0]);
	}
	
	/**
	 * 
	 * @return	the nodes attached to either end of this lane
	 */
	public ArrayList<Node> getNodesList()
	{
		return nodes;
	}
	
	/**
	 * 
	 * @return	the bezier curve object that defines the path of this lane
	 */
	public BezierCurve getCurve()
	{
		return config.getBezierCurve();
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
		return this.getCurve().getPosition(pos);
	}

	/**
	 * Adds u to the list of units pending to be spawned from node n
	 * 
	 * @param n	the node to spawn the unit u from
	 * @param u	the unit to spawn
	 */
	public void addToPending(Node n, Unit u) 
	{
		if(pendingWaves.get(n) == null)
			pendingWaves.put(n, new HashMap<Player, Wave>());
		
		if(pendingWaves.get(n).get(u.getOwner()) == null)
			pendingWaves.get(n).put(u.getOwner(), new Wave(this, u, n));
		else
			pendingWaves.get(n).get(u.getOwner()).addUnit(u);
	}
	
	/**
	 * For the given node, add all of the waves from that node to this lane.
	 * @param n The node the units/waves are coming from.
	 */
	public void addPendingWaves(Node n)
	{
		if(pendingWaves.isEmpty() || pendingWaves.get(n) == null)
			return;
		
		Set<Entry<Player, Wave>> waveSet = pendingWaves.get(n).entrySet();
		ArrayList<Wave> waves = new ArrayList<Wave>();
		for(Entry<Player, Wave> e : waveSet)
			waves.add(e.getValue());
		
		ArrayList<Unit> units = extractAndSortUnits(waves);
		
		Gate closestGate = this.getGate(n);
		//start represents if we're going up the lane (0 -> 1) or down (1 -> 0)
		boolean forward = true;
		if (closestGate.getPosition().distanceSquared(
				this.getPosition(1).getPosition()) < closestGate.getPosition()
				.distanceSquared(this.getPosition(0).getPosition()))
			forward = false;
		
		//represents the minimum forward position on the curve [0,1] that a unit must be placed (ie the back of the current row) 
		double minForward = (forward ? 0 : 1);
		//the place to put the next min forward, is calculated as this line is placed based off the largest radius unit, (ie the next row)
		double nextMinForward = minForward;
		//the biggest radius of a unit in one row
		double biggestRadius = 0;
		//this is the farthest forward from the node [0,1] along the curve units are allowed to spawn
		double forwardBound = findForwardBound(n); //TODO make this not related to next closest unit
		//this represents the position along the lateral part of the lane a unit must be placed below
		double startWidth = this.getWidth()/2;
		ArrayList<Unit> deletedUnits = new ArrayList<Unit>();
		//the last row will need to be centered, so remember it
		ArrayList<Unit> lastRow = new ArrayList<Unit>();
		//while minForward hasn't reached the forward bound (depending on if we're going up or down the lane) and there are still units to place
		while(((minForward < forwardBound && forward) || (minForward > forwardBound && !forward)) && !units.isEmpty())
		{
			lastRow.clear();
			startWidth = this.getWidth()/2; //restart the lateral placement
			for(int i = 0; i < units.size() && startWidth > -this.getWidth()/2;) //look for the biggest unit that will fit
			{
				Unit u = units.get(i);
				
				if((forward && minForward + 2*u.getRadius()/this.getLength() > forwardBound) //if this unit will never fit
					|| (!forward && minForward - 2*u.getRadius()/this.getLength() < forwardBound)
					|| (2*u.getRadius() > this.getWidth()))
				{
					units.remove(i); //get rid of it
					deletedUnits.add(u);
					u.getOwner().removeUnit(u);
				}
				else if(startWidth - 2*u.getRadius() > -this.getWidth()/2) //if there's enough room to fit the unit
				{
					double pos = u.getRadius()/this.getLength(); //get the radius in terms of length along the curve
					pos = minForward + (forward ? pos : -pos); //now figure out where the exact position along the lane the unit should go
					Transformation tpos = this.getPosition(pos); //use the position to get the exact transformation of that position
					if(!forward) tpos = new Transformation(tpos.getPosition(), tpos.getRotation() - Math.PI);
					double w = startWidth - u.getRadius(); //figure out the lateral translation from the center line of the curve for the unit
					Position dir = Position.getUnitVector(tpos.getRotation());
					tpos = new Transformation(dir.orthogonal().scale(w).add(tpos.getPosition()), tpos.getRotation());
					u.setTransformation(tpos);
					startWidth -= 2.05*u.getRadius(); //update the startWidth so the next placed unit will be moved laterally from this unit
					
					units.remove(i); //the unit has been placed, get it out of here
					lastRow.add(u);
					if(u.getRadius() > biggestRadius)
					{
						biggestRadius = u.getRadius();
						Transformation backPos = this.getPosition(minForward);
						if(!forward) //if we're going up the lane
							backPos = new Transformation(backPos.getPosition(), backPos.getRotation() + Math.PI); //flip the direction backwards
						
						nextMinForward = this.getClosestPointRatio(backPos.getPosition()
								.add(Position.getUnitVector(backPos.getRotation()).scale(2.05*biggestRadius)));
					}
					
					//notify sweep and prune that a unit has been added
					this.notifySweepAndPruneUnitAdded(u);
				}
				else //if there's not enough room, check the next biggest unit
					i++;
			}
			minForward = nextMinForward; //no more units can fit in this row, go to the next row
		}
		
		if(units.size() > 0)
		{
			deletedUnits.addAll(units);
			units.clear();
		}
		
		//now center the last row
		double moveBack = Math.abs((this.getWidth() - (this.getWidth()/2 - startWidth))/2); //this is how much we need to move the units by to center them
		for(Unit u : lastRow)
		{
			Transformation tpos = u.getTransformation();
			Position dir = Position.getUnitVector(u.getRotation());
			tpos = new Transformation(dir.orthogonal().scale(-moveBack).add(tpos.getPosition()), tpos.getRotation());
			u.setTransformation(tpos);
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

	private ArrayList<Unit> extractAndSortUnits(ArrayList<Wave> waves) {
		ArrayList<Unit> units = new ArrayList<Unit>();
		for(Wave w : waves)
			for(Unit u : w.getUnits())
			{
				units.add(u);
				u.setWave(w); //set the unit's wave
			}
		
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
		
		//sort units in descending order by range
		Collections.sort(units, new Comparator<Unit>() {
			public int compare(Unit u1, Unit u2){
				if(u1.getCombatStrategy().getRange() - u2.getCombatStrategy().getRange() < 0)
					return 1;
				else if((u1.getCombatStrategy().getRange() - u2.getCombatStrategy().getRange() > 0))
					return -1;
				else
					return 0;
			}
		});
		return units;
	}

	/**
	 * Finds the farthest point at which units can be spawned, defined as the position
	 * farthest away from the gate that units are allowed to spawn
	 */
	private double findForwardBound(Node n)
	{
		Gate closestGate = this.getGate(n);
		boolean forward = true;
		if (closestGate.getPosition().distanceSquared(
				this.getPosition(1).getPosition()) < closestGate.getPosition()
				.distanceSquared(this.getPosition(0).getPosition()))
			forward = false;
		
		double pos;
		if(forward)
		{
			pos = 1;
			for(Wave w : this.getWaves())
			{
				double d = w.getPositionToP0(false);
				if(d < pos)
					pos = d;
			}
		}
		else 	
		{
			pos = 0;
			for(Wave w : this.getWaves())
			{
				double d = 1 - w.getPositionToP3();
				if(d > pos)
					pos = d;
			}
		}
		return pos;
	}
	
	/**
	 * Returns a list of all units within the circle c and in this lane
	 * 
	 * @param c	the circle to get units in
	 * @return	the units in c
	 */
	public List<Unit> getUnitsIn(AABB box)
	{
		//TODO implement this using sweep and prune data structures
		List<Unit> ret = new LinkedList<Unit>();
		for(Wave w : waves)
		{
			for(Unit u : w.getUnits())
			{
				AABB body = u.getBody().getAABB();
				if(body.getXMax() > box.getXMin() &&
				   body.getXMin() < box.getXMax() &&
				   body.getYMax() > box.getYMin() &&
				   body.getYMin() < box.getYMax())
					ret.add(u);
			}
		}
		return ret;
	}
	
	/**
	 * This method updates all the projectiles and waves in the lane
	 */
	public void update()
	{
		if(nodes.size() != 2)
			throw new IllegalStateException("This lane doesn't know about both its end point nodes");
		
		for(Node n : nodes)
			this.addPendingWaves(n);
		
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
			p.updateMapItem();
			//get rid of dead projectiles
			if(p.getState() == MapItemState.Dead && p.finished()) {
				projectiles.remove(i);
			} else {
				i++;
			}
		}
		
		for(int i = 0; i < NUM_COLLISION_FIXES; i++){
			findAndResolveCollisions();			
		}
//		checkWaveConsistency();
	}
	
	/**
	 * Finds and resolves all the collisions in the Lane
	 */
	private void findAndResolveCollisions(){
		long currentTime = System.currentTimeMillis();
		pushUnitsOntoLane();
		//First find all the collisions
		HashMap<MapItem, Position> collisionVectors = new HashMap<MapItem, Position>();
				
		//List<Unit> snppotentiallyCollidingUnits = sweepAndPrune();
		//List<Unit> potentiallyCollidingUnits = getCollidableMapItems();
		List<Unit> potentiallyCollidingUnits = sweepAndPrune();
		
		
		for(Unit first : potentiallyCollidingUnits){//for each unit in the lane
			collisionVectors.put(first, new Position(0, 0));//doesn't have to move yet
			
			for(Unit second : potentiallyCollidingUnits){//for each unit it could be colliding with
				if(first == second) continue;//units can't collide with themselves
				if(first.isCollidingWith(second)){//if the two units are actually colliding
					Position offsetVector = first.getPosition().subtract(second.getPosition());//The vector from first to second
					
					second.getMovementStrategy().notifyOfCollision(offsetVector);
					first.getMovementStrategy().notifyOfCollision(offsetVector.scale(-1));
					
					/*if(!snppotentiallyCollidingUnits.contains(first) || ! snppotentiallyCollidingUnits.contains(second)){
						System.out.println("WTF");
					}*/
					
				}
			}
		}
		
		System.out.println(System.currentTimeMillis() - currentTime);
	}
	
	private LinkedList<Unit> sweepAndPrune()
	{
		if(horizontallySortedUnits.size() == 0){
			return new LinkedList<Unit>();
		}
		
		LinkedList<Unit> potentiallyCollidingUnits = new LinkedList<Unit>();
		
		HashSet<Unit> horizontallyCollidingUnits = new HashSet<Unit>();
		
		updateSweepAndPruneStructures();
	
		boolean addedLastUnit = false;
		Iterator<Unit> iter = horizontallySortedUnits.iterator();
		Unit lastUnit = iter.next();
		for (; iter.hasNext();)
		{
			Unit currentUnit = iter.next();
			if (lastUnit.getBody().getAABB().getXMax() > currentUnit.getBody().getAABB().getXMin())
			{	
				if (!addedLastUnit) {
					horizontallyCollidingUnits.add(lastUnit);
					addedLastUnit = true;
				}
				horizontallyCollidingUnits.add(currentUnit);
			}
			lastUnit = currentUnit;
		}
		
		addedLastUnit = false;
		iter = verticallySortedUnits.iterator();
		lastUnit = iter.next();
		for (; iter.hasNext();)
		{
			Unit currentUnit = iter.next();
			if (lastUnit.getBody().getAABB().getYMax() > currentUnit.getBody().getAABB().getYMin()
					&& horizontallyCollidingUnits.contains(lastUnit))
			{
				if (!addedLastUnit)
					potentiallyCollidingUnits.add(lastUnit);
				potentiallyCollidingUnits.add(currentUnit);
			}
			lastUnit = currentUnit;
		}
		
		return potentiallyCollidingUnits;
	}
	
	private void initializeSortedUnits(List<Unit> allUnits)
	{
		horizontallySortedUnits = new LinkedList<Unit>(allUnits);
		verticallySortedUnits = new LinkedList<Unit>(allUnits);
	}
	
	
	private void sortUnits()
	{
		Collections.sort(horizontallySortedUnits, new Comparator<MapItem>()
				{
					public int compare(MapItem m1, MapItem m2)
					{
						if (m1.getBody().getAABB().getXMin() < m2.getBody().getAABB().getXMin())
							return -1;
						else
							return 1;
					}
				});
				
		Collections.sort(verticallySortedUnits, new Comparator<MapItem>()
				{
					public int compare(MapItem m1, MapItem m2)
					{
						if (m1.getBody().getAABB().getYMin() < m2.getBody().getAABB().getYMin())
							return -1;
						else
							return 1;
					}
				});
	}

	private void pushUnitsOntoLane() {
		//for each unit
		List<Unit> allUnits = getCollidableMapItems();
		for(Unit toMove : allUnits){
			//get its closest point on the curve
			Transformation pointOnCurve = this.getCurve().getPosition(toMove.getPositionAlongCurve());
			//use the width and stuff to figure out where it should be placed so it's actually on the Lane
			Position offset = toMove.getPosition().subtract(pointOnCurve.getPosition());
			double offsetMag = offset.length();
			double maxShouldBe = this.getWidth() / 2;
			if(offsetMag < maxShouldBe){
				continue;
			}
			double ratio = maxShouldBe / offsetMag;
			offset = offset.scale(ratio);
			//put it there
			toMove.setTransformation(new Transformation(pointOnCurve.getPosition().add(offset), pointOnCurve.getRotation()));
		}
	}

	private void checkWaveConsistency()
	{
		for(Wave w: waves)
		{
			for(Wave x : waves)
				if(x != w)
				{
					for(Unit u : w.getUnits())
					{
						for(Unit y : x.getUnits())
							if(u == y)
								throw new IllegalStateException("There are multiple waves with the same unit reference ID!");
					
						if(x.contains(u))
							System.err.println("There are multiple waves with units in identical positions!");
					}
			}
		}
	}
	
	/**
	 * Gets the gate at the end of the lane n is at
	 * 
	 * @param n	the node at the end of the lane the gate is at
	 * @return	the gate at n's end of the lane
	 */
	public Gate getGate(Node n)
	{
		//add a dummy gate
		if(gates.get(n) == null)
		{
			Transformation t = null;
			if (n.getTransformation().getPosition().distanceSquared(
					this.getPosition(1).getPosition()) < n.getTransformation().getPosition()
					.distanceSquared(this.getPosition(0).getPosition()))
				t = this.getPosition(1 - LANE_GATE_DISTANCE);
			else
				t = this.getPosition(LANE_GATE_DISTANCE);
			Gate g = gameState.getPlayer(0).getGateDefinition().createGate(t, gameState.getPlayer(0), gameState);
			g.setState(MapItemState.Dead);
			gates.put(n, g);
		}
		return gates.get(n);
	}
	
	/**
	 * Adds a gate to n's end of the lane and removes any existing gates at that
	 * end of the lane.
	 * 
	 * @param n	the end of the lane to add the gate at
	 * @param p	the player who owns the new gate being placed
	 */
	public void addGate(Node n, Player p)
	{
		
		///*
		Transformation t = null;
		if (n.getTransformation().getPosition().distanceSquared(
				this.getPosition(1).getPosition()) < n.getTransformation().getPosition()
				.distanceSquared(this.getPosition(0).getPosition()))
		{
			t = this.getPosition(1 - LANE_GATE_DISTANCE);
			t = new Transformation(t.getPosition(), Math.PI + t.getRotation());
		}
		else
			t = this.getPosition(LANE_GATE_DISTANCE);
		Gate g = p.getGateDefinition().createGate(t, p, gameState);
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
		//*/
		/*
		for(double i = 0; i <= 1; i += 0.1)
		{
			Transformation t = new Transformation(this.getPosition(i).getPosition().add(0, i*200), this.getPosition(i).getRotation());
			Gate g = p.getGateDefinition().createGate(t);
			this.waves.add(new Wave(this, g, n));
		}
		//*/
	}
	
	/**
	 * 
	 * @return	a list of all the collidable map items in the lane
	 */
	public List<Unit> getCollidableMapItems(){
		ArrayList<Unit> units = new ArrayList<Unit>();
		for(Wave w : waves){
			for(Unit toAdd : w.getUnits()){
				units.add(toAdd);
			}
		}
		return units;
	}
	
	/**
	 * adds n to the lane as one of its endpoints
	 * 
	 * @param n	
	 */
	public void addNode(Node n)
	{
		if(nodes.size() == 2)
			throw new IllegalArgumentException("Can't add more than 2 nodes to a lane");
		nodes.add(n);
	}
	
	/**
	 * 
	 * @return	the length of the lane
	 */
	public double getLength()
	{
		return this.getCurve().getLength();
	}
	
	/**
	 * Gets the parameter t such that getPosition(t) returns
	 * the point on the center line of the lane closest to p.
	 * 
	 * @param p	the point to lookup
	 * @return	the parameter of the point closest to p
	 */
	public double getClosestPointRatio(Position p) 
	{
		return this.getCurve().getClosestPointRatio(p);
	}
	
	/**
	 * 
	 * @return	the game state this lane is in
	 */
	public GameState getGameState()
	{
		return gameState;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Lane)
		{
			return config.equals(((Lane)o).config);
		}
		else
			return false;
	}
	
	public void notifySweepAndPruneUnitAdded(Unit addedUnit){
		horizontallySortedUnits.add(addedUnit);
		verticallySortedUnits.add(addedUnit);
	}
	
	public void notifySweepAndPruneUnitRemoved(Unit removedUnit){
		horizontallySortedUnits.remove(removedUnit);
		verticallySortedUnits.remove(removedUnit);
	}
	
	private void updateSweepAndPruneStructures() 
	{
		sortUnits();
	}

}

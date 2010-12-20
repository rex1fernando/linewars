package linewars.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Unit;

public class PathFinding {
	
	private static final int UNIT_PRECISION = 2;
	
	private class Node{
		
		double visited = 1;
		Position p;
		boolean valid = true;
		double disSquared;
		
	}
	
	private HashMap<String, HashMap<Position, Node>> hashOfMaps = new HashMap<String, HashMap<Position, Node>>();
	private int currentTick = -1;
	
	private double currentVisitedID;
	private Unit unit;
	private MapItem[] obstacles;
	private Position target;
	private double radiusSquared;
	private Position upperLeft;
	private double width;
	private double height;
	private HashMap<Position, Node> nodeMap;
	private GameState gameState;
	
	public PathFinding(GameState gameState)
	{
		this.gameState = gameState;
	}
	
	public Queue<Position> findPath(Unit unit, Position target, double radius, MapItem[] obstacles, Position upperLeft, double width, double height)
	{
		if(currentTick != gameState.getTimerTick())
		{
			currentTick = gameState.getTimerTick();
			hashOfMaps = new HashMap<String, HashMap<Position, Node>>();
		}
		
		nodeMap = hashOfMaps.get(unit.getURI());
		
		currentVisitedID = Math.random();
		this.target = target;
		this.radiusSquared = radius*radius;
		this.obstacles = obstacles;
		this.upperLeft = upperLeft;
		this.width = width;
		this.height = height;
		this.unit = unit;
		
		double size = unit.getRadius()/UNIT_PRECISION;
		int x = (int) (unit.getPosition().getX()/size);
		int y = (int) (unit.getPosition().getY()/size);
		Node n = new Node();
		n.p = new Position((x + 0.5)*size, (y + 0.5)*size);
		
		return recursePath(n);
	}
	
	private Queue<Position> recursePath(Node n)
	{
		//first check to see if this is a valid node
		if(!n.valid || n.visited == currentVisitedID)
			return new LinkedList<Position>();
		
		//now check to see if this is a goal node
		if(target.distanceSquared(n.p) <= radiusSquared)
		{
			LinkedList<Position> list = new LinkedList<Position>();
			list.add(n.p);
			return list;
		}
		
		n.visited = currentVisitedID;
		//next recurse through the neighbors, picking the best one first
		ArrayList<Node> neighbors = new ArrayList<Node>();
		double size = unit.getRadius()/UNIT_PRECISION;
		
		for(int i = 0; i < 4; i++)
		{
			int deltaX = 0;
			int deltaY = 0;
			//go right one
			if(i == 0)
			{
				deltaX = 1;
				if(n.p.getX() + size > upperLeft.getX() + width)
					continue;
			}
			//go up one
			else if(i == 1)
			{
				deltaY = -1;
				if(n.p.getY() - size < upperLeft.getY())
					continue;
			}
			//go left one
			if(i == 2)
			{
				deltaX = -1;
				if(n.p.getX() - size < upperLeft.getX())
					continue;
			}
			//go down one
			if(i == 3)
			{
				deltaY = 1;
				if(n.p.getY() + size > upperLeft.getY() + height)
					continue;
			}
				 
			int x = (int) (unit.getPosition().getX()/size);
			int y = (int) (unit.getPosition().getY()/size);
			Position p = new Position((x + 0.5 + deltaX)*size, (y + 0.5 + deltaY)*size);
			Node newNode = nodeMap.get(p);
			if(newNode == null)
			{
				newNode = new Node();
				newNode.p = p;
				nodeMap.put(p, newNode);
				//check validity of node
				for(MapItem m : obstacles)
					if(m.getPosition().distanceSquared(newNode.p) < Math.pow(unit.getRadius() + m.getRadius(), 2))
						newNode.valid = false;
			}
			
			newNode.disSquared = target.distanceSquared(n.p);
			neighbors.add(newNode);
		}
		
		//now go through the nodes in order of closest to the target
		while(!neighbors.isEmpty())
		{
			Node smallest = neighbors.get(0);
			//get the closest one
			for(int i = 1; i < neighbors.size(); i++)
				if(neighbors.get(i).disSquared < smallest.disSquared)
					smallest = neighbors.get(i);
			neighbors.remove(smallest);
			
			Queue<Position> queue = recursePath(smallest);
			if(!queue.isEmpty())
			{
				queue.add(n.p);
				return queue;
			}
		}
		
		return new LinkedList<Position>();
		
	}

}

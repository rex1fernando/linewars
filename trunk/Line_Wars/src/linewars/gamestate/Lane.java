package linewars.gamestate;

import java.util.ArrayList;
import java.util.Queue;


import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
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
	private ArrayList<Wave> waves;
	private Wave frontlineWave;
	private ArrayList<Node> nodes;
	
	/**
	 * The width of the lane.
	 */
	private double width;
	
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
	
	
	public void mergeWaves(Wave waveOne, Wave waveTwo) throws IllegalArgumentException{
		if(!waves.contains(waveOne) || !waves.contains(waveTwo)){
			throw new IllegalArgumentException("Could not merge waves because one or both of the waves is not in this lane. ");
		}
		waveOne.addUnits(waveTwo.getUnits());
		waves.remove(waveTwo);
	}
	
	public Position getP2()
	{
		return p2;
	}
	
	//TODO
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
		return null;
	}
	
	//TODO
	/**
	 * Gets all map items colliding with the given map item. Uses the isCollidingWith method
	 * in map item to determine collisions.
	 * 
	 * @param m		the item to get collisions with
	 * @return		the list of items colliding with m
	 */
	public MapItem[] getCollisions(MapItem m)
	{
		return null;
	}
	
	/**
	 * 
	 * @param p	the projectile to be added to the lane
	 */
	public void addProjectile(Projectile p)
	{
		
	}

	public Position getP3()
	{
		return p3;
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

}

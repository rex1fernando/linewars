package linewars.gamestate.mapItems;

import java.util.ArrayList;
import java.util.Queue;

import linewars.gamestate.Position;

import linewars.gamestate.Position;

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
	
	/**
	 * The two nodes this lane goes between.
	 */
	private Node node1;
	private Node node2;
	
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
	
	/**
	 * Finds a path as a series of positions from the current position to
	 * the target within the range (i.e. the path doesn't have to get to the
	 * target, it just has to get within range of the target)
	 * 
	 * @param current	the position to start from
	 * @param target	the target position	
	 * @param range		the minimum distance away from the target the path needs to get
	 * @return			a queue of positions that represent the path
	 */
	public Queue<Position> findPath(Position current, Position target, double range)
	{
		return null;
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
	
	public Node getNode1()
	{
		return node1;
	}
	
	public Node getNode2()
	{
		return node2;
	}
}

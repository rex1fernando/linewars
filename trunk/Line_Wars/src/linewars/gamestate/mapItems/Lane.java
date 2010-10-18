package linewars.gamestate.mapItems;

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

	public Position getP2()
	{
		return p2;
	}

	public Position getP3()
	{
		return p3;
	}
	
	public double getWidth()
	{
		return width;
	}
}

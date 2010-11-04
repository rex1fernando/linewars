package linewars.gamestate;

import java.util.Scanner;

public class Position {
	
	private double x;
	private double y;
	
	public Position(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Position(String toParse)
	{
		for(int i = 0; i < toParse.length(); i++)
			if ((toParse.charAt(i) < '0' || toParse.charAt(i) > '9')
					&& toParse.charAt(i) != '.' && toParse.charAt(i) != ' ')
				toParse = toParse.replace(toParse.charAt(i), ' ');
		Scanner s = new Scanner(toParse);
		x = s.nextDouble();
		y = s.nextDouble();
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public Position add(double x, double y)
	{
		return new Position(this.x + x, this.y + y);
	}
	
	public Position add(Position p)
	{
		return add(p.getX(), p.getY());
	}
	
	public Position subtract(double x, double y)
	{
		return add(-x, -y);
	}
	
	public Position subtract(Position p)
	{
		return subtract(p.getX(), p.getY());
	}
	
	public Position scale(double s)
	{
		return new Position(x*s, y*s);
	}
	
	public double distanceSquared(Position p)
	{
		return Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Position)
			return Double.compare(x, ((Position)o).x) == 0 &&
				Double.compare(y, ((Position)o).y) == 0;
		else
			return false;
	}
	
	public Position normalize()
	{
		double length = Math.sqrt((x * x) + (y * y));
		
		return scale(1 / length);
	}

	public Position orthogonal()
	{
		return new Position(y, -x);
	}
	
	public double dot(Position other){
		return x * other.x + y * other.y;
	}
	
	public double scalarProjection(Position axis){
		Position axisHitler = axis.normalize();
		return this.dot(axisHitler);
	}
	
	public Position vectorProjection(Position axis){
		Position axisHitler = axis.normalize();
		return axisHitler.scale(scalarProjection(axis));
	}
	
	@Override
	public int hashCode()
	{
		//TODO how to implement hash code for positions?
		return 0;
	}
}

package linewars.gamestate;

public class Position {
	
	private double x;
	private double y;
	
	public Position(double x, double y)
	{
		this.x = x;
		this.y = y;
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
	
	public Position normalize()
	{
		double length = Math.sqrt((x * x) + (y * y));
		
		return scale(1 / length);
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

}

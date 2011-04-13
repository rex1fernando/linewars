package linewars.gamestate.shapes;

/**
 * Pretty self-explanatory.
 * 
 * @author Rex Fernando
 *
 */
public strictfp class AABB 
{
	private double xMin, yMin, xMax, yMax;
	
	public AABB(double xMin, double yMin, double xMax, double yMax)
	{
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		
		if (xMin > xMax) throw new IllegalArgumentException("xMin > xMax");
		if (yMin > yMax) throw new IllegalArgumentException("yMin > yMax");
	}
	
	public double getXMin()
	{
		return xMin;
	}
	
	public double getXMax()
	{
		return xMax;
	}
	
	public double getYMin()
	{
		return yMin;
	}
	
	public double getYMax()
	{
		return yMax;
	}
	
	public boolean intersectsWith(AABB other){
		return other.xMax > xMin &&
		   other.xMin < xMax &&
		   other.yMax > yMin &&
		   other.yMin < yMax;
	}
}

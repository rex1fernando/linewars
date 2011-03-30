package linewars.gamestate.shapes;

/**
 * Pretty self-explanatory.
 * 
 * @author Rex Fernando
 *
 */
public class AABB 
{
	private double xMin, yMin, xMax, yMax;
	
	public AABB(double xMin, double yMin, double xMax, double yMax)
	{
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
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
}

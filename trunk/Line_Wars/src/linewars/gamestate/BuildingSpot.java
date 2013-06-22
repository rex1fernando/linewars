package linewars.gamestate;

import java.awt.Dimension;
import java.io.Serializable;

import linewars.gamestate.shapes.Rectangle;

public strictfp class BuildingSpot implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1107812883640713531L;
	private Rectangle rect;
	
	public BuildingSpot()
	{
		rect = new Rectangle(new Transformation(new Position(0, 0), 0), 0, 0);
	}
	
	public BuildingSpot(Position p)
	{
		rect = new Rectangle(new Transformation(p, 0), 25, 25);
	}
	
	public void setRect(Rectangle r)
	{
		rect = r;
	}
	
	public void setTrans(Transformation t)
	{
		rect = new Rectangle(t, rect.getWidth(), rect.getHeight());
	}
	
	public void setPos(double x, double y)
	{
		Transformation trans = rect.position();
		rect = new Rectangle(new Transformation(new Position(x, y), trans.getRotation()), rect.getWidth(), rect.getHeight());
	}
	
	public void setRot(double rotation)
	{
		Transformation trans = rect.position();
		rect = new Rectangle(new Transformation(trans.getPosition(), rotation), rect.getWidth(), rect.getHeight());
	}
	
	public void setDim(int width, int height)
	{
		rect = new Rectangle(rect.position(), width, height);
	}
	
	public Rectangle getRect()
	{
		return rect;
	}
	
	public Transformation getTrans()
	{
		return rect.position();
	}
	
	public Dimension getDim()
	{
		return new Dimension((int)rect.getWidth(), (int)rect.getHeight());
	}
	
	@Override
	public String toString()
	{
		return getTrans().getPosition().toString();
	}
}

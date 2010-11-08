package linewars.gamestate;

import java.awt.Dimension;

import linewars.configfilehandler.ConfigData;
import linewars.gamestate.shapes.Rectangle;

public class BuildingSpot
{
	private Rectangle rect;
	
	public BuildingSpot()
	{
		rect = new Rectangle(new Transformation(new Position(0, 0), 0), 0, 0);
	}
	
	public BuildingSpot(ConfigData data)
	{
		rect = new Rectangle(data);
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
}

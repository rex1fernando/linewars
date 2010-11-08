package editor.mapEditor;

import java.awt.Dimension;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

public class BuildingSpot
{
	private Transformation trans;
	private Dimension dim;
	
	public BuildingSpot()
	{
		trans = new Transformation(new Position(0, 0), 0);
		dim = new Dimension(0, 0);
	}
	
	public BuildingSpot(ConfigData data)
	{
		trans = new Transformation(data);
		dim = new Dimension(data.getNumber(ParserKeys.width).intValue(), data.getNumber(ParserKeys.height).intValue());
	}
	
	public void setTrans(Transformation t)
	{
		trans = t;
	}
	
	public void setPos(double x, double y)
	{
		trans = new Transformation(new Position(x, y), trans.getRotation());
	}
	
	public void setRot(double rotation)
	{
		trans = new Transformation(trans.getPosition(), rotation);
	}
	
	public void setDim(int width, int height)
	{
		dim = new Dimension(width, height);
	}
	
	public Transformation getTrans()
	{
		return trans;
	}
	
	public Dimension getDim()
	{
		return dim;
	}
}

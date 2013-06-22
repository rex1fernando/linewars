package linewars.gamestate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.shapes.Circle;
import linewars.gamestate.shapes.Shape;

public strictfp class NodeConfiguration implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2357946522948351831L;
	
	private ArrayList<BuildingSpot> buildingSpots = new ArrayList<BuildingSpot>();
	private Shape shape;
	private boolean isStartNode;
	private BuildingSpot cCenterTransform;
	private ArrayList<LaneConfiguration> attachedLanes = new ArrayList<LaneConfiguration>();
	
	public NodeConfiguration(Position p)
	{
		attachedLanes = new ArrayList<LaneConfiguration>();
		buildingSpots = new ArrayList<BuildingSpot>();
		shape = new Circle(new Transformation(p, 0), 0);
		cCenterTransform = null;
		isStartNode = false;
	}

	public List<BuildingSpot> buildingSpots()
	{
		return buildingSpots;
	}
	
	public Shape getShape()
	{
		return shape;
	}
	
	public boolean isStartNode()
	{
		return isStartNode;
	}
	
	public BuildingSpot getCommandCenterSpot()
	{
		return cCenterTransform;
	}
	
	public List<LaneConfiguration> attachedLanes()
	{
		return attachedLanes;
	}
	
	public void setShape(Shape s)
	{
		shape = s;
	}
	
	public void setStartNode(boolean b)
	{
		isStartNode = b;
	}
	
	public void setCommandCenterSpot(BuildingSpot bs)
	{
		cCenterTransform = bs;
	}
	
	public Node createNode(GameState gameState, int id)
	{
		return new Node(this, gameState, id);
	}
}

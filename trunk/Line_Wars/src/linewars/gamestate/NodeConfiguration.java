package linewars.gamestate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.shapes.Shape;

public class NodeConfiguration implements Serializable {

	private ArrayList<BuildingSpot> buildingSpots = new ArrayList<BuildingSpot>();
	private Shape shape;
	private boolean isStartNode;
	private BuildingSpot cCenterTransform;
	private ArrayList<LaneConfiguration> attachedLanes = new ArrayList<LaneConfiguration>();
	
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
	
//	public Node(Position p, int id)
//	{
//		ID = id;
//		attachedLanes = new ArrayList<Lane>();
//		buildingSpots = new ArrayList<BuildingSpot>();
//		shape = new Circle(new Transformation(p, 0), 0);
//		cCenterTransform = null;
//		isStartNode = false;
//	}
//	
//	public Node(ConfigData parser, Lane[] lanes, int id, boolean force)
//	{
//		ID = id;
//		
//		attachedLanes = new ArrayList<Lane>();
//		List<String> laneNames = new ArrayList<String>();
//		
//		if(parser.getDefinedKeys().contains(ParserKeys.lanes))
//			laneNames = parser.getStringList(ParserKeys.lanes);
//		else if(!force)
//			throw new IllegalArgumentException("No lanes are defined for a node");
//		
//		for(String name : laneNames)
//		{
//			for(Lane l : lanes)
//			{
//				if(name.equals(l.getName()))
//				{
//					attachedLanes.add(l);
//					l.addNode(this);
//				}
//			}
//		}
//		
//		List<ConfigData> transforms = new ArrayList<ConfigData>();
//		if(parser.getDefinedKeys().contains(ParserKeys.buildingSpots))
//			transforms = parser.getConfigList(ParserKeys.buildingSpots);
//		
//		buildingSpots = new ArrayList<BuildingSpot>();
//		for(int i = 0; i < transforms.size(); i++)
//			buildingSpots.add(new BuildingSpot(transforms.get(i)));
//
//		try
//		{
//			shape = Shape.buildFromParser(parser.getConfig(ParserKeys.shape));
//		}
//		catch(IllegalArgumentException e)
//		{
//			if(force)
//				throw new NoSuchKeyException("");
//			else
//				throw e;
//		}
//
//		if(parser.getDefinedKeys().contains(ParserKeys.commandCenterTransformation))
//			cCenterTransform = new BuildingSpot(parser.getConfig(ParserKeys.commandCenterTransformation));
//		else if(force)
//			cCenterTransform = new BuildingSpot(shape.position().getPosition());
//		else
//			throw new IllegalArgumentException("There is no Command Center defined for a node");
//		
//		if(parser.getDefinedKeys().contains(ParserKeys.isStartNode))
//			isStartNode = Boolean.parseBoolean(parser.getString(ParserKeys.isStartNode));
//		else if(force)
//			isStartNode = false;
//		else
//			throw new IllegalArgumentException("There is a node that has not defined if it is a start node");
//	}
}

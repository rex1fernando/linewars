package editor.mapEditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

public strictfp class Node
{
	private ArrayList<Lane> attachedLanes;
	private BuildingSpot[] buildingSpots;
	private BuildingSpot cCenterTransform;
	private Transformation position;
	private double radius;
	private int ID;
	private boolean isStartNode;
	
	private HashMap<Double, Lane> laneMap;
	
	public Node(ConfigData parser, Lane[] lanes, int id)
	{
		ID = id;
		attachedLanes = new ArrayList<Lane>();
		List<String> laneNames = parser.getStringList(ParserKeys.lanes);
		for(String name : laneNames)
		{
			for(Lane l : lanes)
			{
				if(name.equals(l.getName()))
				{
					attachedLanes.add(l);
					l.addNode(this);
				}
			}
		}
		
		List<ConfigData> transforms = parser.getConfigList(ParserKeys.buildingSpots);
		buildingSpots = new BuildingSpot[transforms.size()];
		for(int i = 0; i < transforms.size(); i++)
			buildingSpots[i] = new BuildingSpot(transforms.get(i));

		ConfigData shape = parser.getConfig(ParserKeys.shape);
		radius = shape.getNumber(ParserKeys.radius);
		position = new Transformation(new Position(shape.getNumber(ParserKeys.x), shape.getNumber(ParserKeys.y)), 0);

		laneMap = new HashMap<Double, Lane>();
		
		cCenterTransform = new BuildingSpot(parser.getConfig(ParserKeys.commandCenterTransformation));
		
		isStartNode = Boolean.parseBoolean(parser.getString(ParserKeys.isStartNode));
	}
			
	public Lane[] getAttachedLanes()
	{
		return (Lane[])attachedLanes.toArray();
	}
	
	public BuildingSpot[] getBuildingSpots()
	{
		return buildingSpots;
	}
	
	public BuildingSpot getCommandCenter()
	{
		return cCenterTransform;
	}
	
	/**
	 * Gets the position of the shape that makes up this Node, defined as the center of the Node.
	 * @return a Transformation representing the center of this Node.
	 */
	public Transformation getPosition()
	{
		return position;
	}
	
	public double getRadius()
	{
		return radius;
	}
		
	public boolean isStartNode()
	{
		return isStartNode;
	}
	
	public int getID()
	{
		return ID;
	}
	
	private class Pair<K, V> implements Entry<K, V> {

		private K key;
		private V value;
		
		public Pair(K k, V v)
		{
			key = k;
			value = v;
		}
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			this.value = value;
			return value;
		}
		
	}
}

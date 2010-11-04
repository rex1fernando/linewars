package linewars.gamestate;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

/**
 * 
 * @author John George
 *
 *This class represents the map. It contains information about all of the nodes
 *and lanes of the map and which lanes and which nodes are attached to which lanes.
 */
public class Map {
	private ArrayList<Node> nodes;
	private ArrayList<Lane> lanes;
	private Dimension2D dimensions;
	private ConfigData parser;
	
	
	public Map(GameState gameState, ConfigData mapParser)
	{
		dimensions = new Dimension((int)(double)mapParser.getNumber(ParserKeys.imageWidth), (int)(double)mapParser.getNumber(ParserKeys.imageHeight));
		parser = mapParser;
		
		lanes = new ArrayList<Lane>();
		List<ConfigData> ls = mapParser.getConfigList(ParserKeys.lanes);
		for(ConfigData l : ls)
			lanes.add(new Lane(gameState, l));
		
		nodes = new ArrayList<Node>();
		List<ConfigData> ns = mapParser.getConfigList(ParserKeys.nodes);
		for(ConfigData n : ns)
			nodes.add(new Node(n, gameState, lanes.toArray(new Lane[0]), nodes.size()));
	}
	/**
	 * This method gets a list of the lanes attached to the Node n
	 * @param n The Node in question.
	 * @return A list of the lanes attached to N
	 */
	public Lane[] getLanes(Node n)
	{
		return n.getAttachedLanes();
	}
	
	/**
	 * Return an array containing all of the lanes in the map.
	 * @return An array containing all of the lanes in the map.
	 */
	public Lane[] getLanes()
	{
		return lanes.toArray(new Lane[0]);
	}
	
	/**
	 * Gets a list of the nodes attached to this lane. The size of the array
	 * should be 2 unless later we decide to do crazy n-node lanes.
	 * @param l The lane in question.
	 * @return A list of the nodes this lane connects.
	 */
	public Node[] getNodes(Lane l)
	{
		return l.getNodes();
	}
	
	/**
	 * Return an array containing all of the nodes in the map.
	 * @return An array containing all of the nodes in the map.
	 */
	public Node[] getNodes()
	{
		return nodes.toArray(new Node[0]);
	}
	
	public Dimension2D getDimensions()
	{
		return dimensions;
	}
	
	public String getMapURI()
	{
		return parser.getURI();
	}
	
	/**
	 * 
	 * @return	the parser for the map configuration
	 */
	public ConfigData getParser()
	{
		return parser;
	}
	
	/**
	 * 
	 * @return	the number of nodes players are allowed to start at
	 */
	public int getNumStartNodes()
	{
		int num = 0;
		for(Node n : nodes)
			if(n.isStartNode())
				num++;
		return num;
	}
	
	/**
	 * This method takes in an integer i and returns the ith node
	 * that players are allowed to start on.
	 * 
	 * @param i		the position in the list of the node to start on
	 * @return		the ith start node
	 */
	public Node getStartNode(int i)
	{
		int current = 0;
		for(Node n : nodes)
			if(n.isStartNode())
				if(current == i)
					return n;
				else
					current++;
		return null;
	}
}

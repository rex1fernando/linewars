package linewars.gamestate;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.util.ArrayList;

import linewars.display.MapItemDrawer;
import linewars.parser.Parser;
import linewars.parser.ParserKeys;

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
	private String mapURI;
	
	
	public Map(Parser mapParser, ArrayList<Node> nodes, ArrayList<Lane> lanes)
	{
		dimensions = new Dimension((int)mapParser.getNumericValue(ParserKeys.imageWidth), (int)mapParser.getNumericValue(ParserKeys.imageHeight));
		mapURI = mapParser.getStringValue(ParserKeys.icon);
		
		//load the map image
		try
		{
			MapItemDrawer.getInstance().addImage(mapParser);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.nodes = nodes;
		this.lanes = lanes;
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
		return (Lane[])lanes.toArray();
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
		return (Node[])nodes.toArray();
	}
	
	public Dimension2D getDimensions()
	{
		return dimensions;
	}
	
	public String getMapURI()
	{
		return mapURI;
	}
}

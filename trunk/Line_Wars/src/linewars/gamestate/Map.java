package linewars.gamestate;

import java.util.ArrayList;

/**
 * 
 * @author John George
 *
 *This class represents the map. It contains information about all of the nodes
 *and lanes of the map and which lanes and which nodes are attached to which lanes.
 */
public strictfp class Map {
	private ArrayList<Node> nodes;
	private ArrayList<Lane> lanes;
	private MapConfiguration config;
	
	
	public Map(GameState gameState, MapConfiguration config)
	{
		this.config = config;
		nodes = new ArrayList<Node>();
		int id = 0;
		for(NodeConfiguration nc : config.getNodes())
			nodes.add(nc.createNode(gameState, id++));
		lanes = new ArrayList<Lane>();
		for(LaneConfiguration lc : config.getLanes())
			lanes.add(lc.createLane(gameState));
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
	 * Return an array containing all of the nodes in the map.
	 * @return An array containing all of the nodes in the map.
	 */
	public Node[] getNodes()
	{
		return nodes.toArray(new Node[0]);
	}
	
	/**
	 * 
	 * @return	the dimensions of the map
	 */
	public Position getDimensions()
	{
		return config.getImageSize();
	}
	
	public MapConfiguration getConfig()
	{
		return config;
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
	
	@Override
	public boolean equals(Object o){
		if(o == null) return false;
		if(!(o instanceof Map)) return false;
		Map other = (Map) o;
		if(!other.lanes.equals(lanes)) return false;
		return true;
	}
}

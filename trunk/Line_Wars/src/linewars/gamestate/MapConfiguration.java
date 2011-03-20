package linewars.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import configuration.Configuration;

/**
 * 
 * WARNING: Does not properly implement configuration
 * 
 * @author Connor Schenck
 *
 */
public class MapConfiguration extends Configuration {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3696605874044495962L;
	
	private Position imageSize;
	private String imageURI;
	private ArrayList<NodeConfiguration> nodes = new ArrayList<NodeConfiguration>();
	private ArrayList<LaneConfiguration> lanes = new ArrayList<LaneConfiguration>();
	
	
	public Position getImageSize() {
		return imageSize;
	}
	
	public void setImageSize(Position imageSize) {
		this.imageSize = imageSize;
	}
	
	public String getImageURI() {
		return imageURI;
	}
	
	public void setImageURI(String imageURI) {
		this.imageURI = imageURI;
	}
	
	public List<NodeConfiguration> getNodes()
	{
		return (ArrayList<NodeConfiguration>)nodes.clone();
	}
	
	public List<LaneConfiguration> getLanes()
	{
		return (List<LaneConfiguration>) lanes.clone();
	}
	
	public void setNodes(List<NodeConfiguration> ns)
	{
		nodes.clear();
		nodes.addAll(ns);
	}
	
	public void setLanes(List<LaneConfiguration> ls)
	{
		lanes.clear();
		lanes.addAll(ls);
	}
	
	public Map createMap(GameState gameState)
	{
		Map m = new Map(gameState, this);
		HashMap<LaneConfiguration, Lane> laneHash = new HashMap<LaneConfiguration, Lane>();
		HashMap<NodeConfiguration, Node> nodeHash = new HashMap<NodeConfiguration, Node>();
		for(Lane l : m.getLanes())
			laneHash.put(l.getConfig(), l);
		for(Node n : m.getNodes())
			nodeHash.put(n.getConfig(), n);
		
		for(Node n : m.getNodes())
			for(LaneConfiguration lc : n.getConfig().attachedLanes())
				n.addAttachedLane(laneHash.get(lc));
		
		for(Lane l : m.getLanes())
		{
			l.addNode(nodeHash.get(l.getConfig().getNode(0)));
			l.addNode(nodeHash.get(l.getConfig().getNode(1)));
		}
		
		return m;
	}
	
	public void clearMap()
	{
		nodes.clear();
		lanes.clear();
	}

	@Override
	public String toString()
	{
		return getPropertyForName("bfgName").getValue().toString();
	}
}

package linewars.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import configuration.Configuration;

public class MapConfiguration extends Configuration {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3696605874044495962L;
	
	private Position imageSize;
	private String imageURI;
	private List<NodeConfiguration> nodes = new ArrayList<NodeConfiguration>();
	private List<LaneConfiguration> lanes = new ArrayList<LaneConfiguration>();
	
	
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
	
	public List<NodeConfiguration> nodes()
	{
		return nodes;
	}
	
	public List<LaneConfiguration> lanes()
	{
		return lanes;
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

	@Override
	public String toString()
	{
		return getPropertyForName("bfgName").getValue().toString();
	}
}

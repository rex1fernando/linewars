package linewars.gamestate;

import java.io.Serializable;
import java.util.ArrayList;

public class LaneConfiguration implements Serializable {
	
	private BezierCurve curve;
	private double width;
	private NodeConfiguration[] nodes = new NodeConfiguration[2];
	
	public BezierCurve getBezierCurve()
	{
		return curve;
	}
	
	public double getWidth()
	{
		return width;
	}
	
	public NodeConfiguration getNode(int end)
	{
		return nodes[end];
	}
	
	public void setBezierCurve(BezierCurve c)
	{
		curve = c;
	}
	
	public void setWidth(double w)
	{
		width = w;
	}
	
	public void setNodeConfiguration(NodeConfiguration nc, int end)
	{
		nodes[end] = nc;
	}
	
	public Lane createLane(GameState gameState)
	{
		return new Lane(gameState, this);
	}
	
//	private static int NEXT_UID = 1;
//	public Lane(Node n1, Node n2)
//	{
//		width = 100;
//		name = "lane" + NEXT_UID++;
//		nodes = new ArrayList<Node>();
//		nodes.add(n1);
//		nodes.add(n2);
//		
//		Position n1Pos = n1.getTransformation().getPosition();
//		Position n2Pos = n2.getTransformation().getPosition();
//		
//		//calculate p0
//		Position pointingVec = n2Pos.subtract(n1Pos).normalize();
//		Position p0 = pointingVec.scale(n1.getBoundingCircle().getRadius()).add(n1Pos);
//		
//		//calculate p3
//		pointingVec = pointingVec.scale(-1);
//		Position p3 = pointingVec.scale(n2.getBoundingCircle().getRadius()).add(n2Pos);
//		
//		//set the curve
//		curve = BezierCurve.buildCurve(p0, p3);
//	}
//		
//	public Lane(ConfigData parser, boolean force)
//	{
//		curve = BezierCurve.buildCurve(parser);
//		
//		this.nodes = new ArrayList<Node>();
//		this.name = parser.getString(ParserKeys.name);
//		int id = new Integer(name.substring(4)).intValue();
//		if(id >= NEXT_UID)
//		{
//			NEXT_UID = id + 1;
//		}
//		
//		if(parser.getDefinedKeys().contains(ParserKeys.width))
//			this.width = parser.getNumber(ParserKeys.width);
//		else if(force)
//			this.width = 100;
//		else
//			throw new IllegalArgumentException("The lane width is not defined for lane " + name);
//	}

}

package linewars.gamestate;

import java.io.Serializable;

public class LaneConfiguration implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 844226680065291366L;
	
	private BezierCurve curve;
	private double width;
	private NodeConfiguration[] nodes = new NodeConfiguration[2];
	
	public LaneConfiguration(NodeConfiguration n1, NodeConfiguration n2)
	{
		width = 100;
		nodes = new NodeConfiguration[]{n1, n2};
		
		Position n1Pos = n1.getShape().position().getPosition();
		Position n2Pos = n2.getShape().position().getPosition();
		
		//calculate p0
		Position pointingVec = n2Pos.subtract(n1Pos).normalize();
		Position p0 = pointingVec.scale(n1.getShape().boundingCircle().getRadius()).add(n1Pos);
		
		//calculate p3
		pointingVec = pointingVec.scale(-1);
		Position p3 = pointingVec.scale(n2.getShape().boundingCircle().getRadius()).add(n2Pos);
		
		//set the curve
		curve = BezierCurve.buildCurve(p0, p3);
	}

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
}

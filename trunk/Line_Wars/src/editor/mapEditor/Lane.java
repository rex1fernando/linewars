package editor.mapEditor;

import java.util.ArrayList;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.BezierCurve;
import linewars.gamestate.Transformation;

public strictfp class Lane
{
	private static int NEXT_UID = 1;
	
	private BezierCurve curve;
	private ArrayList<Node> nodes;
	
	/**
	 * The width of the lane.
	 */
	private double width;
	private String name;
			
	public Lane(ConfigData parser)
	{
		curve = BezierCurve.buildCurve(parser);
		
		this.width = parser.getNumber(ParserKeys.width);
		this.nodes = new ArrayList<Node>();
		this.name = parser.getString(ParserKeys.name);
		
		int id = new Integer(name.substring(4)).intValue();
		if(id >= NEXT_UID)
		{
			NEXT_UID = id + 1;
		}
	}
	
	public String getName()
	{
		return name;
	}

	public double getWidth()
	{
		return width;
	}
	
	public Node[] getNodes()
	{
		return nodes.toArray(new Node[0]);
	}
	
	public ArrayList<Node> getNodesList()
	{
		return nodes;
	}

	/**
	 * Gets the position along the bezier curve represented by the percentage
	 * pos. This follows the equation found at
	 * 		<a href="http://en.wikipedia.org/wiki/Bezier_curve#Cubic_B.C3.A9zier_curves">http://en.wikipedia.org/wiki/Bezier_curve</a>
	 * B(t)= (1-t)^3 * P0 + 3(1-t)^2 * t * P1 + 3(1-t) * t^2 * P 2 + t^3 * P3 where t = [0,1].
	 * 
	 * @param pos
	 *            The percentage along the bezier curve to get a position.
	 * 
	 * @return The position along the bezier curve represented by the percentage
	 *         pos.
	 */
	public Transformation getPosition(double pos)
	{
		return curve.getPosition(pos);
	}
		
	public void addNode(Node n)
	{
		if(nodes.size() == 2)
			throw new IllegalArgumentException("Can't add more than 2 nodes to a lane");
		nodes.add(n);
	}
}

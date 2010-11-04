package linewars.display.layers;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.List;

import linewars.display.ColoredEdge;
import linewars.display.ColoredNode;
import linewars.display.Display;
import linewars.gamestate.GameState;
import linewars.gamestate.Lane;
import linewars.gamestate.Node;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.CommandCenter;

public class GraphLayer implements ILayer
{
	private ColoredNode cn;
	private ColoredEdge ce;
	
	public GraphLayer(Display d, int numPlayers)
	{
		cn = new ColoredNode(d, numPlayers);
		ce = new ColoredEdge(d, numPlayers);
	}
	
	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scale)
	{
		//the following code is for testing of the ColoredEdge class
//		Lane l = new Lane(new Position(305, 350), new Position(10, 250), new Position(540, 115), new Position(350, 345), 10);
//		ColoredEdge test = new ColoredEdge(l);
//		test.draw(g);
		//end test code

		//the following code is for testing of the ColoredNode class
//		CommandCenter center = new CommandCenter(new Transformation(new Position(50, 50), 0), null, null);
//		Node n = new Node(null, new Lane[]{}, center, null);
//		ColoredNode testNode = new ColoredNode(n);
//		testNode.draw(g);
		//end test code

		Node[] nodes = gamestate.getMap().getNodes();
		for (Node n : nodes)
		{
//			if (node is visible)
//			{
				cn.draw(g, n, scale);
//			}
		}
		
		Lane[] edges = gamestate.getMap().getLanes();
		for (Lane e : edges)
		{
//			if (edge is visible)
//			{
				ce.draw(g, e, scale);
//			}
		}
	}
	
	/*
	 * private void drawNode(Node n, Graphics g)
	 * {
	 * 	...
	 * }
	 * 
	 * private void drawEdge(Edge n, Graphics g)
	 * {
	 * 	...
	 * }
	 */

}

package linewars.display.layers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import linewars.display.ColoredEdge;
import linewars.gamestate.GameState;
import linewars.gamestate.Lane;
import linewars.gamestate.Position;

public class GraphLayer implements ILayer
{
	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen)
	{
		//the following code is for testing of the ColoredEdge class
		Lane l = new Lane(new Position(205, 350), new Position(310, 110), new Position(340, 115), new Position(450, 345), 10);
		ColoredEdge test = new ColoredEdge(l, Color.blue);
		test.draw(g);
		//end test code
		
		/*
		 * List<Node> nodes = gamestate.getNodes();
		 * for (Node n : nodes)
		 * {
		 * 		if (node is visible)
		 * 		{
		 * 			drawNode(n, g);
		 * 		}
		 * }
		 * 
		 * List<Edges> edges = gamestate.getEdges();
		 * for (Edge e : edges)
		 * {
		 * 		if (edge is visible)
		 * 		{
		 * 			drawEdge(e, g);
		 * 		}
		 * }
		 */
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

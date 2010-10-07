package linewars.display.layers;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import linewars.gamestate.GameState;

public class GraphLayer implements ILayer
{
	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen)
	{
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

package linewars.display.layers;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import linewars.display.ColoredEdge;
import linewars.display.ColoredNode;
import linewars.display.Display;
import linewars.display.FlowIndicator;
import linewars.gamestate.GameState;
import linewars.gamestate.Lane;
import linewars.gamestate.Node;

/**
 * The layer with the colored nodes and edges along with the flow indicators.
 * 
 * @author Ryan Tew
 * 
 */
public class GraphLayer implements ILayer
{
	private ColoredNode cn;
	private ColoredEdge ce;
	private FlowIndicator fi;

	/**
	 * Constructs this graph layer.
	 * 
	 * @param d
	 *            The display this layer will be drawing for.
	 * @param pIndex
	 *            The index of the player the display is for.
	 * @param numPlayers
	 *            The number of players in the game.
	 */
	public GraphLayer(Display d, int pIndex, int numPlayers)
	{
		cn = new ColoredNode(d, numPlayers);
		ce = new ColoredEdge(d, numPlayers);
		fi = new FlowIndicator(d, pIndex);
	}

	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scale)
	{
		// draw the lanes first because they are drawn into the nodes
		Lane[] edges = gamestate.getMap().getLanes();
		for(Lane e : edges)
		{
			ce.draw(g, e, scale);
		}

		// draw the nodes last to cover up the part of the lane that was drawn
		// in the nodes space
		Node[] nodes = gamestate.getMap().getNodes();
		for(Node n : nodes)
		{
			cn.draw(g, n, scale);
		}

		fi.draw(g, gamestate);
	}
}

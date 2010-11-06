package linewars.display.layers;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import linewars.display.ColoredEdge;
import linewars.display.ColoredNode;
import linewars.display.Display;
import linewars.gamestate.GameState;
import linewars.gamestate.Lane;
import linewars.gamestate.Node;

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
		//draw the lanes first because they are drawn into the nodes
		Lane[] edges = gamestate.getMap().getLanes();
		for (Lane e : edges)
		{
			ce.draw(g, e, scale);
		}
		
		//draw the nodes last to cover up the part of the lane that was drawn in the nodes space
		Node[] nodes = gamestate.getMap().getNodes();
		for (Node n : nodes)
		{
			cn.draw(g, n, scale);
		}
	}
}

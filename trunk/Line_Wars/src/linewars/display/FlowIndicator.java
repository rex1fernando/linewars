package linewars.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import linewars.gamestate.BezierCurve;
import linewars.gamestate.GameState;
import linewars.gamestate.Lane;
import linewars.gamestate.Node;
import linewars.gamestate.Player;
import linewars.gamestate.Position;

/**
 * Handles drawing the flow indicators.
 * 
 * @author Ryan Tew
 * 
 */
public class FlowIndicator
{
	private Display display;
	private int playerIndex;

	/**
	 * Constructs this flow indicator.
	 * 
	 * @param d
	 *            The display this FlowIndicator will be drawing for.
	 * @param pIndex
	 *            The player index the display is drawing for.
	 */
	public FlowIndicator(Display d, int pIndex)
	{
		display = d;
		playerIndex = pIndex;
	}

	/**
	 * Draws the colored edges for the player.
	 * 
	 * @param g
	 *            The graphics object to draw on.
	 * @param state
	 *            The gamestate to draw.
	 */
	public void draw(Graphics g, GameState state)
	{
		for(Lane l : state.getMap().getLanes())
		{
			BezierCurve curve = l.getCurve();
			Player p = state.getPlayer(playerIndex);
			Node startNode = p.getStartNode(l);
			double flow = p.getFlowDist(l);

			Position origin1 = display.toScreenCoord(curve.getP0());
			Position origin2 = display.toScreenCoord(curve.getP3());
			Position point1 = display.toScreenCoord(curve.getP0());
			Position point2 = display.toScreenCoord(curve.getP3());

			Node[] nodes = l.getNodes();
			if(startNode == nodes[0])
			{
				Position destination = display.toScreenCoord(curve.getP1());
				Position scale = destination.subtract(origin1).normalize();
				point1 = origin1.add(scale.scale(flow * 2));
			}
			else if(startNode == nodes[1])
			{
				Position destination = display.toScreenCoord(curve.getP2());
				Position scale = destination.subtract(origin2).normalize();
				point2 = origin2.add(scale.scale(flow * 2));
			}

			g.setColor(Color.red);
			((Graphics2D)g).setStroke(new BasicStroke(5));
			g.drawLine((int)origin1.getX(), (int)origin1.getY(), (int)point1.getX(), (int)point1.getY());
			g.drawLine((int)origin2.getX(), (int)origin2.getY(), (int)point2.getX(), (int)point2.getY());

			CircleDrawer.drawCircle(g, point1, 10);
			CircleDrawer.drawCircle(g, point2, 10);
		}
	}
}

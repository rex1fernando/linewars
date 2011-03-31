package linewars.display.layers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import linewars.display.CircleDrawer;
import linewars.display.Display;
import linewars.gamestate.BezierCurve;
import linewars.gamestate.GameState;
import linewars.gamestate.Lane;
import linewars.gamestate.Node;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.network.MessageReceiver;
import linewars.network.messages.AdjustFlowDistributionMessage;

/**
 * Handles drawing the flow indicators.
 * 
 * @author Ryan Tew
 * 
 */
public class FlowIndicator implements ILayer
{
	private Display display;
	private MessageReceiver receiver;
	private int playerIndex;
	private int selectedIndex;
	private boolean adjustingFlow1;
	private Position mouse;

	/**
	 * Constructs this flow indicator.
	 * 
	 * @param d
	 *            The display this FlowIndicator will be drawing for.
	 * @param pIndex
	 *            The player index the display is drawing for.
	 */
	public FlowIndicator(Display d, int pIndex, MessageReceiver receiver)
	{
		this.display = d;
		this.receiver = receiver;
		this.playerIndex = pIndex;
		this.selectedIndex = -1;
		this.mouse = null;
	}
	
	public void setSelectedLane(int lane)
	{
		selectedIndex = lane;
	}
	
	public void setAdjustingFlow1(boolean adjustFlow1)
	{
		adjustingFlow1 = adjustFlow1;
	}
	
	public void setMousePos(Position pos)
	{
		mouse = pos;
	}
	
	public void deselectLane()
	{
		selectedIndex = -1;
	}

	@Override
	public void draw(Graphics g, GameState state, Rectangle2D visibleScreen, double scale)
	{
		Lane[] lanes = state.getMap().getLanes();
		for(int i = 0; i < lanes.length; ++i)
		{
			Lane l = lanes[i];
			BezierCurve curve = l.getCurve();
			Player p = state.getPlayer(playerIndex);
			Node startNode = p.getStartNode(l);
			double flow = p.getFlowDist(l);

			Position origin1 = display.toScreenCoord(curve.getP0());
			Position origin2 = display.toScreenCoord(curve.getP3());
			Position point1 = display.toScreenCoord(curve.getP0());
			Position point2 = display.toScreenCoord(curve.getP3());

			Node[] nodes = l.getNodes();
			if(selectedIndex == i && mouse != null)
			{
				if(adjustingFlow1)
				{
					Position axis = display.toScreenCoord(curve.getP1()).subtract(origin1).normalize();
					Position ray = mouse.subtract(origin1);

					flow = ray.length() * axis.dot(ray.normalize()) / 2;
					if(flow > 100)
						flow = 100;
					if(flow < 0)
						flow = 0;

					startNode = nodes[0];
				}
				else
				{
					Position axis = display.toScreenCoord(curve.getP2()).subtract(origin2).normalize();
					Position ray = mouse.subtract(origin2);

					flow = ray.length() * axis.dot(ray.normalize()) / 2;
					if(flow > 100)
						flow = 100;
					if(flow < 0)
						flow = 0;

					startNode = nodes[1];
				}
				
				receiver.addMessage(new AdjustFlowDistributionMessage(playerIndex, selectedIndex, flow, startNode.getID()));
			}
			
			if(startNode == nodes[0])
			{
				Position destination = display.toScreenCoord(curve.getP1());
				Position direction = destination.subtract(origin1).normalize();
				point1 = origin1.add(direction.scale(flow * 2));
			}
			else if(startNode == nodes[1])
			{
				Position destination = display.toScreenCoord(curve.getP2());
				Position direction = destination.subtract(origin2).normalize();
				point2 = origin2.add(direction.scale(flow * 2));
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

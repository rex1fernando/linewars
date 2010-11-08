package editor.mapEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import linewars.gamestate.Node;
import linewars.gamestate.Position;
import linewars.gamestate.shapes.Circle;

/**
 * This class handles drawing a Nodes. It will draw the Node in the color of the
 * Player that controls it. A "neutral" or "contested" Node is drawn white.
 * 
 * @author Ryan Tew
 * 
 */
public class NodeDrawer
{
	private MapPanel panel;

	/**
	 * Constructs a ColoredNode
	 * 
	 * @param n
	 *            The Node that this ColoredNode will color.
	 * @param numPlayers
	 *            The number of players in the game.
	 */
	public NodeDrawer(MapPanel panel)
	{
		this.panel = panel;
	}

	public void draw(Graphics g, Node node, Position mouse, double scale)
	{
		if(node.isStartNode())
			g.setColor(new Color(0, 255, 0, 80));
		else
			g.setColor(new Color(255, 0, 0, 80));

		double radius = node.getBoundingCircle().getRadius();
		Position centerPos = node.getTransformation().getPosition();
		Position gamePos = new Position(centerPos.getX() - radius, centerPos.getY() - radius);
		Position screenPos = panel.toScreenCoord(gamePos);

		g.fillOval((int)screenPos.getX(), (int)screenPos.getY(), (int)((2 * radius) * scale), (int)((2 * radius) * scale));
		
		if(node.isStartNode())
			g.setColor(new Color(0, 255, 0));
		else
			g.setColor(new Color(255, 0, 0));

		Circle bounds = node.getBoundingCircle();
		Circle outer = new Circle(bounds.position(), bounds.getRadius() + 2.5 / scale);
		Circle inner = new Circle(bounds.position(), bounds.getRadius() - 2.5 / scale);
		if(outer.positionIsInShape(mouse) && !inner.positionIsInShape(mouse))
			((Graphics2D)g).setStroke(new BasicStroke(10));
		else
			((Graphics2D)g).setStroke(new BasicStroke(5));
		
		g.drawOval((int)screenPos.getX(), (int)screenPos.getY(), (int)((2 * radius) * scale), (int)((2 * radius) * scale));
	}
}

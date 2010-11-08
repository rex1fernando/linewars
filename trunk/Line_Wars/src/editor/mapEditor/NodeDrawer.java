package editor.mapEditor;

import java.awt.Color;
import java.awt.Graphics;

import linewars.gamestate.Node;
import linewars.gamestate.Position;

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

	public void draw(Graphics g, Node node, double scale)
	{
		if(node.isStartNode())
			g.setColor(Color.green);
		else
			g.setColor(Color.red);

		double radius = node.getBoundingCircle().getRadius();
		Position centerPos = node.getTransformation().getPosition();
		Position gamePos = new Position(centerPos.getX() - radius, centerPos.getY() - radius);
		Position screenPos = panel.toScreenCoord(gamePos);

		g.fillOval((int)screenPos.getX(), (int)screenPos.getY(), (int)((2 * radius) * scale), (int)((2 * radius) * scale));
	}
}

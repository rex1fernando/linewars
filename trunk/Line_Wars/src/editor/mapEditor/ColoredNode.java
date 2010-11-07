package editor.mapEditor;

import java.awt.Color;
import java.awt.Graphics;

import linewars.gamestate.Position;

/**
 * This class handles drawing a Nodes. It will draw the Node in the color of the
 * Player that controls it. A "neutral" or "contested" Node is drawn white.
 * 
 * @author Ryan Tew
 * 
 */
public class ColoredNode
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
	public ColoredNode(MapPanel panel)
	{
		this.panel = panel;
	}

	public void draw(Graphics g, Node node, double scale)
	{
		g.setColor(Color.red);

		double radius = node.getRadius();
		Position centerPos = node.getPosition().getPosition();
		Position gamePos = new Position(centerPos.getX() - radius, centerPos.getY() - radius);
		Position screenPos = panel.toScreenCoord(gamePos);

		g.fillOval((int)screenPos.getX(), (int)screenPos.getY(), (int)((2 * radius) * scale), (int)((2 * radius) * scale));
	}
}

package linewars.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

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
public class ColoredNode
{
	private Node node;
	private int numPlayers;

	/**
	 * Constructs a ColoredNode
	 * 
	 * @param n
	 *            The Node that this ColoredNode will color.
	 * @param numPlayers
	 *            The number of players in the game.
	 */
	public ColoredNode(Node n, int numPlayers)
	{
		node = n;
		this.numPlayers = numPlayers;
	}

	public void draw(Graphics g)
	{
		if(node.isContested() || node.getOwner() == null)
		{
			g.setColor(Color.white);
		}
		else
		{
			int playerIndex = node.getOwner().getPlayerID();
			Color playerColor = MapItemDrawer.getInstance().getPlayerColor(playerIndex, numPlayers);
			g.setColor(playerColor);
		}

		Position pos = node.getCommandCenter().getPosition();
		double radius = node.getBoundingCircle().getRadius();

		g.fillOval((int)(pos.getX() - radius), (int)(pos.getY() - radius), (int)(2 * radius), (int)(2 * radius));
	}
}

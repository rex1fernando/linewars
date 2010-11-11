package linewars.display;

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
public class ColoredNode
{
	private Display display;
	private int numPlayers;

	/**
	 * Constructs a ColoredNode
	 * 
	 * @param n
	 *            The Node that this ColoredNode will color.
	 * @param numPlayers
	 *            The number of players in the game.
	 */
	public ColoredNode(Display d, int numPlayers)
	{
		display = d;
		this.numPlayers = numPlayers;
	}

	public void draw(Graphics g, Node node, double scale)
	{
		if(node.isContested() || node.getOwner() == null)
		{
			g.setColor(Color.white);
		}
		else
		{
			int playerIndex = node.getOwner().getPlayerID();
			Color playerColor = ImageDrawer.getInstance().getPlayerColor(playerIndex, numPlayers);
			g.setColor(playerColor);
		}

		double radius = node.getBoundingCircle().getRadius();
		Position centerPos = node.getTransformation().getPosition();
		Position screenPos = display.toScreenCoord(centerPos);

		CircleDrawer.drawCircle(g, screenPos, radius * scale);
	}
}

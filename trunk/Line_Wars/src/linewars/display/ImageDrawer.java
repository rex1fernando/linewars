package linewars.display;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.HashMap;

import linewars.gamestate.Position;

/**
 * Encapsulates the drawing of images.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
public class ImageDrawer
{
	private HashMap<Integer, Color> playerColors;
	private HashMap<String, GameImage> images;
	private static ImageDrawer instance;
	private static final Object lock = new Object();

	private ImageDrawer()
	{
		playerColors = new HashMap<Integer, Color>();
		images = new HashMap<String, GameImage>();
	}

	/**
	 * Returns the instance of the singleton MapItemDrawer.
	 * 
	 * @return The instance of the singleton MapItemDrawer.
	 */
	public static ImageDrawer getInstance()
	{
		if(instance == null)
		{
			synchronized(lock)
			{
				if(instance == null)
				{
					instance = new ImageDrawer();
				}
			}
		}

		return instance;
	}

	/**
	 * Adds an image to the MapItemDrawer's repository of images.
	 * 
	 * @param uri
	 *            The URI of the image to load.
	 * @param width
	 *            The width of the image in game units.
	 * @param height
	 *            The height of the image in game units.
	 * @throws IOException
	 *             If an error occurs while reading the image.
	 */
	public void addImage(String uri, int width, int height) throws IOException
	{
		if(images.get(uri + width + height) != null)
			return;

		GameImage scaledImage = new GameImage(uri, width, height);

		images.put(uri + width + height, scaledImage);
	}

	/**
	 * Draws an image to the graphics context.
	 * 
	 * @param g
	 *            The graphics context object to be drawn to.
	 * @param uri
	 *            The URI of the image to be drawn.
	 * @param position
	 *            The position to draw the image.
	 * @param scaleX
	 *            The amount to scale the image on the x-axis.
	 * @param scaleY
	 *            The amount to scale the image on the y-axis.
	 */
	public void draw(Graphics g, String uri, int width, int height, Position position, double scale)
	{
		GameImage image = images.get(uri + width + height);
		if(image == null)
			return;
		
		int x = (int)(position.getX() * scale);
		int y = (int)(position.getY() * scale);
		try
		{
			g.drawImage(image.scaleImage(scale), x, y, null);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void drawAtCenter(Graphics g, String uri, int width, int height, Position position, double scale)
	{
		GameImage image = images.get(uri + width + height);
		if(image == null)
			return;
		
		image.draw(g, position, scale);
	}
	
	public void setPlayerColor(int playerIndex, Color c)
	{
		playerColors.put(playerIndex, c);
	}

	/**
	 * Retrieves the color for the specified player assuming there are
	 * numPlayers players.
	 * 
	 * @param playerIndex
	 *            The zero based index of the player we want the color for.
	 * @param numPlayers
	 *            The number of players in the game.
	 * @return The color for the specified player.
	 */
	public Color getPlayerColor(int playerIndex, int numPlayers)
	{
		Color c = playerColors.get(playerIndex);
//		switch(playerIndex)
//		{
//		case 0:
//			return new Color(140, 23, 23); // scarlet
//		case 1:
//			return Color.blue;
//		case 2:
//			return Color.green;
//		case 3:
//			return Color.orange;
//		case 4:
//			return Color.yellow;
//		case 5:
//			return Color.pink;
//		case 6:
//			return Color.cyan;
//		case 7:
//			return Color.magenta;
//		case 8:
//			return new Color(0, 128, 128); // teal
//		case 9:
//			return new Color(0, 0, 128); // navy
//		case 10:
//			return new Color(0, 245, 255); // turquoise
//		case 11:
//			return new Color(47, 79, 47); // dark green
//		}

		if(c != null)
			return c;
		else
			return Color.white;
	}
}

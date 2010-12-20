package linewars.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import linewars.gamestate.Position;
import linewars.parser.ParserKeys;

/**
 * Encapsulates the drawing of images.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
public class ImageDrawer
{
	private HashMap<String, GameImage> images;
	private static ImageDrawer instance;
	private static final Object lock = new Object();

	private ImageDrawer()
	{
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
	 *            TODO
	 * @param unitURI TODO
	 * @param width
	 *            TODO
	 * @param height
	 *            TODO
	 * @throws IOException
	 *             If an error occurs while reading the image.
	 */
	public void addImage(String uri, String unitURI, int width, int height) throws IOException
	{
		if(images.get(uri + unitURI) != null)
			return;
		
		Image image = loadImage(uri);

		GameImage scaledImage = new GameImage(image, width, height);

		images.put(uri + unitURI, scaledImage);
	}

	public BufferedImage loadImage(String uri) throws IOException
	{
		String absURI = "file:" + System.getProperty("user.dir") + uri.replace("/", File.separator);

		BufferedImage image;
		try
		{
			image = ImageIO.read(new URL(absURI));
		}
		catch (IOException e)
		{
			throw new IOException("Unable to load " + uri + " from the game resources.");
		}
		
		return image;
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
	 * @param rotation
	 *            The rotation of the image.
	 * @param scaleX
	 *            The amount to scale the image on the x-axis.
	 * @param scaleY
	 *            The amount to scale the image on the y-axis.
	 */
	public void draw(Graphics g, String uri, Position position, double rotation, double scale)
	{
		//TODO rotate the image
		GameImage image = images.get(uri);
		int x = (int)(position.getX() * scale);
		int y = (int)(position.getY() * scale);
		g.drawImage(image.scaleImage(scale), x, y, null);
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
	public static Color getPlayerColor(int playerIndex, int numPlayers)
	{
		double mask = Math.log(0xFFFFFF) / Math.log(numPlayers);
		return new Color((int)Math.pow(mask, playerIndex) - 1);
	}
}

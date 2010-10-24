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
import linewars.parser.Parser;
import linewars.parser.ParserKeys;

/**
 * Encapsulates the drawing of images.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
public class MapItemDrawer
{
	private HashMap<String, Image> images;
	private static MapItemDrawer instance;
	private static final Object lock = new Object();

	private MapItemDrawer()
	{
		images = new HashMap<String, Image>();
	}

	/**
	 * Returns the instance of the singleton MapItemDrawer.
	 * 
	 * @return The instance of the singleton MapItemDrawer.
	 */
	public static MapItemDrawer getInstance()
	{
		if(instance == null)
		{
			synchronized(lock)
			{
				if(instance == null)
				{
					instance = new MapItemDrawer();
				}
			}
		}

		return instance;
	}

	/**
	 * Adds an image to the MapItemDrawer's repository of images.
	 * @param parser
	 *            The parser that holds the height and width information from
	 *            the config file.
	 * 
	 * @throws IOException
	 *             If an error occurs while reading the image.
	 */
	public void addImage(Parser parser) throws IOException
	{
		String uri = parser.getStringValue(ParserKeys.icon);
		String absURI = "file:" + System.getProperty("user.dir") + uri.replace("/", File.separator);

		Image image;
		try
		{
			image = ImageIO.read(new URL(absURI));
		}
		catch (IOException e)
		{
			throw new IOException("Unable to load " + uri + " from the game resources.");
		}

		int width = (int)parser.getNumericValue(ParserKeys.imageWidth);
		int height = (int)parser.getNumericValue(ParserKeys.imageHeight);

		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics g = scaledImage.getGraphics();
		g.drawImage(image, 0, 0, width, height, null);

		images.put(uri, scaledImage);
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
	 * @param rotation TODO
	 * @param scaleX TODO
	 * @param scaleY TODO
	 */
	public void draw(Graphics g, String uri, Position position, double rotation, double scaleX, double scaleY)
	{
		Image image = images.get(uri);
		int x = (int) (position.getX() * scaleX);
		int y = (int) (position.getY() * scaleY);
		int w = (int) (image.getWidth(null) * scaleX);
		int h = (int) (image.getHeight(null) * scaleY);
		g.drawImage(image, x, y, w, h, null);
	}
}

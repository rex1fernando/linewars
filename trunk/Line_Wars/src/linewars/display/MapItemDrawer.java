package linewars.display;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import linewars.gamestate.Position;

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
		if (instance == null)
		{
			synchronized(lock)
			{
				if (instance == null)
				{
					instance = new MapItemDrawer();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Adds an image to the MapItemDrawer's repository of images.
	 * 
	 * @param uri The image URI to be loaded from resources.
	 * 
	 * @throws IOException If an error occurs while reading the image.
	 */
	public void addImage(String uri) throws IOException
	{
		Image image;
		try
		{
			image = ImageIO.read(new URL(uri));
		} catch (IOException e)
		{
			throw new IOException("Unable to load " + uri + " from the game resources.");
		}
		
		images.put(uri, image);
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
	 */
	public void draw(Graphics g, String uri, Position position)
	{
		Image image = images.get(uri);
		g.drawImage(image, (int) position.getY(), (int) position.getY(), null);
	}
}

package linewars.display;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import linewars.gamestate.Position;

/**
 * Encapsulates Image scaling.
 * 
 * @author Ryan Tew
 * 
 */
public class GameImage
{
	private double scale;
	private double lastScale;
	private Image originalImage;
	private int originalWidth;
	private int originalHeight;
	private Image lastScaledImage;

	/**
	 * Constructs this game image.
	 * 
	 * @param uri
	 *            The URI of the image.
	 * @throws IOException 
	 */
	public GameImage(String uri) throws IOException
	{
		Image image = loadImage(uri);
		if(image == null)
			throw new IOException(uri + " was not loaded properly from the game resources.");
		
		originalImage = image;
		originalWidth = image.getWidth(null);
		originalHeight = image.getHeight(null);

		scale = -1;
		lastScale = -1;
		lastScaledImage = null;
	}
	
	/**
	 * Constructs this game image.
	 * 
	 * @param image
	 *            The GameImage to copy.
	 * @param width
	 *            The width of the image in game units.
	 * @param height
	 *            The height of the image in game units.
	 * @throws IOException 
	 */
	public GameImage(GameImage image, int width, int height)
	{
		originalImage = image.originalImage;
		originalWidth = image.originalWidth;
		originalHeight = image.originalHeight;

		double scaleX = (double)width / originalWidth;
		double scaleY = (double)height / originalHeight;
		
		if(scaleX < scaleY)
			scale = scaleX;
		else
			scale = scaleY;

		lastScaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = lastScaledImage.getGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);

		lastScale = 1.0;
	}

	/**
	 * Loads the specified image URI.
	 * 
	 * @param uri
	 *            The URI of the image to load.
	 * @return The image stored in the file with the given URI.
	 * @throws IOException
	 *             If the image could not be loaded.
	 */
	public static BufferedImage loadImage(String uri) throws IOException
	{
		String absURI = "File:" + System.getProperty("user.dir") + "/resources/images/" + uri;
		absURI = absURI.replace("/", File.separator);

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
	 * Scales the image. If scale is the same as the last call to this method it
	 * will return the previously returned image. Otherwise the original image
	 * is scaled to the specified scale and that image is stored for future use
	 * and returned.
	 * 
	 * @param scale
	 *            The conversion factor from game units to screen units.
	 * @return The scaled image.
	 * @throws IOException 
	 */
	public Image scaleImage(double scale) throws IOException
	{
		if(scale != lastScale)
		{
			int width = (int)(originalWidth * this.scale * scale);
			int height = (int)(originalHeight * this.scale * scale);

			lastScaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			Graphics g = lastScaledImage.getGraphics();
			g.drawImage(originalImage, 0, 0, width, height, null);

			lastScale = scale;
		}

		return lastScaledImage;
	}
	
	public void draw(Graphics g, Position p, double scale)
	{
		int x = (int)(p.getX() - (originalWidth * this.scale * scale / 2));
		int y = (int)(p.getY() - (originalHeight * this.scale * scale / 2));
		try
		{
			g.drawImage(scaleImage(scale), x, y, null);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Gets the width of the image in game units.
	 * 
	 * @return The width of the image.
	 */
	public int getWidth()
	{
		return (int)(originalWidth * scale);
	}

	/**
	 * Gets the height of the image in game units.
	 * 
	 * @return The height of the image.
	 */
	public int getHeight()
	{
		return (int)(originalHeight * scale);
	}
}

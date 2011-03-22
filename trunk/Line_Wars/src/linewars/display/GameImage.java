package linewars.display;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Encapsulates Image scaling.
 * 
 * @author Ryan Tew
 * 
 */
public class GameImage
{
	private double scaleX;
	private double scaleY;
	private double lastScale;
	private Image originalImage;
	private Image lastScaledImage;

	/**
	 * Constructs this game image.
	 * 
	 * @param image
	 *            The image to encapsulate.
	 * @param width
	 *            The width of the image in game units.
	 * @param height
	 *            The height of the image in game units.
	 * @throws IOException 
	 */
	public GameImage(String uri, int width, int height) throws IOException
	{
		originalImage = loadImage(uri);
		if(originalImage == null)
			throw new IOException(uri + " was not loaded properly from the game resources.");

		scaleX = (double)width / originalImage.getWidth(null);
		scaleY = (double)height / originalImage.getHeight(null);

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
	private BufferedImage loadImage(String uri) throws IOException
	{
		String absURI = "file:" + System.getProperty("user.dir") + "/resources/images/" + uri;
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
	 */
	public Image scaleImage(double scale)
	{
		if(scale != lastScale)
		{
			int width = (int)(originalImage.getWidth(null) * scaleX * scale);
			int height = (int)(originalImage.getHeight(null) * scaleY * scale);

			lastScaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			Graphics g = lastScaledImage.getGraphics();
			g.drawImage(originalImage, 0, 0, width, height, null);

			lastScale = scale;
		}

		return lastScaledImage;
	}

	/**
	 * Gets the width of the image in game units.
	 * 
	 * @param observer
	 *            The image observer.
	 * @return The width of the image.
	 */
	public int getWidth(ImageObserver observer)
	{
		return (int)(originalImage.getWidth(observer) * scaleX);
	}

	/**
	 * Gets the height of the image in game units.
	 * 
	 * @param observer
	 *            The image observer.
	 * @return The height of the image.
	 */
	public int getHeight(ImageObserver observer)
	{
		return (int)(originalImage.getHeight(observer) * scaleY);
	}
}

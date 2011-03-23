package linewars.display;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
//	private byte[] originalImage;
	private int originalWidth;
	private int originalHeight;
//	private Image lastScaledImage;
	private byte[] lastScaledImage;

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
		Image image = loadImage(uri);
		if(image == null)
			throw new IOException(uri + " was not loaded properly from the game resources.");
		
		originalWidth = image.getWidth(null);
		originalHeight = image.getHeight(null);

		scaleX = (double)width / originalWidth;
		scaleY = (double)height / originalHeight;

		originalImage = image;

//		lastScaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//		Graphics g = lastScaledImage.getGraphics();
		Graphics g = temp.getGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ImageIO.write(temp, "png", outStream);
		lastScaledImage = outStream.toByteArray();

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
//		String absURI = System.getProperty("user.dir") + "/resources/images/" + uri;
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
		
//		File file = new File(absURI);
//		FileInputStream fis = new FileInputStream(file);
//		long length = file.length();
//		
//		originalImage = new byte[(int)length];
//		fis.read(originalImage);
//				
//		ByteArrayInputStream inStream = new ByteArrayInputStream(originalImage);
//		BufferedImage image = ImageIO.read(inStream);

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
		BufferedImage temp;
		if(scale != lastScale)
		{
			int width = (int)(originalWidth * scaleX * scale);
			int height = (int)(originalHeight * scaleY * scale);

//			ByteArrayInputStream inStream = new ByteArrayInputStream(originalImage);
//			BufferedImage image = ImageIO.read(inStream);
//			if(image == null)
//				throw new IOException("Could not read byte stream for image");

//			lastScaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

//			Graphics g = lastScaledImage.getGraphics();
			Graphics g = temp.getGraphics();
			g.drawImage(originalImage, 0, 0, width, height, null);
//			g.drawImage(image, 0, 0, width, height, null);
			
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			ImageIO.write(temp, "png", outStream);
			lastScaledImage = outStream.toByteArray();

			lastScale = scale;
		}
		else
		{
			ByteArrayInputStream inStream = new ByteArrayInputStream(lastScaledImage);
			temp = ImageIO.read(inStream);
		}

//		return lastScaledImage;
		return temp;
	}

	/**
	 * Gets the width of the image in game units.
	 * 
	 * @return The width of the image.
	 */
	public int getWidth()
	{
		return (int)(originalWidth * scaleX);
	}

	/**
	 * Gets the height of the image in game units.
	 * 
	 * @return The height of the image.
	 */
	public int getHeight()
	{
		return (int)(originalHeight * scaleY);
	}
}

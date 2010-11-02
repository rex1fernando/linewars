package linewars.display;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class GameImage
{
	private double lastScaleX;
	private double lastScaleY;
	private Image originalImage;
	private Image lastScaledImage;

	public GameImage(Image image, int width, int height)
	{
		originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = originalImage.getGraphics();
		g.drawImage(image, 0, 0, width, height, null);

		lastScaledImage = originalImage;
		lastScaleX = 1.0;
		lastScaleY = 1.0;
	}

	public Image scaleImage(double scaleX, double scaleY)
	{
		if(scaleX != lastScaleX || scaleY != lastScaleY)
		{
			lastScaledImage = new BufferedImage((int)(originalImage.getWidth(null) * scaleX), (int)(originalImage.getHeight(null) * scaleY), BufferedImage.TYPE_INT_ARGB);
			Graphics g = lastScaledImage.getGraphics();
			g.drawImage(originalImage, 0, 0, (int)(originalImage.getWidth(null) * scaleX), (int)(originalImage.getHeight(null) * scaleY), null);
	
			lastScaleX = scaleX;
			lastScaleY = scaleY;
		}
		
		return lastScaledImage;
	}
	
	public int getWidth(ImageObserver observer)
	{
		return originalImage.getWidth(observer);
	}
	
	public int getHeight(ImageObserver observer)
	{
		return originalImage.getHeight(observer);
	}
}

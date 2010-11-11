package linewars.display;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class GameImage
{
	private double scaleX;
	private double scaleY;
	private double lastScale;
	private Image originalImage;
	private Image lastScaledImage;

	public GameImage(Image image, int width, int height)
	{
		originalImage = image;
		
		lastScaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = lastScaledImage.getGraphics();
		g.drawImage(image, 0, 0, width, height, null);

		lastScale = 1.0;
	}

	public Image scaleImage(double scale)
	{
		if(scale != lastScale)
		{
			int width = (int)(originalImage.getWidth(null) * scale);
			int height = (int)(originalImage.getHeight(null) * scale);
			
			lastScaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			
			Graphics g = lastScaledImage.getGraphics();
			g.drawImage(originalImage, 0, 0, width, height, null);
	
			lastScale = scale;
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

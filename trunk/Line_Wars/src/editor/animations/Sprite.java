package editor.animations;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 * @author Connor Schenck
 * 
 * The sprite class represents a sprite. It holds an image of frames for a 
 * sprite and handles drawing them correctly given a row and and column
 * to draw the image for.
 *
 */
public class Sprite {
	
	private static final int FRAME_DELAY = 200;
	
	/** The image to be drawn for this sprite */
	private Image image;
	private BufferedImage buffImage;
	private String string;
	
	/**
	 * Creates a new sprite based off the image.
	 * 
	 * @param imageLocation	the location of the image
	 * @throws IOException 
	 */
	public Sprite(String imageLocation, boolean addPadding) throws IOException {
		string = imageLocation;
		BufferedImage im = null;
		im = ImageIO.read(new File(imageLocation));
		
		if(im == null)
			throw new IOException("The image " + imageLocation + " was not read correctly");
		
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		image = gc.createCompatibleImage(im.getWidth(),im.getHeight(),Transparency.TRANSLUCENT);
		
		// draw our source image into the accelerated image
		image.getGraphics().drawImage(im,0,0,null);
		buffImage = im;
		
		if(addPadding)
		{
			//now make the image bigger so that it can be rotate without losing anything
			int diagonal = (int) Math.sqrt(Math.pow(image.getWidth(null), 2) + Math.pow(image.getHeight(null), 2));
			buffImage = gc.createCompatibleImage(diagonal,diagonal,Transparency.TRANSLUCENT);
			Graphics2D g = (Graphics2D)buffImage.getGraphics();
			
			int x = (diagonal - image.getWidth(null))/2;
			int y = (diagonal - image.getHeight(null))/2;
			g.drawImage(image,x,y, x + image.getWidth(null), y + image.getHeight(null), 0, 0, image.getWidth(null), image.getHeight(null), null);
			
			image = buffImage;
		}
	}
	
	/**
	 * Get the width of the drawn sprite
	 * 
	 * @return The width in pixels of this sprite
	 */
	public int getWidth() {
		return image.getWidth(null);
	}

	/**
	 * Get the height of the drawn sprite
	 * 
	 * @return The height in pixels of this sprite
	 */
	public int getHeight() {
		return image.getHeight(null);
	}
	
	/**
	 * Draws the sprite with its currently set frame in its currently
	 * set row to the given graphics object at x,y with width,height.
	 * 
	 * @param g			the graphics object to draw to
	 * @param x			the x coord
	 * @param y			the y coord
	 * @param width		the width
	 * @param height	the height
	 */
	public void draw(Graphics g,int x,int y, int width, int height) {
		g.drawImage(image,x,y, x + width, y + height, 0, 0, image.getWidth(null), image.getHeight(null), null);
	}
	
	/**
	 * @return the file path to the image for this sprite
	 */
	public String toString()
	{
		return string;
	}
	
	public void rotate(double rot) {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		buffImage = gc.createCompatibleImage(image.getWidth(null),image.getHeight(null),Transparency.TRANSLUCENT);
		Graphics2D g = (Graphics2D)buffImage.getGraphics().create();
		
		g.rotate(rot, image.getWidth(null)/2, image.getHeight(null)/2);
		g.drawImage(image,0,0, image.getWidth(null), image.getHeight(null), 0, 0, image.getWidth(null), image.getHeight(null), null);
		
		image = buffImage;
	}
	
	public void save(String filepath) throws IOException
	{
		filepath = filepath.subSequence(0, filepath.lastIndexOf(".") + 1) + "png";
		File f = new File(filepath);
			
		ImageIO.write(buffImage, "png", f);
	}
}

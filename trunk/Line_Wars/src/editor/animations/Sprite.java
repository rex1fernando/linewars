package editor.animations;


import java.awt.Graphics;
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
	
	/** The current frame of the animation */
	private int frameX1;
	private int frameY1;
	private int frameX2;
	private int frameY2;
	
	private int framesPerRow; //the general number of frames per row (or just number of columns)
	private int framesPerCol;
	private int[] frames; //holds the specific number of frames on each row
	private int[] frameRates;
	
	/**
	 * Creates a new sprite based off the image. The frames per row and
	 * frames per column are set by default to 1.
	 * 
	 * @param imageLocation
	 * @throws IOException 
	 */
	public Sprite(String imageLocation) throws IOException
	{
		this(imageLocation, 1, 1);
	}
	
	/**
	 * Creates a new sprite based off the image.
	 * 
	 * @param imageLocation	the location of the image
	 * @param fPR			the number of frames in a row
	 * @param fPC			the number of frames in a column
	 * @throws IOException 
	 */
	public Sprite(String imageLocation, int fPR, int fPC) throws IOException {
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
		
		framesPerRow = fPR;
		framesPerCol = fPC;
		frameX1 = 0;
		frameY1 = 0;
		frameX2 = image.getWidth(null)/framesPerRow;
		frameY2 = image.getHeight(null)/framesPerCol;
		frames = new int[framesPerCol];
		frameRates = new int[framesPerCol];
		for(int i = 0; i < frames.length; i++)
		{
			frames[i] = framesPerRow;
			frameRates[i] = FRAME_DELAY;
		}
	}
	
	/**
	 * Sets the number of frames in one row
	 * 
	 * @param type		the row to set
	 * @param number	the frames in that row
	 */
	public void setFrames(int type, int number)
	{
		frames[type] = number;
	}
	
	/**
	 * Sets the frame rate delay for a specific row of the sprite.
	 * 
	 * @param type		the row to set the delay for
	 * @param delay		the time between frames in the row in ms
	 */
	public void setFrameRate(int type, int delay)
	{
		frameRates[type] = delay;
	}
	
	/**
	 * gets the frame rate delay for the given row
	 * 
	 * @param type	the row to get the delay for
	 * @return		the delay in ms for that row
	 */
	public int getFrameRate(int type)
	{
		return frameRates[type];
	}
	
	/**
	 * Get the width of the drawn sprite
	 * 
	 * @return The width in pixels of this sprite
	 */
	public int getWidth() {
		return image.getWidth(null)/framesPerRow;
	}

	/**
	 * Get the height of the drawn sprite
	 * 
	 * @return The height in pixels of this sprite
	 */
	public int getHeight() {
		return image.getHeight(null)/framesPerCol;
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
		g.drawImage(image,x,y, x + width, y + height, frameX1, frameY1, frameX2, frameY2, null);
	}
	
	/**
	 * Gets the number of frames in the given row
	 * 
	 * @param type	 the row
	 * @return		 the number of frames in that row
	 */
	public int getNumberFrames(int type)
	{
		return frames[type];
	}
	
	/**
	 * gets the number of rows
	 * 
	 * @return	the number of rows in this sprite
	 */
	public int getNumberTypes()
	{
		return framesPerCol;
	}
	
	/**
	 * sets the row to be used for animation
	 * 
	 * @param t	the row to be used
	 */
	public void setType(int t)
	{
		if(t >= framesPerCol)
			return;
		
		frameY1 = t*image.getHeight(null)/framesPerCol;
		frameY2 = (t + 1)*image.getHeight(null)/framesPerCol;
		
		if(frameX1/getWidth() >= getNumberFrames(t))
		{
			setFrame((frameX1/getWidth())%getNumberFrames(t));
		}
	}
	
	/**
	 * sets the frame of animation
	 * 
	 * @param frame	the frame to set to
	 */
	public void setFrame(int frame)
	{
		frameX1 = frame*image.getWidth(null)/framesPerRow;
		frameX2 = (frame + 1)*image.getWidth(null)/framesPerRow;
	}
	
	/**
	 * Gets the value of the alpha channel at the given x,y int
	 * the current frame in the current row
	 * 
	 * @param x	the x coord
	 * @param y	the y coord
	 * @return	0-255 for the alpha channel
	 */
	public int getAlpha(int x, int y)
	{
		int pixel = buffImage.getRGB(frameX1 + x, frameY1 + y);
		//int alpha = ((pixel & 0xFF000000)/0x800000)&0x000000FF;
		int alpha = (pixel>>24) & 0xff;
		//just in case the rest of the pixels values are ever needed
//		int red = (pixel & 0x00FF0000)/0x8000;
//		int green = (pixel & 0x0000FF00)/0x80;
//		int blue = (pixel & 0x000000FF);
		int  red = (pixel & 0x00ff0000) >> 16;
		int  green = (pixel & 0x0000ff00) >> 8;
		int  blue = pixel & 0x000000ff;
		
		return alpha;
	}
	
	/**
	 * @return the file path to the image for this sprite
	 */
	public String toString()
	{
		return string;
	}
}

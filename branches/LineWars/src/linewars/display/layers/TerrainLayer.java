package linewars.display.layers;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import linewars.display.Display;
import linewars.display.ImageDrawer;
import linewars.gamestate.GameState;
import linewars.gamestate.Position;
import linewars.parser.Parser;
import linewars.parser.ParserKeys;

public class TerrainLayer implements ILayer
{
	private BufferedImage mapImage;
	private BufferedImage displayedImage;
	
	private int width;
	private int height;
	
	private int lastX;
	private int lastY;
	private double lastScale;
	
	public TerrainLayer(String mapURI)
	{
		try
		{
			mapImage = ImageDrawer.getInstance().loadImage(mapURI);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		width = 0;
		height = 0;
		
		lastX = 0;
		lastY = 0;
		lastScale = 1.0;
	}
	
	@Override
	public void draw(Graphics g, Display display, GameState gamestate, Rectangle2D visibleScreen, double scale)
	{
		boolean newDrawImage = false;
		int screenWidth = display.getScreenWidth();
		int screenHeight = display.getScreenHeight();
		if(width != screenWidth || height != screenHeight)
		{
			width = screenWidth;
			height = screenHeight;
			
			displayedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			
			newDrawImage = true;
		}
		
		int x = (int)visibleScreen.getMinX();
		int y = (int)visibleScreen.getMinY();
		int w = (int)visibleScreen.getWidth();
		int h = (int)visibleScreen.getHeight();
		if(newDrawImage || lastX != x || lastY != y || width != w || height != h || lastScale != scale)
		{
			lastX = x;
			lastY = y;
			lastScale = scale;
			
			Parser map = gamestate.getMap().getParser();
			int mapWidth = (int)map.getNumericValue(ParserKeys.imageWidth);
			int mapHeight = (int)map.getNumericValue(ParserKeys.imageHeight);
			
			Image image = mapImage.getSubimage(Math.max(0, x), Math.max(0, y), Math.min(mapWidth, w), Math.min(mapHeight, h));

			Graphics b = displayedImage.getGraphics();
			b.drawImage(image, 0, 0, (int)(image.getWidth(null) * lastScale), (int)(image.getHeight(null) * lastScale), null);
		}
		
		g.drawImage(displayedImage, Math.max((int)(-lastX * lastScale), 0), Math.max((int)(-lastY * lastScale), 0), null);
		
//		String uri = gamestate.getMap().getParser().getStringValue(ParserKeys.icon);
//		ImageDrawer.getInstance().draw(g, uri, new Position(-visibleScreen.getX(), -visibleScreen.getY()), 0.0, scale);
	}
}

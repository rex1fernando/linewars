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
		int w = (int)visibleScreen.getWidth();
		int h = (int)visibleScreen.getHeight();
		if(width != w || height != h)
		{
			width = w;
			height = h;
			displayedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		}
		
		int x = (int)visibleScreen.getMinX();
		int y = (int)visibleScreen.getMinY();
		if(newDrawImage || lastX != x || lastY != y || lastScale != scale)
		{
			Image image = mapImage.getSubimage(x, y, w, h);

			Graphics imageG = displayedImage.getGraphics();
			imageG.drawImage(image, 0, 0, width, height, null);
		}
		
		g.drawImage(displayedImage, 0, 0, null);
		
//		String uri = gamestate.getMap().getParser().getStringValue(ParserKeys.icon);
//		ImageDrawer.getInstance().draw(g, uri, new Position(-visibleScreen.getX(), -visibleScreen.getY()), 0.0, scale);
	}
}

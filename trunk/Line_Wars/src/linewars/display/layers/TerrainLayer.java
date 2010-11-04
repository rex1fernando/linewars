package linewars.display.layers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import linewars.display.Display;
import linewars.display.ImageDrawer;
import linewars.gamestate.GameState;

public class TerrainLayer implements ILayer
{
	private Image map;
	private Image bufferedMap;
	private Rectangle2D lastVisibleScreen;
	private int lastWidth, lastHeight;
	private boolean bufferedChanged;
	private Display display;
	
	public TerrainLayer(String mapURI, Display d)
	{
		try
		{
			map = ImageDrawer.getInstance().loadImage(mapURI);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		display = d;
	}
	
	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scale)
	{
//		int x = (int)(-visibleScreen.getX() * scale);
//		int y = (int)(-visibleScreen.getY() * scale);
//		int w = (int)(map.getWidth(null) * scale);
//		int h = (int)(map.getHeight(null) * scale);
//		g.drawImage(map, x, y, w, h, null);
		
		if (bufferedMap == null || lastWidth != display.getScreenWidth() || lastHeight != display.getScreenHeight())
		{
			bufferedMap = new BufferedImage(display.getScreenWidth(), display.getScreenHeight(), BufferedImage.TYPE_INT_ARGB);
			bufferedChanged = true;
			lastWidth = display.getScreenWidth();
			lastHeight = display.getScreenHeight();
		}
		
		if (bufferedChanged || !visibleScreen.equals(lastVisibleScreen))
		{
			
			int dx1 = 0;
			int dy1 = 0;
			int dx2 = (int) (visibleScreen.getWidth() * scale);
			int dy2 = (int) (visibleScreen.getHeight() * scale);
			int sx1 = (int) visibleScreen.getX();
			int sy1 = (int) visibleScreen.getY();
			int sx2 = (int) (visibleScreen.getX() + visibleScreen.getWidth());
			int sy2 = (int) (visibleScreen.getY() + visibleScreen.getHeight());
			Color bg = Color.black;
			
			Graphics bmg = bufferedMap.getGraphics();
			bmg.setColor(bg);
			bmg.fillRect(0, 0, display.getScreenWidth(), display.getScreenHeight());
			bmg.drawImage(map, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
			
			lastVisibleScreen = new Rectangle2D.Double();
			lastVisibleScreen.setRect(visibleScreen);
			
			bufferedChanged = false;
		}
		
		g.drawImage(bufferedMap, 0, 0, null);
	}
}

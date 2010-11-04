package linewars.display.layers;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import linewars.configfilehandler.ParserKeys;
import linewars.display.ImageDrawer;
import linewars.gamestate.GameState;
import linewars.gamestate.Position;

public class TerrainLayer implements ILayer
{
	private Image map;
	
	public TerrainLayer(String mapURI)
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
	}
	
	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scale)
	{
		int x = (int)(-visibleScreen.getX() * scale);
		int y = (int)(-visibleScreen.getY() * scale);
		int w = (int)(map.getWidth(null) * scale);
		int h = (int)(map.getHeight(null) * scale);
		g.drawImage(map, x, y, w, h, null);
	}
}

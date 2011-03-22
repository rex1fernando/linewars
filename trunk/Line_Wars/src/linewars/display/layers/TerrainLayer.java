package linewars.display.layers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import linewars.display.Display;
import linewars.display.ImageDrawer;
import linewars.gamestate.GameState;

/**
 * Handles drawing the map.
 * 
 * @author Ryan Tew
 * 
 */
public class TerrainLayer implements ILayer
{
	private Image map;
	private Image bufferedMap;
	private Rectangle2D lastVisibleScreen;
	private int lastWidth, lastHeight;
	private boolean bufferedChanged;
	private Display display;

	double scaleX;
	double scaleY;

	/**
	 * Constructs this Terrain Layer.
	 * 
	 * @param mapURI
	 *            The URI of the map that will be drawn.
	 * @param d
	 *            The display that this will be drawing for.
	 * @param mapWidth
	 *            The width of the map in game units.
	 * @param mapHeight
	 *            The height of the map in game units.
	 */
	public TerrainLayer(String mapURI, Display d, double mapWidth, double mapHeight)
	{
		String absURI = "file:" + System.getProperty("user.dir") + "/resources/images/" + mapURI;
		absURI = absURI.replace("/", File.separator);

		try
		{
			map = ImageIO.read(new URL(absURI));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		display = d;

		scaleX = map.getWidth(null) / mapWidth;
		scaleY = map.getHeight(null) / mapHeight;
	}

	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scale)
	{
		if(bufferedMap == null || lastWidth != display.getScreenWidth() || lastHeight != display.getScreenHeight())
		{
			bufferedMap = new BufferedImage(display.getScreenWidth(), display.getScreenHeight(),
					BufferedImage.TYPE_INT_ARGB);
			bufferedChanged = true;
			lastWidth = display.getScreenWidth();
			lastHeight = display.getScreenHeight();
		}

		if(bufferedChanged || !visibleScreen.equals(lastVisibleScreen))
		{

			int dx1 = 0;
			int dy1 = 0;
			int dx2 = display.getScreenWidth();
			int dy2 = display.getScreenHeight();
			int sx1 = (int)(visibleScreen.getX() * scaleX);
			int sy1 = (int)(visibleScreen.getY() * scaleY);
			int sx2 = (int)((visibleScreen.getX() + visibleScreen.getWidth()) * scaleX);
			int sy2 = (int)((visibleScreen.getY() + visibleScreen.getHeight()) * scaleY);
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

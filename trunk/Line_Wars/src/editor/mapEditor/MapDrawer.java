package editor.mapEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import linewars.gamestate.Position;

/**
 * Handles drawing the map to the map panel.
 * 
 * @author Ryan Tew
 * 
 */
public class MapDrawer
{
	private Image map;
	private Image bufferedMap;
	private Rectangle2D lastVisibleScreen;
	private int lastWidth, lastHeight;
	private boolean bufferedChanged;
	private MapPanel panel;

	private double scaleX;
	private double scaleY;

	/**
	 * Constructs this map drawer.
	 * 
	 * @param panel
	 *            The map panel this will draw to.
	 */
	public MapDrawer(MapPanel panel)
	{
		this.panel = panel;
	}

	/**
	 * Sets the map image this will draw.
	 * 
	 * @param mapURI
	 *            The URI of the image.
	 * @return The size of the map image in pixels.
	 */
	public Position setMap(String mapURI)
	{
		if(mapURI == null)
		{
			map = null;
			bufferedMap = null;
			return new Position(100, 100);
		}

		String absURI = "file:" + System.getProperty("user.dir") + "/resources/images/" + mapURI;
		absURI = absURI.replace("/", File.separator);
		
		try
		{
			map = ImageIO.read(new URL(absURI));
			JOptionPane.showMessageDialog(null, "Map file loaded successfuly!", "success",
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Unable to load " + mapURI + " from the game resources!", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		scaleX = 1.0;
		scaleY = 1.0;

		return new Position(map.getWidth(null), map.getHeight(null));
	}

	/**
	 * Sets the map width and height in game size.
	 * 
	 * @param mapWidth
	 *            The map width in game size.
	 * @param mapHeight
	 *            The map height in game size.
	 */
	public void setMapSize(double mapWidth, double mapHeight)
	{
		if(map == null)
		{
			scaleX = 1;
			scaleY = 1;
		}
		else
		{
			scaleX = map.getWidth(null) / mapWidth;
			scaleY = map.getHeight(null) / mapHeight;
		}
	}

	/**
	 * Draws the map.
	 * 
	 * @param g
	 *            The graphics object to draw on.
	 * @param visibleScreen
	 *            The portion of the map that is visible.
	 * @param scale
	 *            The conversion factor from map size to screen size.
	 */
	public void draw(Graphics g, Rectangle2D visibleScreen, double scale)
	{
		if(bufferedMap == null || lastWidth != panel.getWidth() || lastHeight != panel.getHeight())
		{
			bufferedMap = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
			bufferedChanged = true;
			lastWidth = panel.getWidth();
			lastHeight = panel.getHeight();
		}

		if(bufferedChanged || !visibleScreen.equals(lastVisibleScreen))
		{

			int dx1 = 0;
			int dy1 = 0;
			int dx2 = panel.getWidth();
			int dy2 = panel.getHeight();
			int sx1 = (int)(visibleScreen.getX() * scaleX);
			int sy1 = (int)(visibleScreen.getY() * scaleY);
			int sx2 = (int)((visibleScreen.getX() + visibleScreen.getWidth()) * scaleX);
			int sy2 = (int)((visibleScreen.getY() + visibleScreen.getHeight()) * scaleY);
			Color bg = Color.black;

			Graphics bmg = bufferedMap.getGraphics();
			bmg.setColor(bg);
			bmg.fillRect(0, 0, panel.getWidth(), panel.getHeight());
			bmg.drawImage(map, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);

			lastVisibleScreen = new Rectangle2D.Double();
			lastVisibleScreen.setRect(visibleScreen);

			bufferedChanged = false;
		}

		g.drawImage(bufferedMap, 0, 0, null);
	}
}

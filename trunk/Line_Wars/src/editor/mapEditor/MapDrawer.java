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

public class MapDrawer
{
	private Image map;
	private Image bufferedMap;
	private Rectangle2D lastVisibleScreen;
	private int lastWidth, lastHeight;
	private boolean bufferedChanged;
	private MapPanel panel;
	
	public MapDrawer(MapPanel panel)
	{
		this.panel = panel;
	}
	
	public void setMap(String mapURI)
	{
		String absURI = "file:" + System.getProperty("user.dir") + mapURI.replace("/", File.separator);

		try
		{
			map = ImageIO.read(new URL(absURI));
		}
		catch (IOException e)
		{
			//TODO spawn a popup notificatoin
			//throw new IOException("Unable to load " + mapURI + " from the game resources.");
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics g, Rectangle2D visibleScreen, double scale)
	{
		if (bufferedMap == null || lastWidth != panel.getWidth() || lastHeight != panel.getHeight())
		{
			bufferedMap = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
			bufferedChanged = true;
			lastWidth = panel.getWidth();
			lastHeight = panel.getHeight();
		}
		
		if (bufferedChanged || !visibleScreen.equals(lastVisibleScreen))
		{
			
			int dx1 = 0;
			int dy1 = 0;
			int dx2 = panel.getWidth();
			int dy2 = panel.getHeight();
			int sx1 = (int) visibleScreen.getX();
			int sy1 = (int) visibleScreen.getY();
			int sx2 = (int) (visibleScreen.getX() + (panel.getWidth() / scale));
			int sy2 = (int) (visibleScreen.getY() + (panel.getHeight() / scale));
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

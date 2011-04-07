package menu.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import linewars.gamestate.MapConfiguration;

public class MapPanel extends JPanel
{
	private MapConfiguration map;
	private Image image;
	
	public void setMap(MapConfiguration map)
	{
		if (this.map != map)
		{
			this.map = map;
			
			SwingWorker<Image, Object> loader = new ImageLoader();
			loader.execute();
		}
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		paintMapImage(g);
	}
	
	private void paintMapImage(Graphics g)
	{
		if (image != null)
		{
			double thisWidth = getWidth();
			double thisHeight = getHeight();
			double thatWidth = image.getWidth(null);
			double thatHeight = image.getHeight(null);
			
			double thisRatio = thisWidth / thisHeight;
			double thatRatio = thatWidth / thatHeight * 1.0;
			
			double scale = (thisRatio > thatRatio) ? thisHeight / thatHeight : thisWidth / thatWidth;
			
			int imgWidth = (int) (thatWidth * scale);
			int imgHeight = (int) (thatHeight * scale);
			int x = (int) (thisWidth - imgWidth) / 2;
			int y = (int) (thisHeight - imgHeight) / 2;
			
			g.drawImage(image, x, y, imgWidth, imgHeight, null);
		}
	}
	
	private class ImageLoader extends SwingWorker<Image, Object>
	{
		@Override
		protected Image doInBackground() throws Exception {
			return ImageIO.read(new File("resources/images/" + map.getImageURI()));
		}
		
		@Override
		protected void done()
		{
			try {
				image = get();
				repaint();
			} catch (Exception e) {
				// do nothing
			}
		}
	}
}

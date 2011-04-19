package menu.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import linewars.gamestate.MapConfiguration;
import menu.ContentProvider;

public class MapPanel extends JPanel
{
	private static final long serialVersionUID = -9078593187354990077L;
	private int map = 0;
	
	public void setMap(int index)
	{
		this.map = index;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		Image image = ContentProvider.getMapImage(map);
		
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

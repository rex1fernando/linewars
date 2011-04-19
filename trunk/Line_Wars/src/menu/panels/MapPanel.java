package menu.panels;

import java.awt.Graphics;

import javax.swing.JPanel;

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
		g.drawImage(ContentProvider.getMapImage(map), 0, 0, null);
	}
}

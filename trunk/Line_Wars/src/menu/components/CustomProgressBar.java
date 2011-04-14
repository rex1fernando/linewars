package menu.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

import menu.ContentProvider;
import menu.ContentProvider.MenuImage;

public class CustomProgressBar extends JProgressBar
{
	public CustomProgressBar()
	{
		setUI(new CustomPBUI());
		setBorder(null);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = 0.5;
		Dimension size = new Dimension((int) (screenWidth * screenSize.width), 50);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
	}
	
	private class CustomPBUI extends BasicProgressBarUI
	{
		
		@Override
		public void paint(Graphics g, JComponent c)
		{
			Image back = ContentProvider.getImageResource(MenuImage.progressbar_back);
			Image front = ContentProvider.getImageResource(MenuImage.progressbar_front);
			
			double prog =  1.0 * getValue() / getMaximum();
			int dx = (int) (prog * getWidth());
			int sx = (int) (prog * front.getWidth(null));
			
			g.drawImage(back, 0, 0, getWidth(), getHeight(), null);
			g.drawImage(front, 0, 0, dx, getHeight(), 0, 0, sx, front.getHeight(null), null);
		}
	}
}

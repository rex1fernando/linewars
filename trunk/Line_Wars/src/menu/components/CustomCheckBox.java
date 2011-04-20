package menu.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JCheckBox;

public class CustomCheckBox extends JCheckBox
{
	public CustomCheckBox()
	{
		Dimension size = new Dimension(25, 25);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		Icon icon = new CustomIcon();
		setIcon(icon);
		setSelectedIcon(icon);
	}
	
	private class CustomIcon implements Icon
	{
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.black);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			if (isSelected())
			{
				// Image img = ContentProvider.getImageResource(MenuImage.checkbox);
				// g.drawImage(img, x, y, getWidth(), getHeight(), null);
				g.setColor(Color.red);
				g.fillRect(0,0,getWidth(), getHeight());
			}
		}

		@Override
		public int getIconWidth() {
			return getWidth();
		}

		@Override
		public int getIconHeight() {
			return getHeight();
		}
		
	}
}

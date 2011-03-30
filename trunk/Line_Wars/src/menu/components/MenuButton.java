package menu.components;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JButton;

import menu.ContentProvider;
import menu.ContentProvider.MenuImage;

public class MenuButton extends JButton
{
	private String text;
	private Font font;
	
	public MenuButton()
	{
		font = new CustomFont(12);
		text = "";
		setOpaque(false);
		//setPressedIcon(new ButtonIcon(MenuImage.buttonPressed));
		setIcon(new ButtonIcon(MenuImage.buttonDefault));
		//setDisabledIcon(new ButtonIcon(MenuImage.buttonDisabled));
		setRolloverIcon(new ButtonIcon(MenuImage.buttonRollover));
		setBorder(null);
	}
	
	@Override
	public void setText(String text)
	{
		this.text = text;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		
		Point pos = ContentProvider.centerText(fm, text, getWidth(), getHeight());
		g.drawString(text, pos.x, pos.y);
	}
	

	
	private class ButtonIcon implements Icon
	{
		private MenuImage image;
		
		public ButtonIcon(MenuImage img)
		{
			image = img;
		}
		
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Image img = ContentProvider.getImageResource(image);
			g.drawImage(img, x, y, getWidth(), getHeight(), null);
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

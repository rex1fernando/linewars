package menu.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

import linewars.display.sound.SoundPlayer;
import menu.ContentProvider;
import menu.ContentProvider.MenuImage;

public class CustomScrollBar extends JScrollBar
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 736213111629593196L;

	public CustomScrollBar()
	{
		setUI(new CustomScrollBarUI());
	}
	
	private class CustomScrollBarUI extends BasicScrollBarUI
	{
		@Override
		public JButton createIncreaseButton(int o)
		{
			return new ScrollbarButton(SOUTH);
		}
		
		@Override
		public JButton createDecreaseButton(int o)
		{
			return new ScrollbarButton(NORTH);
		}
		
		@Override
		public void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
		{
			Image img = ContentProvider.getImageResource(MenuImage.scrollbar_track);
			g.drawImage(img, trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, null);
		}
		
		@Override
		public void paintThumb(Graphics g, JComponent c, Rectangle trackBounds)
		{
			MenuImage i = (isThumbRollover()) ? MenuImage.scrollbar_highlight : MenuImage.scrollbar_thumb;
			Image img = ContentProvider.getImageResource(i);
			g.drawImage(img, trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, null);
		}
	}
	
	private class ScrollbarButton extends JButton
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2616576965641481610L;
		private int orientation;
		private Color disabledColor;
		
		public ScrollbarButton(int orientation)
		{
			disabledColor = new Color(255, 255, 255, 125);
			this.orientation = orientation;
			setPreferredSize(new Dimension(0, 18));
			setBorder(null);
			addActionListener(SoundPlayer.getInstance().getButtonSoundListener());
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			MenuImage i = (orientation == NORTH) ? MenuImage.scrollbar_incr : MenuImage.scrollbar_decr;
			Image img = ContentProvider.getImageResource(i);
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			
			if (isEnabled() == false)
			{
				g.setColor(disabledColor);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		}
	}
}

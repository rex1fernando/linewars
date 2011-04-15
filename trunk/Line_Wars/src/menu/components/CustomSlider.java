package menu.components;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

import menu.ContentProvider;
import menu.ContentProvider.MenuImage;

public class CustomSlider extends JSlider
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7789228430009816757L;
	private static final int EDGE = 33;
	
	public CustomSlider()
	{
		setUI(new CustomSliderUI(this));
		setOpaque(false);
		setFocusable(false);
	}
	
	private class CustomSliderUI extends BasicSliderUI
	{
		public CustomSliderUI(JSlider b)
		{
			super(b);
		}
		
		@Override
		public void paintTrack(Graphics g)
		{
			Image img = ContentProvider.getImageResource(MenuImage.slider_track);
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
		}
		
		@Override
		public void calculateTrackRect()
		{
			super.calculateTrackRect();
			trackRect.width -= 2*EDGE;
			trackRect.x += EDGE;
		}
		
//		@Override
//		public void calculateThumbLocation()
//		{
//			super.calculateThumbLocation();
//			
//			if (getWidth() != 0)
//				thumbRect.x = (int) (thumbRect.x * ((getWidth() - 2*EDGE) / getWidth() * 1.0) + EDGE);
//		}
		
		@Override
		public void paintThumb(Graphics g)
		{
			Image img = ContentProvider.getImageResource(MenuImage.slider_thumb);
			g.drawImage(img, thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height, null);
		}
	}
}

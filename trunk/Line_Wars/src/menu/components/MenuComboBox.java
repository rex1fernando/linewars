package menu.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;

import menu.ContentProvider;
import menu.ContentProvider.MenuImage;

public class MenuComboBox extends JComboBox
{
	private Font font;
	private Color disabledColor;
	private boolean hideTopLevel;
	
	public MenuComboBox(boolean visible)
	{
		hideTopLevel = visible;
		font = ContentProvider.FONT.deriveFont(12.0f);
		disabledColor = new Color(255, 255, 255, 125);
		
		setUI(new CustomComboBoxUI());
		setRenderer(new ComboBoxRenderer());
		setMaximumRowCount(500);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (hideTopLevel == false)
		{
			Image img = ContentProvider.getImageResource(MenuImage.combobox_main);
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			
			String text = getSelectedItem().toString();
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();
			Point pos = ContentProvider.centerText(fm, text, getWidth() - 28, getHeight());
			g.drawString(text, pos.x, pos.y);
		}
		
		if (isEnabled() == false)
		{
			g.setColor(disabledColor);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
	
	private class ComboButton extends JButton
	{
		public ComboButton()
		{
			setBorder(null);
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			Image img = ContentProvider.getImageResource(MenuImage.combobox_button);
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			
			if (isEnabled() == false)
			{
				g.setColor(disabledColor);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		}
	}
	
	private class CustomComboBoxUI extends BasicComboBoxUI
	{
		@Override
	    protected JButton createArrowButton()
	    {
			return new ComboButton();
	    }
	}
	
	private class ComboBoxRenderer extends BasicComboBoxRenderer {

		private static final long serialVersionUID = 5860676115203832231L;
		
	    private boolean selected = false;
	    private String text = "";

	    @Override
	    public Component getListCellRendererComponent(
	            JList list,
	            Object value,
	            int index,
	            boolean isSelected,
	            boolean cellHasFocus) {

	        selected = isSelected;
	        text = value.toString();
	        return this;
	    }

	    @Override
	    public void paint(Graphics g) {
	    	Image back = ContentProvider.getImageResource(MenuImage.combobox_background);
	    	g.drawImage(back, 0, 0, getWidth(), getHeight(), null);
	    	if (selected) {
	    		Image high = ContentProvider.getImageResource(MenuImage.combobox_highlighted);
	    		g.drawImage(high, 0, 0, getWidth(), getHeight(), null);
	    	}
	    	
	    	g.setFont(font);
	    	g.setColor(Color.black);
			FontMetrics fm = g.getFontMetrics();
			
			Point pos = ContentProvider.centerText(fm, text, getWidth(), getHeight());
			g.drawString(text, pos.x, pos.y);
	    }
	}
}

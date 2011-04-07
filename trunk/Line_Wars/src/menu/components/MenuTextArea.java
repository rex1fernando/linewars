package menu.components;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JTextArea;

import menu.ContentProvider;

public class MenuTextArea extends JTextArea
{
	public static final int OPACITY = 180;
	public static final Color BACKGROUND = new Color(44,61,47, OPACITY);
	public static final Color FOREGROUND = new Color(160,224,171);
	
	public MenuTextArea()
	{
		setOpaque(false);
		setForeground(FOREGROUND);
		setFont(ContentProvider.FONT.deriveFont(12.0f));
		setBorder(null);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(BACKGROUND);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		super.paintComponent(g);
	}
}
 
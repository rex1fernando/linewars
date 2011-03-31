package menu.components;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JTextArea;

public class MenuTextArea extends JTextArea
{
	private static final int OPACITY = 125;
	private static final Color BACKGROUND = new Color(230,239,222, OPACITY);
	private static final Color FOREGROUND = new Color(70,74,42);
	
	public MenuTextArea()
	{
		setOpaque(false);
		setForeground(FOREGROUND);
		setFont(new CustomFont(12));
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(BACKGROUND);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		super.paintComponent(g);
	}
}
 
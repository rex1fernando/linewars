package menu.components;

import java.awt.Graphics;

import javax.swing.JTextField;

import menu.ContentProvider;

public class MenuTextField extends JTextField
{
	public MenuTextField()
	{
		setOpaque(false);
		setForeground(MenuTextArea.FOREGROUND);
		setFont(ContentProvider.FONT.deriveFont(12.0f));
		setBorder(null);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(MenuTextArea.BACKGROUND);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		super.paintComponent(g);
	}
}

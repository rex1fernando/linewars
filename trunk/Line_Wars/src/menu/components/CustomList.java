package menu.components;

import java.awt.Graphics;

import javax.swing.JList;

import menu.ContentProvider;

public class CustomList extends JList
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4827473406658812837L;

	public CustomList()
	{
		setOpaque(false);
		setForeground(MenuTextArea.FOREGROUND);
		setFont(ContentProvider.FONT.deriveFont(12.0f));
		setBorder(null);
	}
	
	public void paintComponent(Graphics g)
	{
		g.setColor(MenuTextArea.BACKGROUND);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		super.paintComponent(g);
	}
}

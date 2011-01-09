package menu;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;

import javax.swing.JFrame;

public class WindowManager extends JFrame
{
	private TitlePanel titleMenu;
	
	public WindowManager()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
	}
	
	public void showWindow()
	{
		setVisible(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		titleMenu = new TitlePanel(this);
		
		setContentPane(titleMenu);
	}
	
	public Dimension getPanelSize()
	{
		return getContentPane().getSize();
	}
	
	public void gotoTitleMenu()
	{
		setContentPane(titleMenu);
	}
	
	public void gotoCreateGame()
	{
		// TODO implement
	}
	
	public void gotoJoinGame()
	{
		// TODO implement
	}
	
	public void gotoEditor()
	{
		// TODO implement
	}
	
	public void gotoCredits()
	{
		// TODO implement
	}
	
	public void exitGame()
	{
		dispose();
	}
	
	public static Point centerText(FontMetrics f, String text, int width, int height)
	{
		int w = f.stringWidth(text);
		int h = f.getAscent();
		
		Point p = new Point();
		p.x = (int) ((width - w) / 2);
		p.y = (int) ((height - h) / 2) + h;
		return p;
	}
}

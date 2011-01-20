package menu;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

import menu.creategame.CreateGamePanel;

public class WindowManager extends JFrame
{
	private TitlePanel titleMenu;
	private CreateGamePanel createMenu;
	
	public WindowManager()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		
		titleMenu = new TitlePanel(this);
		createMenu = new CreateGamePanel(this);
	}
	
	public void showWindow()
	{
		setVisible(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		changeContentPane(titleMenu);
	}
	
	public Dimension getPanelSize()
	{
		return getContentPane().getSize();
	}
	
	public void gotoTitleMenu()
	{
		changeContentPane(titleMenu);
	}
	
	public void gotoCreateGame()
	{
		changeContentPane(createMenu);
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
	
	private void changeContentPane(JPanel pane)
	{
		setContentPane(pane);
		validate();
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

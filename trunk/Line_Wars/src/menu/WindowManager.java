package menu;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

import menu.creategame.CreateGamePanel;

public class WindowManager extends JFrame
{	
	private static final long serialVersionUID = 6285963785850922274L;

	private InnerPanel innerPanel;
	
	private TitlePanel titleMenu;
	private CreateGamePanel createMenu;
	
	public WindowManager()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		titleMenu = new TitlePanel(this);
		createMenu = new CreateGamePanel(this);
		
		innerPanel = new InnerPanel(titleMenu);
		setContentPane(innerPanel);
	}
	
	public void showWindow()
	{
		pack();
		setVisible(true);
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
		createMenu.startServer();

		changeContentPane(createMenu);
	}
	
	public void gotoJoinGame()
	{
		String serverIp = "127.0.0.1";
		
		// TODO get server ip somehow
		
		createMenu.startClient(serverIp);
		changeContentPane(createMenu);
	}
	
	public void gotoEditor()
	{
		// TODO implement
	}
	
	public void gotoOptions()
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
		innerPanel.switchPanel(pane);
		validate();
		innerPanel.repaint();
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
	
	private class InnerPanel extends JPanel
	{
		private static final long serialVersionUID = 2587119152843642036L;
		private JPanel panel;
		
		public InnerPanel(JPanel p)
		{
			panel = p;
			add(panel);
		}
		
		public void switchPanel(JPanel newPanel)
		{
			remove(panel);
			panel = newPanel;
			add(panel);
		}
	}
}

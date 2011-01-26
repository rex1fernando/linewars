package linewars.display.panels;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import linewars.display.Display;
import linewars.gameLogic.GameStateProvider;

public class TechPanel extends Panel
{
	private static final double ASPECT_RATIO = 0.75;

	private static final int DEFAULT_WIDTH = 1500;
	private static final int DEFAULT_HEIGHT = 250;
	
	private Display display;
	
	private JTabbedPane tabPanel;
	
	public TechPanel(Display display, GameStateProvider stateManager)
	{
		super(stateManager, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		this.display = display;
		this.tabPanel = new JTabbedPane();
	}
	
	@Override
	public void updateLocation()
	{
		scaleFactor = (display.getScreenWidth() * ASPECT_RATIO) / DEFAULT_WIDTH;

		super.updateLocation();
		
		setLocation((getParent().getWidth() / 2) - (getWidth() / 2), 0);
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(Color.pink);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		super.paint(g);
	}
}

package linewars.display.panels;

import javax.swing.JPanel;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.CommandCenter;

public class GameNodePanel extends JPanel
{
	private CommandCenter commandCenter;
	private double width, height;
	
	public GameNodePanel(CommandCenter cc)
	{
		commandCenter = cc;
		this.width = width;
		this.height = height;
	}
	
	public void updateLocation(double zoomLevel)
	{
		
	}
}

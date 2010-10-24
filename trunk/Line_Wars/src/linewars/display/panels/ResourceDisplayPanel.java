package linewars.display.panels;

import java.awt.Color;
import java.awt.Graphics;

import linewars.display.Animation;
import linewars.gamestate.GameStateManager;
import linewars.gamestate.Player;

@SuppressWarnings("serial")
public class ResourceDisplayPanel extends Panel
{
	/**
	 * The height and width of the panel
	 */
	private static final int WIDTH = 75;
	private static final int HEIGHT = 25;
	
	private Player player;
	
	public ResourceDisplayPanel(GameStateManager stateManager, Player curPlayer, Animation ... anims)
	{
		super(stateManager, WIDTH, HEIGHT, anims);
		
		player = curPlayer;
	}

	@Override
	public void updateLocation()
	{
		super.updateLocation();

		setLocation(getParent().getWidth() - getWidth(), 0);
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(Color.white);
		g.drawString(Integer.toString(3527), 0, HEIGHT);
//		g.drawString(Integer.toString((int)player.getStuff()), 0, HEIGHT);
	}
}

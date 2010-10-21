package linewars.display.panels;

import linewars.display.Animation;
import linewars.gamestate.GameStateManager;

public class ResourceDisplayPanel extends Panel
{
	private static final int WIDTH = 75;
	private static final int HEIGHT = 25;
	private static final double X_POS = 1 - WIDTH;
	private static final double Y_POS = 0.0;
	
	public ResourceDisplayPanel(GameStateManager stateManager, Animation ... anims)
	{
		super(stateManager, anims);

		setSize(WIDTH, HEIGHT);
	}

	@Override
	public void updateLocation()
	{
		setSize(WIDTH, HEIGHT);
		setLocation(getParent().getWidth() - getWidth(), 0);
	}
}

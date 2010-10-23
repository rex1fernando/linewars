package linewars.display.panels;

import linewars.display.Animation;
import linewars.gamestate.GameStateManager;

@SuppressWarnings("serial")
public class ResourceDisplayPanel extends Panel
{
	/**
	 * The height and width of the panel
	 */
	private static final int WIDTH = 75;
	private static final int HEIGHT = 25;
	
	public ResourceDisplayPanel(GameStateManager stateManager, Animation ... anims)
	{
		super(stateManager, WIDTH, HEIGHT, anims);
	}

	@Override
	public void updateLocation()
	{
		super.updateLocation();

		setLocation(getParent().getWidth() - getWidth(), 0);
	}
}

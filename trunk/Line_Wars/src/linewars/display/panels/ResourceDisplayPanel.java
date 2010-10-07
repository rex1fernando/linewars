package linewars.display.panels;

import linewars.display.Animation;
import linewars.gamestate.GameStateManager;

public class ResourceDisplayPanel extends Panel
{
	private static final double WIDTH = 0.1;
	private static final double HEIGHT = 0.04;
	private static final double X_POS = 1 - WIDTH;
	private static final double Y_POS = 0.0;
	
	public ResourceDisplayPanel(GameStateManager stateManager, Animation ... anims)
	{
		super(stateManager, X_POS, Y_POS, WIDTH, HEIGHT, anims);
	}
}

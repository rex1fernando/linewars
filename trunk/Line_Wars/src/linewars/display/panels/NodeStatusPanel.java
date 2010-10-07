package linewars.display.panels;

import linewars.display.Animation;
import linewars.gamestate.GameStateManager;

public class NodeStatusPanel extends Panel
{
	private static final double WIDTH = 0.3;
	private static final double HEIGHT = 0.2;
	private static final double X_POS = 0.0;
	private static final double Y_POS = 1 - HEIGHT;
	
	public NodeStatusPanel(GameStateManager stateManager, Animation ... anims)
	{
		super(stateManager, X_POS, Y_POS, WIDTH, HEIGHT, anims);
	}
}

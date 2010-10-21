package linewars.display.panels;

import linewars.display.Animation;
import linewars.gamestate.GameStateManager;

public class NodeStatusPanel extends Panel
{
	private static final int WIDTH = 500;
	private static final int HEIGHT = 400;
	private static final double X_POS = 0.0;
	private static final double Y_POS = 1 - HEIGHT;
	
	public NodeStatusPanel(GameStateManager stateManager, Animation ... anims)
	{
		super(stateManager, anims);

		setSize(WIDTH, HEIGHT);
	}

	@Override
	public void updateLocation()
	{
		setSize(WIDTH, HEIGHT);
		setLocation(0, getParent().getHeight() - getHeight());
	}
}

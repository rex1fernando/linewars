package linewars.display.panels;

import linewars.display.Animation;
import linewars.gamestate.GameStateManager;
import linewars.parser.Parser;

@SuppressWarnings("serial")
public class NodeStatusPanel extends Panel
{
	/**
	 * The height and width of the panel
	 */
	private static final int WIDTH = 500;
	private static final int HEIGHT = 400;
	
	public NodeStatusPanel(GameStateManager stateManager, Parser ... anims)
	{
		super(stateManager, WIDTH, HEIGHT, anims);
	}

	@Override
	public void updateLocation()
	{
		super.updateLocation();

		setLocation(0, getParent().getHeight() - getHeight());
	}
}

package linewars.display.panels;

import linewars.gameLogic.GameStateProvider;
import linewars.parser.Parser;

@SuppressWarnings("serial")
public class NodeStatusPanel extends Panel
{
	/**
	 * The height and width of the panel
	 */
	private static final int WIDTH = 500;
	private static final int HEIGHT = 400;
	
	public NodeStatusPanel(GameStateProvider stateManager, Parser ... anims)
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

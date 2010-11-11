package linewars.display.panels;

import linewars.configfilehandler.ConfigData;
import linewars.display.Display;
import linewars.gameLogic.GameStateProvider;

/**
 * Encapsulates the information needed to display Node status information.
 * @author Titus Klinge
 * @author Ryan Tew
 *
 */
@SuppressWarnings("serial")
public class NodeStatusPanel extends Panel
{
	/**
	 * The ratio of the width of this panel to the with of the main display
	 */
	private static final double ASPECT_RATIO = 0.2;
	
	/**
	 * The default height and width of the panel
	 */
	private static final int DEFAULT_WIDTH = 500;
	private static final int DEFAULT_HEIGHT = 400;
	
	private Display display;
	
	/**
	 * Creates this Node Status display.
	 * @param display The display this will be drawn on.
	 * @param stateManager The gamestate manager for this instance of the game.
	 * @param anims The list of animations for this panel.
	 */
	public NodeStatusPanel(Display display, GameStateProvider stateManager, ConfigData ... anims)
	{
		super(stateManager, DEFAULT_WIDTH, DEFAULT_HEIGHT, anims);
		
		this.display = display;
	}

	@Override
	public void updateLocation()
	{
		super.updateLocation();

		scaleFactor = (display.getScreenWidth() * ASPECT_RATIO) / DEFAULT_WIDTH;

		setSize((int)(DEFAULT_WIDTH * scaleFactor), (int)(DEFAULT_HEIGHT * scaleFactor));
		setLocation(0, getParent().getHeight() - getHeight());
	}
}

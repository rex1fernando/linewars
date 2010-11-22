package linewars.display.panels;

import javax.swing.JList;

import linewars.configfilehandler.ConfigData;
import linewars.display.Display;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.Node;

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
	
	/**
	 * The location of the command button panel within the command card
	 */
	private static final int STATUS_PANEL_X = 11;
	private static final int STATUS_PANEL_Y = 123;

	/**
	 * The height and width of the command button panel
	 */
	private static final int STATUS_PANEL_WIDTH = 345;
	private static final int STATUS_PANEL_HEIGHT = 268;

	private Display display;
	private JList nodeStatus;
	
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
		this.nodeStatus = new JList();
		
		//nodeStatus.setOpaque(false);
		
		add(nodeStatus);
	}

	@Override
	public void updateLocation()
	{
		super.updateLocation();

		scaleFactor = (display.getScreenWidth() * ASPECT_RATIO) / DEFAULT_WIDTH;

		setSize((int)(DEFAULT_WIDTH * scaleFactor), (int)(DEFAULT_HEIGHT * scaleFactor));
		setLocation(0, getParent().getHeight() - getHeight());

		// resizes the inner panel
		nodeStatus.setLocation((int)(STATUS_PANEL_X * scaleFactor), (int)(STATUS_PANEL_Y * scaleFactor));
		nodeStatus.setSize((int)(STATUS_PANEL_WIDTH * scaleFactor), (int)(STATUS_PANEL_HEIGHT * scaleFactor));
	}
	
	public void updateNodeStatus(Node node)
	{
		
	}
}

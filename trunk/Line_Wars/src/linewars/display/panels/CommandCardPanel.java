package linewars.display.panels;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import linewars.display.Animation;
import linewars.gamestate.GameStateManager;

/**
 * Encapsulates command card GUI information.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
@SuppressWarnings("serial")
public class CommandCardPanel extends Panel
{
	/**
	 * The height and width of the panel
	 */
	private static final int WIDTH = 500;
	private static final int HEIGHT = 400;
	
	/**
	 * The factor that the image is scaled to in order to fill the panel
	 */
	private static final double scaleFactor = 1.0;
	
	/**
	 * The number of buttons on the command card
	 */
	private static final int NUM_H_BUTTONS = 4;
	private static final int NUM_V_BUTTONS = 3;
	
	/**
	 * The location of the command button panel within the command card
	 */
	private static final int BTN_PANEL_X = 150;
	private static final int BTN_PANEL_Y = 133;
	
	/**
	 * The height and width of the command button panel
	 */
	private static final int BTN_PANEL_WIDTH = 341;
	private static final int BTN_PANEL_HEIGHT = 258;
	
	/**
	 * The gaps between the command buttons
	 */
	private static final int BTN_PANEL_H_GAP = 10;
	private static final int BTN_PANEL_V_GAP = 11;
	
	private JPanel buttonPanel;
	private JButton[] buttons;
	
	/*
	 * TODO actually implement once specified
	 * 
	 * private TechNode prevTechPosition;
	 * private TechNode curTechPosition;
	 * private GameNode selectedNode;
	 */
	
	/**
	 * Creates a new CommandCardPanel object.
	 */
	public CommandCardPanel(GameStateManager stateManager, Animation ... anims)
	{
		super(stateManager, WIDTH, HEIGHT, anims);
				
		buttonPanel = new JPanel(new GridLayout(NUM_V_BUTTONS, NUM_H_BUTTONS, (int)(BTN_PANEL_H_GAP * scaleFactor), (int)(BTN_PANEL_V_GAP * scaleFactor)));
		buttonPanel.setOpaque(false);
		
		buttons = new JButton[NUM_V_BUTTONS * NUM_H_BUTTONS];
		for (int i = 0; i < NUM_V_BUTTONS * NUM_H_BUTTONS; i++)
		{
			buttons[i] = new JButton();
			buttons[i].setVisible(true);
			buttonPanel.add(buttons[i]);
		}
		
		add(buttonPanel);
		validate();
	}
	
	@Override
	public void updateLocation()
	{
		super.updateLocation();
		
		setLocation(getParent().getWidth() - getWidth(), getParent().getHeight() - getHeight());
		
		// resizes the inner panel
		buttonPanel.setLocation((int) (BTN_PANEL_X * scaleFactor), (int) (BTN_PANEL_Y * scaleFactor));
		buttonPanel.setSize((int) (BTN_PANEL_WIDTH * scaleFactor), (int) (BTN_PANEL_HEIGHT * scaleFactor));
	}
	
	/**
	 * Updates the icons, the action, and the tooltip of every
	 * button in the panel.  Also handles disabling buttons if they
	 * are not usable.
	 */
	public void updateButtons()
	{
		/*
		 * TODO actually implement once more specified
		 * 
		 * if (curTechPosition == prevTechPosition)
		 * {
		 * 		return;
		 * }
		 * 
		 * TechNode[] children = curTechPosition.getChildren();
		 * for (int i = 0; i < children.length; i++)
		 * {
		 * 		buttons[i].setIcon(...);
		 * 		buttons[i].setPressedIcon(...);
		 * 		buttons[i].setRolloverIcon(...);
		 * 		buttons[i].setTooltipText(...);
		 * 		buttons[i].setActionListener(...);
		 * 
		 * 		boolean enabled = (is researched yet);
		 * 		button[i].setEnabled(enabled);
		 * }
		 * 
		 * for (int i = children.length; i < buttons.length; i++)
		 * {
		 * 		buttons[i].setVisible(false);
		 * }
		 */
	}
}

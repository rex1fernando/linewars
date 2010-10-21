package linewars.display.panels;

import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import linewars.display.Animation;
import linewars.display.MapItemDrawer;
import linewars.gamestate.GameStateManager;
import linewars.gamestate.Position;

/**
 * Encapsulates command card GUI information.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
public class CommandCardPanel extends Panel
{
	/**
	 * These four variables represent the location and size of the
	 * command card as a percentage of the screen real estate.
	 */
	
	private static final int WIDTH = 500;
	private static final int HEIGHT = 400;
	
	private static final int NUM_H_BUTTONS = 4;
	private static final int NUM_V_BUTTONS = 3;
	
	private static final double BTN_PANEL_X = 0.1;
	private static final double BTN_PANEL_Y = 0.1;
	private static final double BTN_PANEL_WIDTH = 0.8;
	private static final double BTN_PANEL_HEIGHT = 0.8;
	
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
		super(stateManager, anims);
		
		setSize(WIDTH, HEIGHT);
		
		buttonPanel = new JPanel(new GridLayout(NUM_V_BUTTONS, NUM_H_BUTTONS));
		buttonPanel.setOpaque(false);
		
		buttons = new JButton[NUM_V_BUTTONS * NUM_H_BUTTONS];
		for (int i = 0; i < NUM_V_BUTTONS * NUM_H_BUTTONS; i++)
		{
			buttons[i] = new JButton();
			buttons[i].setVisible(false);
			buttonPanel.add(buttons[i]);
		}
		
		add(buttonPanel);
	}
	
	@Override
	public void updateLocation()
	{
		setSize(WIDTH, HEIGHT);
		setLocation(getParent().getWidth() - getWidth(), getParent().getHeight() - getHeight());
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

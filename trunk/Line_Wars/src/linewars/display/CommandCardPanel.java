package linewars.display;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JPanel;

import linewars.gamestate.GameStateManager;

/**
 * Encapsulates command card GUI information.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
public class CommandCardPanel extends JPanel
{
	/**
	 * These four variables represent the location and size of the
	 * command card as a percentage of the screen real estate.
	 */
	private static final double X_POS = 0.7;
	private static final double Y_POS = 0.8;
	private static final double WIDTH = 0.3;
	private static final double HEIGHT = 0.2;
	
	private static final int NUM_H_BUTTONS = 4;
	private static final int NUM_V_BUTTONS = 3;
	
	private static final double BTN_PANEL_X = 0.1;
	private static final double BTN_PANEL_Y = 0.1;
	private static final double BTN_PANEL_WIDTH = 0.8;
	private static final double BTN_PANEL_HEIGHT = 0.8;
	
	private enum ANIMATION { DEFAULT, ROLE_IN, ROLE_OUT }
	
	private Animation curAnimation;
	private Animation[] animations;
	
	private JPanel buttonPanel;
	private JButton[] buttons;
	
	private Container parent;
	private GameStateManager stateManager;
	
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
	public CommandCardPanel(Container parent, GameStateManager stateManager, Animation ... anims)
	{
		super(null);
		
		// check for correct animations
		if (anims == null || anims.length != ANIMATION.values().length)
		{
			throw new IllegalArgumentException("The CommandCardPanel requires exactly " + ANIMATION.values().length + " animations!");
		}
		
		this.parent = parent;
		this.stateManager = stateManager;
		animations = anims;
		curAnimation = anims[ANIMATION.DEFAULT.ordinal()];
		
		buttons = new JButton[NUM_V_BUTTONS * NUM_H_BUTTONS];
		buttonPanel = new JPanel(new GridLayout(NUM_V_BUTTONS, NUM_H_BUTTONS));
		for (int i = 0; i < NUM_V_BUTTONS * NUM_H_BUTTONS; i++)
		{
			buttons[i] = new JButton();
			// buttons[i].setVisible(false);
			buttonPanel.add(buttons[i]);
		}
		add(buttonPanel);
		updateLocation();
	}
	
	/**
	 * Updates the size and location of the panel relative to its parent.  This method
	 * is called when the containing panel is resized.
	 */
	public void updateLocation()
	{
		// resizes the outer panel
		setLocation((int) (X_POS * parent.getWidth()), (int) (Y_POS * parent.getHeight()));
		setSize((int) (WIDTH * parent.getWidth()), (int) (HEIGHT * parent.getHeight()));
		
		// resizes the inner panel
		buttonPanel.setLocation((int) (BTN_PANEL_X * getWidth()), (int) (BTN_PANEL_Y * getHeight()));
		buttonPanel.setSize((int) (BTN_PANEL_WIDTH * getWidth()), (int) (BTN_PANEL_HEIGHT * getHeight()));
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
	
	@Override
	public void paint(Graphics g)
	{
		MapItemDrawer d = MapItemDrawer.getInstance();
		
		//d.draw(g, curAnimation.getImage(stateManager.getDisplayGameState().getTime()), new Point2D.Double(0,0));
		super.paint(g);
	}
}

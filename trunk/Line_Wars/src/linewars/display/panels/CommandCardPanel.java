package linewars.display.panels;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import linewars.display.Animation;
import linewars.display.MapItemDrawer;
import linewars.gamestate.GameStateManager;
import linewars.parser.Parser;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.CommandCenter;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;

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
	private ButtonIcon[] buttonIcons;
	private ClickHandler[] clickEvents;
	
	/**
	 * Creates a new CommandCardPanel object.
	 */
	public CommandCardPanel(GameStateManager stateManager, Parser ... anims)
	{
		super(stateManager, WIDTH, HEIGHT, anims);
				
		buttonPanel = new JPanel(new GridLayout(NUM_V_BUTTONS, NUM_H_BUTTONS, (int)(BTN_PANEL_H_GAP * scaleFactor), (int)(BTN_PANEL_V_GAP * scaleFactor)));
		buttonPanel.setOpaque(false);
		
		buttons = new JButton[NUM_V_BUTTONS * NUM_H_BUTTONS];
		buttonIcons = new ButtonIcon[NUM_V_BUTTONS * NUM_H_BUTTONS];
		clickEvents = new ClickHandler[NUM_V_BUTTONS * NUM_H_BUTTONS];
		for (int i = 0; i < NUM_V_BUTTONS * NUM_H_BUTTONS; i++)
		{
			buttons[i] = new JButton();
			buttons[i].setVisible(false);
			buttonPanel.add(buttons[i]);
			
			buttonIcons[i] = new ButtonIcon();
			buttons[i].setIcon(buttonIcons[i]);
			
			clickEvents[i] = new ClickHandler();
			buttons[i].addActionListener(clickEvents[i]);
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
	public void updateButtons(CommandCenter cc, int nodeId)
	{
		AbilityDefinition[] ad = cc.getAvailableAbilities();
		for (int i = 0; i < ad.length; i++)
		{
			buttons[i].setVisible(true);
			buttonIcons[i].setURI(ad[i].getIconURI());
			buttons[i].setToolTipText(ad[i].getDescription());
			clickEvents[i].setIds(nodeId, ad[i].getID());
			buttons[i].setEnabled(ad[i].unlocked());
		}
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
	
	private class ButtonIcon implements Icon
	{
		private String uri;
		
		public ButtonIcon()
		{
			uri = "";
		}
		
		public void setURI(String newUri)
		{
			uri = newUri;
		}
		
		@Override
		public int getIconHeight()
		{
			return getHeight();
		}

		@Override
		public int getIconWidth()
		{
			return getWidth();
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			MapItemDrawer.getInstance().draw(g, uri, new Position(x, y), 0.0, 1, 1);
		}
	}
	
	private class ClickHandler implements ActionListener
	{
		private int nodeId;
		private int abilityId;
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO implement
			System.out.println("Created a message with node=" + nodeId + " and ability=" + abilityId);
		}
		
		public void setIds(int nodeId, int abilityId)
		{
			this.nodeId = nodeId;
			this.abilityId = abilityId;
		}
	}
}

package linewars.display.panels;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import linewars.display.Display;
import linewars.display.ImageDrawer;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.GameState;
import linewars.gamestate.Node;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.CommandCenter;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.abilities.ConstructBuildingDefinition;
import linewars.gamestate.mapItems.abilities.ResearchTechDefinition;
import linewars.network.MessageReceiver;
import linewars.network.messages.BuildMessage;
import linewars.network.messages.Message;
import linewars.network.messages.UpgradeMessage;
import linewars.parser.Parser;

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
	private CommandButton[] buttons;
	private ButtonIcon[] buttonIcons;
	private ButtonIcon[] pressedIcons;
	private ButtonIcon[] rolloverIcons;
	private ButtonIcon[] selectedIcons;
	private ClickHandler[] clickEvents;
	
	private Display display;
	private MessageReceiver receiver;
	
	/**
	 * Creates a new CommandCardPanel object.
	 * @param disp TODO
	 * @param receiver TODO
	 */
	public CommandCardPanel(Display display, GameStateProvider stateManager, MessageReceiver receiver, Parser ... anims)
	{
		super(stateManager, WIDTH, HEIGHT, anims);
		
		this.display = display;
		this.receiver = receiver;
				
		buttonPanel = new JPanel(new GridLayout(NUM_V_BUTTONS, NUM_H_BUTTONS, (int)(BTN_PANEL_H_GAP * scaleFactor), (int)(BTN_PANEL_V_GAP * scaleFactor)));
		buttonPanel.setOpaque(false);
		
		buttons = new CommandButton[NUM_V_BUTTONS * NUM_H_BUTTONS];
		buttonIcons = new ButtonIcon[NUM_V_BUTTONS * NUM_H_BUTTONS];
		pressedIcons = new ButtonIcon[NUM_V_BUTTONS * NUM_H_BUTTONS];
		rolloverIcons = new ButtonIcon[NUM_V_BUTTONS * NUM_H_BUTTONS];
		selectedIcons = new ButtonIcon[NUM_V_BUTTONS * NUM_H_BUTTONS];
		clickEvents = new ClickHandler[NUM_V_BUTTONS * NUM_H_BUTTONS];
		for (int i = 0; i < NUM_V_BUTTONS * NUM_H_BUTTONS; i++)
		{
			buttons[i] = new CommandButton();
			buttons[i].setOpaque(false);
			buttons[i].setVisible(false);
			buttonPanel.add(buttons[i]);
			
			buttonIcons[i] = new ButtonIcon();
			buttons[i].setIcon(buttonIcons[i]);
			
			pressedIcons[i] = new ButtonIcon();
			buttons[i].setPressedIcon(pressedIcons[i]);

			rolloverIcons[i] = new ButtonIcon();
			buttons[i].setRolloverIcon(rolloverIcons[i]);
			
			selectedIcons[i] = new ButtonIcon();
			buttons[i].setSelectedIcon( selectedIcons[i]);

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
	public void updateButtons(CommandCenter cc, Node node)
	{
		AbilityDefinition[] ad = cc.getAvailableAbilities();
		for (int i = 0; i < ad.length; i++)
		{
			String iconURI = ad[i].getIconURI();
			String pressedURI = ad[i].getPressedIconURI();
			String rolloverURI = ad[i].getRolloverIconURI();
			String selectedURI = ad[i].getSelectedIconURI();
			try
			{
				ImageDrawer.getInstance().addImage(iconURI, "", buttons[i].getWidth(), buttons[i].getHeight());
				ImageDrawer.getInstance().addImage(pressedURI, "", buttons[i].getWidth(), buttons[i].getHeight());
				ImageDrawer.getInstance().addImage(rolloverURI, "", buttons[i].getWidth(), buttons[i].getHeight());
				ImageDrawer.getInstance().addImage(selectedURI, "", buttons[i].getWidth(), buttons[i].getHeight());
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			buttons[i].setVisible(true);
			buttonIcons[i].setURI(iconURI);
			pressedIcons[i].setURI(pressedURI);
			rolloverIcons[i].setURI(rolloverURI);
			selectedIcons[i].setURI(selectedURI);
			buttons[i].setToolTipText(ad[i].getDescription());
			clickEvents[i].setAbility(node, ad[i]);
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
	
	private class CommandButton extends JButton
	{
		@Override
		public void paint(Graphics g)
		{
			DefaultButtonModel model = (DefaultButtonModel) getModel();
			if(model.isPressed())
				getPressedIcon().paintIcon(this, g, 0, 0);
			else if(model.isSelected())
				getSelectedIcon().paintIcon(this, g, 0, 0);
			else if(model.isRollover())
				getRolloverIcon().paintIcon(this, g, 0, 0);
			else
				getIcon().paintIcon(this, g, 0, 0);

		}
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
			ImageDrawer.getInstance().draw(g, uri, new Position(x, y), 0.0, 1);
		}
	}
	
	private class ClickHandler implements ActionListener
	{
		private Node node;
		private AbilityDefinition ability;
		
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO remove debug output
			System.out.println("Created a message with node=" + node + " and ability=" + ability);
			
			Message message = null;
			if(ability instanceof ConstructBuildingDefinition)
			{
				message = new BuildMessage(node.getOwner().getPlayerID(), display.getTimeTick() + 3, node.getID(), ability.getID());
			}
			else if(ability instanceof ResearchTechDefinition)
			{
				message = new UpgradeMessage(node.getOwner().getPlayerID(), display.getTimeTick() + 3, node.getID(), ability.getID());
			}
			
			CommandCardPanel.this.receiver.addMessage(message);
		}
		
		public void setAbility(Node node, AbilityDefinition ability)
		{
			this.node = node;
			this.ability = ability;
		}
	}
}

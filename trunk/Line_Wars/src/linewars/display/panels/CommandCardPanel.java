package linewars.display.panels;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import linewars.display.Animation;
import linewars.display.Display;
import linewars.display.IconConfiguration;
import linewars.display.ImageDrawer;
import linewars.display.IconConfiguration.IconType;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.GameState;
import linewars.gamestate.Node;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.abilities.ConstructBuildingDefinition;
import linewars.network.MessageReceiver;
import linewars.network.messages.BuildMessage;
import linewars.network.messages.Message;
import linewars.network.messages.UpgradeMessage;

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
	 * The ratio of the width of this panel to the with of the main display
	 */
	private static final double ASPECT_RATIO = 0.2;

	/**
	 * The default height and width of the panel
	 */
	private static final int DEFAULT_WIDTH = 500;
	private static final int DEFAULT_HEIGHT = 400;

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
	 * 
	 * @param display
	 *            The display this will be drawn on.
	 * @param stateManager
	 *            The gamestate manager for this instance of the game.
	 * @param receiver
	 *            The message receiver for this instance of the game.
	 * @param anims
	 *            An array of animations to use for this panel.
	 */
	public CommandCardPanel(Display display, int pID, GameStateProvider stateManager, MessageReceiver receiver,
			Animation... anims)
	{
		super(stateManager, DEFAULT_WIDTH, DEFAULT_HEIGHT, anims);

		this.display = display;
		this.receiver = receiver;

		buttonPanel = new JPanel(new GridLayout(NUM_V_BUTTONS, NUM_H_BUTTONS, (int)(BTN_PANEL_H_GAP * scaleFactor),
				(int)(BTN_PANEL_V_GAP * scaleFactor)));
		buttonPanel.setOpaque(false);

		buttons = new CommandButton[NUM_V_BUTTONS * NUM_H_BUTTONS];
		buttonIcons = new ButtonIcon[NUM_V_BUTTONS * NUM_H_BUTTONS];
		pressedIcons = new ButtonIcon[NUM_V_BUTTONS * NUM_H_BUTTONS];
		rolloverIcons = new ButtonIcon[NUM_V_BUTTONS * NUM_H_BUTTONS];
		selectedIcons = new ButtonIcon[NUM_V_BUTTONS * NUM_H_BUTTONS];
		clickEvents = new ClickHandler[NUM_V_BUTTONS * NUM_H_BUTTONS];
		for(int i = 0; i < NUM_V_BUTTONS * NUM_H_BUTTONS; i++)
		{
			buttons[i] = new CommandButton();
			buttons[i].setOpaque(false);
			buttons[i].setVisible(false);
			buttonPanel.add(buttons[i]);

			buttonIcons[i] = new ButtonIcon(buttons[i]);
			buttons[i].setIcon(buttonIcons[i]);

			pressedIcons[i] = new ButtonIcon(buttons[i]);
			buttons[i].setPressedIcon(pressedIcons[i]);

			rolloverIcons[i] = new ButtonIcon(buttons[i]);
			buttons[i].setRolloverIcon(rolloverIcons[i]);

			selectedIcons[i] = new ButtonIcon(buttons[i]);
			buttons[i].setSelectedIcon(selectedIcons[i]);

			clickEvents[i] = new ClickHandler(stateManager.getCurrentGameState().getPlayer(pID).getRace().getAllBuildings());
			buttons[i].addActionListener(clickEvents[i]);
		}

		add(buttonPanel);
		validate();
	}

	@Override
	public void updateLocation()
	{
		scaleFactor = (display.getScreenWidth() * ASPECT_RATIO) / DEFAULT_WIDTH;

		super.updateLocation();

		setLocation(getParent().getWidth() - getWidth(), getParent().getHeight() - getHeight());

		// resizes the inner panel
		buttonPanel.setLayout(new GridLayout(NUM_V_BUTTONS, NUM_H_BUTTONS, (int)(BTN_PANEL_H_GAP * scaleFactor), (int)(BTN_PANEL_V_GAP * scaleFactor)));
		buttonPanel.setLocation((int)(BTN_PANEL_X * scaleFactor), (int)(BTN_PANEL_Y * scaleFactor));
		buttonPanel.setSize((int)(BTN_PANEL_WIDTH * scaleFactor), (int)(BTN_PANEL_HEIGHT * scaleFactor));
	}

	/**
	 * Updates the icons, the action, and the tooltip of every button in the
	 * panel. Also handles disabling buttons if they are not usable.
	 * 
	 * @param cc
	 *            The selected Command Center.
	 * @param node
	 *            The selected Node.
	 */
	public void updateButtons(GameState state, Node node)
	{
		int pID = node.getOwner().getPlayerID();
		List<BuildingDefinition> buildings = state.getPlayer(pID).getRace().getUnlockedBuildings();
		for(int i = 0; i < buildings.size(); ++i)
		{
			BuildingDefinition def = buildings.get(i);
			IconConfiguration icons = def.getIconConfig();

			String iconURI = icons.getIconURI(IconType.regular);
			String pressedURI = icons.getIconURI(IconType.pressed);
			String rolloverURI = icons.getIconURI(IconType.rollover);
			String selectedURI = icons.getIconURI(IconType.highlighted);
			try
			{
				int width = buttons[i].getWidth();
				int height = buttons[i].getHeight();
				ImageDrawer.getInstance().addImage(iconURI, "" + width + height, width, height);
				ImageDrawer.getInstance().addImage(pressedURI, "" + width + height, width, height);
				ImageDrawer.getInstance().addImage(rolloverURI, "" + width + height, width, height);
				ImageDrawer.getInstance().addImage(selectedURI, "" + width + height, width, height);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			buttons[i].setVisible(true);
			buttonIcons[i].setURI(iconURI);
			pressedIcons[i].setURI(pressedURI);
			rolloverIcons[i].setURI(rolloverURI);
			selectedIcons[i].setURI(selectedURI);
			buttons[i].setToolTipText(def.getToolTip());
			clickEvents[i].setAbility(node, def);
			buttons[i].setEnabled(true);
		}
	}

	/**
	 * A button for the command card.
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class CommandButton extends JButton
	{
		@Override
		public void paint(Graphics g)
		{
			DefaultButtonModel model = (DefaultButtonModel)getModel();
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

	/**
	 * An icon for a Command Button
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class ButtonIcon implements Icon
	{
		private JButton button;
		private String uri;

		/**
		 * Constructs the icon.
		 * 
		 * @param b
		 *            The button this icon is on.
		 */
		public ButtonIcon(JButton b)
		{
			button = b;
			uri = "";
		}

		/**
		 * Sets the uri for the icon.
		 * 
		 * @param newUri
		 *            The uri for the icon.
		 */
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
			ImageDrawer.getInstance().draw(g, uri + button.getWidth() + button.getHeight(), new Position(x, y), 1);
		}
	}

	/**
	 * Handles what happens when a Command Button is clicked
	 * 
	 * @author Titus Klinge
	 * 
	 */
	private class ClickHandler implements ActionListener
	{
		private Node node;
		private BuildingDefinition building;
		private List<BuildingDefinition> buildings;
		
		public ClickHandler(List<BuildingDefinition> buildings)
		{
			this.buildings = buildings;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int pID = node.getOwner().getPlayerID();
			int buildingID = buildings.indexOf(building);
			
			Message message = new BuildMessage(pID, node.getID(), buildingID);

			CommandCardPanel.this.receiver.addMessage(message);
		}

		/**
		 * Sets the ability that this click handler will apply.
		 * 
		 * @param node
		 *            The currently selected Node.
		 * @param building
		 *            The definition of the ability that this will apply.
		 */
		public void setAbility(Node node, BuildingDefinition building)
		{
			this.node = node;
			this.building = building;
		}
	}
}

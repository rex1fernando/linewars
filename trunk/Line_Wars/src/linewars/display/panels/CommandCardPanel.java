package linewars.display.panels;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import linewars.display.Animation;
import linewars.display.Display;
import linewars.display.IconConfiguration;
import linewars.display.IconConfiguration.IconType;
import linewars.display.ImageDrawer;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.GameState;
import linewars.gamestate.Node;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.playerabilities.PlayerAbility;
import linewars.network.MessageReceiver;
import linewars.network.messages.BuildMessage;
import linewars.network.messages.DestroyMessage;
import linewars.network.messages.Message;

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
	private static final int DEFAULT_WIDTH = 486;
	private static final int DEFAULT_HEIGHT = 374;
	
	private static final int TOGGLE_PANEL_X = 0;
	private static final int TOGGLE_PANEL_Y = 80;
	
	private static final int TOGGLE_PANEL_WIDTH = 80;
	private static final int TOGGLE_PANEL_HEIGHT = 200;
	
	private static final int NUM_ACTIVE_ABILITIES = 3;
	
	private static final int ABILITY_HEIGHT = 75;
	
	private static final int ABILITY_PANEL_X = 90;
	private static final int ABILITY_PANEL_Y = 0;
	
	private static final int ABILITY_PANEL_WIDTH = 386;
	private static final int ABILITY_PANEL_HEIGHT = 70;

	/**
	 * The number of buttons on the command card
	 */
	private static final int NUM_H_BUTTONS = 4;
	private static final int NUM_V_BUTTONS = 3;

	/**
	 * The location of the command button panel within the command card
	 */
	private static final int BTN_PANEL_X = 90;
	private static final int BTN_PANEL_Y = 80;

	/**
	 * The height and width of the command button panel
	 */
	private static final int BTN_PANEL_WIDTH = 386;
	private static final int BTN_PANEL_HEIGHT = 284;

	/**
	 * The gaps between the command buttons
	 */
	private static final int BTN_H_GAP = 10;
	private static final int BTN_V_GAP = 10;
	
	private JPanel togglePanel;
	private JButton buildButton;
	private JButton destroyButton;
	private boolean buildNotDestroy;

	private JPanel abilityPanel;
	private CommandButton[] activeAbilities;
	private ButtonIcon[] abilityIcons;
	private ButtonIcon[] abilityPressedIcons;
	private ButtonIcon[] abilityRolloverIcons;
	private ButtonIcon[] abilitySelectedIcons;
	private ActiveAbilityHandler[] abilityEvents;
	
	private JPanel buttonPanel;
	private CommandButton[] buttons;
	private ButtonIcon[] buttonIcons;
	private ButtonIcon[] pressedIcons;
	private ButtonIcon[] rolloverIcons;
	private ButtonIcon[] selectedIcons;
	private ClickHandler[] clickEvents;

	private Display display;
	private MessageReceiver receiver;
	private int playerID;
	private boolean displayed;

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
		this.playerID = pID;
		this.displayed = false;
		
		togglePanel = new JPanel(new GridLayout(2, 1));
		buildNotDestroy = true;
		
		buildButton = new JButton("B");
		destroyButton = new JButton("D");
		
		ToggleListener toggle = new ToggleListener();
		buildButton.addActionListener(toggle);
		destroyButton.addActionListener(toggle);
		
		togglePanel.add(buildButton);
		togglePanel.add(destroyButton);
		
		abilityPanel = new JPanel(new GridLayout(1, NUM_ACTIVE_ABILITIES, (int)(BTN_H_GAP * scaleFactor), (int)(BTN_V_GAP * scaleFactor)));
		abilityPanel.setOpaque(false);
		
		activeAbilities = new CommandButton[NUM_ACTIVE_ABILITIES];
		abilityIcons = new ButtonIcon[NUM_ACTIVE_ABILITIES];
		abilityPressedIcons = new ButtonIcon[NUM_ACTIVE_ABILITIES];
		abilityRolloverIcons = new ButtonIcon[NUM_ACTIVE_ABILITIES];
		abilitySelectedIcons = new ButtonIcon[NUM_ACTIVE_ABILITIES];
		abilityEvents = new ActiveAbilityHandler[NUM_ACTIVE_ABILITIES];
		for(int i = 0; i < NUM_ACTIVE_ABILITIES; ++i)
		{
			activeAbilities[i] = new CommandButton();
			activeAbilities[i].setOpaque(false);
			activeAbilities[i].setVisible(false);
			abilityPanel.add(activeAbilities[i]);
			
			abilityIcons[i] = new ButtonIcon(activeAbilities[i]);
			activeAbilities[i].setIcon(abilityIcons[i]);
			
			abilityPressedIcons[i] = new ButtonIcon(activeAbilities[i]);
			activeAbilities[i].setPressedIcon(abilityPressedIcons[i]);
			
			abilityRolloverIcons[i] = new ButtonIcon(activeAbilities[i]);
			activeAbilities[i].setRolloverIcon(abilityRolloverIcons[i]);
			
			abilitySelectedIcons[i] = new ButtonIcon(activeAbilities[i]);
			activeAbilities[i].setSelectedIcon(abilitySelectedIcons[i]);

			abilityEvents[i] = new ActiveAbilityHandler();
			activeAbilities[i].addActionListener(abilityEvents[i]);
		}

		buttonPanel = new JPanel(new GridLayout(NUM_V_BUTTONS, NUM_H_BUTTONS, (int)(BTN_H_GAP * scaleFactor),
				(int)(BTN_V_GAP * scaleFactor)));
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

			clickEvents[i] = new ClickHandler();
			buttons[i].addActionListener(clickEvents[i]);
		}

		add(togglePanel);
		add(abilityPanel);
		add(buttonPanel);
		validate();
	}

	@Override
	public void updateLocation()
	{
		scaleFactor = (display.getScreenWidth() * ASPECT_RATIO) / DEFAULT_WIDTH;

		super.updateLocation();

		if(isDisplayed())
			setLocation(getParent().getWidth() - getWidth(), getParent().getHeight() - getHeight());
		else
			setLocation(getParent().getWidth() - getWidth(), getParent().getHeight() - (int)(scaleFactor * ABILITY_HEIGHT));

		// resizes the inner panels
		togglePanel.setLocation((int)(TOGGLE_PANEL_X * scaleFactor), (int)(TOGGLE_PANEL_Y * scaleFactor));
		togglePanel.setSize((int)(TOGGLE_PANEL_WIDTH * scaleFactor), (int)(TOGGLE_PANEL_HEIGHT * scaleFactor));

		abilityPanel.setLayout(new GridLayout(1, NUM_ACTIVE_ABILITIES, (int)(BTN_H_GAP * scaleFactor), (int)(BTN_V_GAP * scaleFactor)));
		abilityPanel.setLocation((int)(ABILITY_PANEL_X * scaleFactor), (int)(ABILITY_PANEL_Y * scaleFactor));
		abilityPanel.setSize((int)(ABILITY_PANEL_WIDTH * scaleFactor), (int)(ABILITY_PANEL_HEIGHT * scaleFactor));
		
		buttonPanel.setLayout(new GridLayout(NUM_V_BUTTONS, NUM_H_BUTTONS, (int)(BTN_H_GAP * scaleFactor), (int)(BTN_V_GAP * scaleFactor)));
		buttonPanel.setLocation((int)(BTN_PANEL_X * scaleFactor), (int)(BTN_PANEL_Y * scaleFactor));
		buttonPanel.setSize((int)(BTN_PANEL_WIDTH * scaleFactor), (int)(BTN_PANEL_HEIGHT * scaleFactor));
	}
	
	private boolean isDisplayed()
	{
		return displayed;
	}
	
	private void setDisplayed(boolean disp)
	{
		if(displayed == disp)
			return;
		
		displayed = disp;
		updateLocation();
	}
	
	public void setBuildNotDestroy(boolean buildNotDestroy)
	{
		buildButton.setSelected(buildNotDestroy);
		destroyButton.setSelected(!buildNotDestroy);
		this.buildNotDestroy = buildNotDestroy;
	}
	
	private void addIconImage(String uri, int width, int height)
	{
		try
		{
			ImageDrawer.getInstance().addImage(uri, width, height);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
		Player player = state.getPlayer(playerID);
		List<PlayerAbility> allAbilities = player.getAllPlayerAbilities();
		List<PlayerAbility> unlockedAbilities = player.getUnlockedPlayerAbilities();
		for(int i = 0; i < NUM_ACTIVE_ABILITIES; ++i)
		{
			if(i >= unlockedAbilities.size())
			{
				activeAbilities[i].setVisible(false);
				activeAbilities[i].setEnabled(false);
				continue;
			}
			
			PlayerAbility ability = unlockedAbilities.get(i);
			IconConfiguration icons = (IconConfiguration)ability.getIconConfiguration();

			String iconURI = icons.getIconURI(IconType.regular);
			String pressedURI = icons.getIconURI(IconType.pressed);
			String rolloverURI = icons.getIconURI(IconType.rollover);
			String selectedURI = icons.getIconURI(IconType.highlighted);

			int width = activeAbilities[i].getWidth();
			int height = activeAbilities[i].getHeight();
			if(width > 0 && height > 0)
			{
				
				addIconImage(iconURI, width, height);
				addIconImage(pressedURI, width, height);
				addIconImage(rolloverURI, width, height);
				addIconImage(selectedURI, width, height);
			}

			activeAbilities[i].setVisible(true);
			abilityIcons[i].setURI(iconURI);
			abilityPressedIcons[i].setURI(pressedURI);
			abilityRolloverIcons[i].setURI(rolloverURI);
			abilitySelectedIcons[i].setURI(selectedURI);
			activeAbilities[i].setToolTipText(ability.getTooltip());
			abilityEvents[i].setAbility(allAbilities.indexOf(ability));
			activeAbilities[i].setEnabled(true);
		}
		
		if(node == null)
		{
			setDisplayed(false);
			return;
		}
		else
		{
			setDisplayed(true);
		}
		
		List<BuildingDefinition> displayedBuildings;
		if(buildNotDestroy)
		{
			displayedBuildings = player.getRace().getUnlockedBuildings();
		}
		else
		{
			Building[] containedBuildings = node.getContainedBuildings();
			displayedBuildings = new ArrayList<BuildingDefinition>(containedBuildings.length);
			for(int i = 0; i < containedBuildings.length; ++i)
			{
				displayedBuildings.add((BuildingDefinition)containedBuildings[i].getDefinition());
			}
		}
		
		List<BuildingDefinition> allBuildings = player.getRace().getAllBuildings();
		for(int i = 0; i < NUM_V_BUTTONS * NUM_H_BUTTONS; ++i)
		{
			if(i >= displayedBuildings.size())
			{
				buttons[i].setVisible(false);
				buttons[i].setEnabled(false);
				continue;
			}

			BuildingDefinition def = displayedBuildings.get(i);
			IconConfiguration icons = def.getIconConfig();
			
			String iconURI = icons.getIconURI(IconType.regular);
			String pressedURI = icons.getIconURI(IconType.pressed);
			String rolloverURI = icons.getIconURI(IconType.rollover);
			String selectedURI = icons.getIconURI(IconType.highlighted);
			int width = buttons[i].getWidth();
			int height = buttons[i].getHeight();
			if(width > 0 && height > 0)
			{
				
				addIconImage(iconURI, width, height);
				addIconImage(pressedURI, width, height);
				addIconImage(rolloverURI, width, height);
				addIconImage(selectedURI, width, height);
			}
			
			buttons[i].setVisible(true);
			buttonIcons[i].setURI(iconURI);
			pressedIcons[i].setURI(pressedURI);
			rolloverIcons[i].setURI(rolloverURI);
			selectedIcons[i].setURI(selectedURI);
			buttons[i].setToolTipText(def.getToolTip());
			clickEvents[i].setAbility(node.getID(), allBuildings.indexOf(def));
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
			Icon pressedIcon = getPressedIcon();
			Icon selectedIcon = getSelectedIcon();
			Icon rolloverIcon = getRolloverIcon();
			Icon icon = getIcon();

			DefaultButtonModel model = (DefaultButtonModel)getModel();
			if(model.isPressed())
			{
				if(pressedIcon != null)
					pressedIcon.paintIcon(this, g, 0, 0);
			}
			else if(model.isSelected())
			{
				if(selectedIcon != null)
					selectedIcon.paintIcon(this, g, 0, 0);
			}
			else if(model.isRollover())
			{
				if(rolloverIcon != null)
					rolloverIcon.paintIcon(this, g, 0, 0);
			}
			else
			{
				if(icon != null)
					icon.paintIcon(this, g, 0, 0);
			}
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
		
		public String getURI()
		{
			return uri;
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
			ImageDrawer.getInstance().draw(g, uri, button.getWidth(), button.getHeight(), new Position(x, y), 1);
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
		private int nodeID;
		private int buildingID;
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Message message;
			
			if(buildNotDestroy)
				message = new BuildMessage(playerID, nodeID, buildingID);
			else
				message = new DestroyMessage(playerID, nodeID, buildingID);

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
		public void setAbility(int nodeID, int buildingID)
		{
			this.nodeID = nodeID;
			this.buildingID = buildingID;
		}
	}
	
	private class ActiveAbilityHandler implements ActionListener
	{
		private int abilityID;
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			display.setActiveAbilityIndex(abilityID);
		}
		
		private void setAbility(int abilityID)
		{
			this.abilityID = abilityID;
		}
	}
	
	private class ToggleListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			if(source == buildButton)
			{
				setBuildNotDestroy(true);
			}
			else if(source == destroyButton)
			{
				setBuildNotDestroy(false);
			}
		}
	}
}

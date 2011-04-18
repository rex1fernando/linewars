package linewars.display.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import linewars.display.sound.SoundPlayer;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.GameState;
import linewars.gamestate.Node;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.BuildingDefinition;
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
	private static final int ENERGY_COLOR = 0x00FFFF;
	
	/**
	 * The ratio of the width of this panel to the with of the main display
	 */
	private static final double ASPECT_RATIO = 0.2;

	/**
	 * The default height and width of the panel
	 */
	private static final int DEFAULT_WIDTH = 650;
	private static final int DEFAULT_HEIGHT = 550;
	
	private static final int BUILD_BUTTON_X = 4;
	private static final int BUILD_BUTTON_Y = 264;
	
	private static final int BUILD_BUTTON_WIDTH = 47;
	private static final int BUILD_BUTTON_HEIGHT = 111;
	
	private static final int DESTROY_BUTTON_X = 4;
	private static final int DESTROY_BUTTON_Y = 425;
	
	private static final int DESTROY_BUTTON_WIDTH = 47;
	private static final int DESTROY_BUTTON_HEIGHT = 111;
	
	private static final int NUM_ACTIVE_ABILITIES = 3;
	
	private static final int ABILITY_HEIGHT = 116;
	
	private static final int ABILITY_PANEL_X = 187;
	private static final int ABILITY_PANEL_Y = 24;
	
	private static final int ABILITY_PANEL_WIDTH = 462;
	private static final int ABILITY_PANEL_HEIGHT = 73;
	
	private static final int ENERGY_PANEL_X = 167;
	private static final int ENERGY_PANEL_Y = 104;

	private static final int ENERGY_PANEL_WIDTH = 482;
	private static final int ENERGY_PANEL_HEIGHT = 7;
	
	/**
	 * The number of buttons on the command card
	 */
	private static final int NUM_H_BUTTONS = 4;
	private static final int NUM_V_BUTTONS = 3;

	/**
	 * The location of the command button panel within the command card
	 */
	private static final int BTN_PANEL_X = 112;
	private static final int BTN_PANEL_Y = 144;

	/**
	 * The height and width of the command button panel
	 */
	private static final int BTN_PANEL_WIDTH = 523;
	private static final int BTN_PANEL_HEIGHT = 393;

	/**
	 * The gaps between the command buttons
	 */
	private static final int BTN_H_GAP = 10;
	private static final int BTN_V_GAP = 10;
	
	private CommandButton buildButton;
	private ButtonIcon buildIcon;
	private ButtonIcon buildPressed;
	private ButtonIcon buildRollover;
	private ButtonIcon buildSelected;
	
	private CommandButton destroyButton;
	private ButtonIcon destroyIcon;
	private ButtonIcon destroyPressed;
	private ButtonIcon destroyRollover;
	private ButtonIcon destroySelected;
	
	private boolean buildNotDestroy;

	private JPanel abilityPanel;
	private CommandButton[] activeAbilities;
	private ButtonIcon[] abilityIcons;
	private ButtonIcon[] abilityPressedIcons;
	private ButtonIcon[] abilityRolloverIcons;
	private ButtonIcon[] abilitySelectedIcons;
	private ActiveAbilityHandler[] abilityEvents;
	
	private EnergyPanel energyPanel;
	
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
	public CommandCardPanel(Display display, int pID, GameStateProvider stateManager, MessageReceiver receiver, String buildURI, String destroyURI, Animation regularButton, Animation clickedButton,
			Animation... anims)
	{
		super(stateManager, DEFAULT_WIDTH, DEFAULT_HEIGHT, anims[0]);

		this.display = display;
		this.receiver = receiver;
		this.playerID = pID;
		this.displayed = false;
		
		buildNotDestroy = true;
		
		try
		{
			ImageDrawer.getInstance().addImage(buildURI);
			ImageDrawer.getInstance().addImage(destroyURI);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		buildButton = new CommandButton();
		buildIcon = new ButtonIcon(buildButton);
		buildIcon.setURI(buildURI);
		buildIcon.setBackground(regularButton);
		buildButton.setIcon(buildIcon);
		
		buildPressed = new ButtonIcon(buildButton);
		buildPressed.setURI(buildURI);
		buildPressed.setBackground(clickedButton);
		buildButton.setPressedIcon(buildPressed);
		
		buildRollover = new ButtonIcon(buildButton);
		buildRollover.setURI(buildURI);
		buildRollover.setBackground(regularButton);
		buildButton.setPressedIcon(buildRollover);
		
		buildSelected = new ButtonIcon(buildButton);
		buildSelected.setURI(buildURI);
		buildSelected.setBackground(regularButton);
		buildButton.setPressedIcon(buildSelected);
		
		destroyButton = new CommandButton();
		destroyIcon = new ButtonIcon(destroyButton);
		destroyIcon.setURI(destroyURI);
		destroyIcon.setBackground(regularButton);
		destroyButton.setIcon(destroyIcon);
		
		destroyPressed = new ButtonIcon(destroyButton);
		destroyPressed.setURI(destroyURI);
		destroyPressed.setBackground(clickedButton);
		destroyButton.setPressedIcon(destroyPressed);
		
		destroyRollover = new ButtonIcon(destroyButton);
		destroyRollover.setURI(destroyURI);
		destroyRollover.setBackground(regularButton);
		destroyButton.setPressedIcon(destroyRollover);
		
		destroySelected = new ButtonIcon(destroyButton);
		destroySelected.setURI(destroyURI);
		destroySelected.setBackground(regularButton);
		destroyButton.setPressedIcon(destroySelected);
		
		ToggleListener toggle = new ToggleListener();
		buildButton.addActionListener(toggle);
		destroyButton.addActionListener(toggle);
		
		add(buildButton);
		add(destroyButton);
		
		energyPanel = new EnergyPanel();
		energyPanel.setMaxEnergy(GameState.MAX_PLAYER_ENERGY);
		
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
			abilityIcons[i].setBackground(regularButton);
			activeAbilities[i].setIcon(abilityIcons[i]);
			
			abilityPressedIcons[i] = new ButtonIcon(activeAbilities[i]);
			abilityPressedIcons[i].setBackground(clickedButton);
			activeAbilities[i].setPressedIcon(abilityPressedIcons[i]);
			
			abilityRolloverIcons[i] = new ButtonIcon(activeAbilities[i]);
			abilityRolloverIcons[i].setBackground(regularButton);
			activeAbilities[i].setRolloverIcon(abilityRolloverIcons[i]);
			
			abilitySelectedIcons[i] = new ButtonIcon(activeAbilities[i]);
			abilitySelectedIcons[i].setBackground(regularButton);
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
			buttonIcons[i].setBackground(regularButton);
			buttons[i].setIcon(buttonIcons[i]);

			pressedIcons[i] = new ButtonIcon(buttons[i]);
			pressedIcons[i].setBackground(clickedButton);
			buttons[i].setPressedIcon(pressedIcons[i]);

			rolloverIcons[i] = new ButtonIcon(buttons[i]);
			rolloverIcons[i].setBackground(regularButton);
			buttons[i].setRolloverIcon(rolloverIcons[i]);

			selectedIcons[i] = new ButtonIcon(buttons[i]);
			selectedIcons[i].setBackground(regularButton);
			buttons[i].setSelectedIcon(selectedIcons[i]);

			clickEvents[i] = new ClickHandler();
			buttons[i].addActionListener(clickEvents[i]);
		}

		add(abilityPanel);
		add(energyPanel);
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
		buildButton.setLocation((int)(BUILD_BUTTON_X * scaleFactor), (int)(BUILD_BUTTON_Y * scaleFactor));
		buildButton.setSize((int)(BUILD_BUTTON_WIDTH * scaleFactor), (int)(BUILD_BUTTON_HEIGHT * scaleFactor));

		destroyButton.setLocation((int)(DESTROY_BUTTON_X * scaleFactor), (int)(DESTROY_BUTTON_Y * scaleFactor));
		destroyButton.setSize((int)(DESTROY_BUTTON_WIDTH * scaleFactor), (int)(DESTROY_BUTTON_HEIGHT * scaleFactor));

		abilityPanel.setLayout(new GridLayout(1, NUM_ACTIVE_ABILITIES, (int)(BTN_H_GAP * scaleFactor), (int)(BTN_V_GAP * scaleFactor)));
		abilityPanel.setLocation((int)(ABILITY_PANEL_X * scaleFactor), (int)(ABILITY_PANEL_Y * scaleFactor));
		abilityPanel.setSize((int)(ABILITY_PANEL_WIDTH * scaleFactor), (int)(ABILITY_PANEL_HEIGHT * scaleFactor));
		
		energyPanel.setLocation((int)(ENERGY_PANEL_X * scaleFactor), (int)(ENERGY_PANEL_Y * scaleFactor));
		energyPanel.setSize((int)(ENERGY_PANEL_WIDTH * scaleFactor), (int)(ENERGY_PANEL_HEIGHT * scaleFactor));
		
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
		
		energyPanel.setEnergy(player.getPlayerEnergy());
		
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

			activeAbilities[i].setVisible(true);
			abilityIcons[i].setURI(icons.getIconURI(IconType.regular));
			abilityPressedIcons[i].setURI(icons.getIconURI(IconType.pressed));
			abilityRolloverIcons[i].setURI(icons.getIconURI(IconType.rollover));
			abilitySelectedIcons[i].setURI(icons.getIconURI(IconType.highlighted));
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
			
			buttons[i].setVisible(true);
			buttonIcons[i].setURI(icons.getIconURI(IconType.regular));
			pressedIcons[i].setURI(icons.getIconURI(IconType.pressed));
			rolloverIcons[i].setURI(icons.getIconURI(IconType.rollover));
			selectedIcons[i].setURI(icons.getIconURI(IconType.highlighted));
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
		private boolean isPressed;
		
		public CommandButton()
		{
			addActionListener(SoundPlayer.getInstance().getButtonSoundListener());
			isPressed = false;
			addMouseListener(new MousePressAdapter());
		}
		
		@Override
		public void paint(Graphics g)
		{
			super.paint(g);
			Icon pressedIcon = getPressedIcon();
			Icon selectedIcon = getSelectedIcon();
			Icon rolloverIcon = getRolloverIcon();
			Icon icon = getIcon();

			DefaultButtonModel model = (DefaultButtonModel)getModel();
			
			if(isPressed)
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
		
		private class MousePressAdapter extends MouseAdapter
		{
			public void mousePressed(MouseEvent e)
			{
				isPressed = true;
			}
			
			public void mouseReleased(MouseEvent e)
			{
				isPressed = false;
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
		private Animation background;

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
		
		public void setBackground(Animation background)
		{
			this.background = background;
		}
		
		public String getURI()
		{
			return uri;
		}

		@Override
		public int getIconHeight()
		{
			return button.getHeight();
		}

		@Override
		public int getIconWidth()
		{
			return button.getWidth();
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			ImageDrawer.getInstance().draw(g, background.getImage(0.0, 0.0), button.getWidth(), button.getHeight(), new Position(x, y), 1);
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
	
	private class EnergyPanel extends JPanel
	{
		private double energy;
		private double maxEnergy;
		private Color c;
		
		public EnergyPanel()
		{
			this.c = new Color(ENERGY_COLOR);
			this.maxEnergy = 0.0;
			this.energy = 0.0;
		}
		
		public void setEnergy(double energy)
		{
			this.energy = energy;
		}
		
		public void setMaxEnergy(double maxEnergy)
		{
			this.maxEnergy = maxEnergy;
		}
		
		@Override
		public void paint(Graphics g)
		{
			double barWidth = getWidth() * (energy / maxEnergy);
			
			g.setColor(c);
			g.fillRect(0, 0, (int)barWidth, getHeight());
		}
	}
}

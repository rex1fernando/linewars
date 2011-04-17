package linewars.display.panels;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;

import linewars.display.Animation;
import linewars.display.Display;
import linewars.display.ImageDrawer;
import linewars.display.sound.SoundPlayer;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.Position;

@SuppressWarnings("serial")
public class TechButtonPanel extends Panel
{
	private static final double ASPECT_RATIO = 0.05;
	
	/**
	 * The height and width of the panel
	 */
	private static final int DEFAULT_WIDTH = 104;
	private static final int DEFAULT_HEIGHT = 42;

	private Display display;
	private TechPanel techPanel;
	private JButton techButton;

	/**
	 * Constructs this exit button.
	 * 
	 * @param frame
	 *            The JFrame to close on exit.
	 * @param stateManager
	 *            The gamestate manager for this instance of the game.
	 * @param anims
	 *            The list of animations for the button.
	 */
	public TechButtonPanel(TechPanel techPanel, Display display, GameStateProvider stateManager, Animation... anims)
	{
		super(stateManager, DEFAULT_WIDTH, DEFAULT_HEIGHT, anims);
		
		this.display = display;
		this.techPanel = techPanel;

		setLayout(new GridLayout(1, 1));
		techButton = new ArrowButton();
		techButton.setFocusable(false);
		techButton.setOpaque(false);
		techButton.setIcon(new ArrowIcon(animations[0].getImage(0, 0.0), animations[1].getImage(0, 0.0)));
		techButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TechButtonPanel.this.techPanel.toggleDisplayed();
				TechButtonPanel.this.techPanel.updateLocation();
				updateLocation();
			}
		});
		add(techButton);
	}
	
	@Override
	public void updateLocation()
	{
		scaleFactor = (display.getScreenWidth() * ASPECT_RATIO) / DEFAULT_WIDTH;

		super.updateLocation();
		
		if(techPanel.isDisplayed())
			setLocation((getParent().getWidth() / 2) - (getWidth() / 2), (int)(techPanel.scaleFactor * TechPanel.DEFAULT_HEIGHT));
		else
			setLocation((getParent().getWidth() / 2) - (getWidth() / 2), 0);
		
		techButton.setSize((int)(DEFAULT_WIDTH * scaleFactor), (int)(DEFAULT_HEIGHT * scaleFactor));
	}

	/**
	 * A button for the command card.
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class ArrowButton extends JButton
	{
		public ArrowButton()
		{
			addActionListener(SoundPlayer.getInstance().getButtonSoundListener());
		}
		
		@Override
		public void paint(Graphics g)
		{
			getIcon().paintIcon(this, g, 0, 0);
		}
	}

	/**
	 * An animation for the exit button
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class ArrowIcon implements Icon
	{
		private String downImageURI;
		private String upImageURI;

		/**
		 * Constructs this icon animation.
		 * 
		 * @param uri
		 */
		public ArrowIcon(String uriDown, String uriUp)
		{
			downImageURI = uriDown;
			upImageURI = uriUp;
		}

		@Override
		public int getIconHeight()
		{
			return DEFAULT_HEIGHT;
		}

		@Override
		public int getIconWidth()
		{
			return DEFAULT_WIDTH;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			if(techPanel.isDisplayed())
				ImageDrawer.getInstance().draw(g, upImageURI, getIconWidth(), getIconHeight(), new Position(x, y), scaleFactor);
			else
				ImageDrawer.getInstance().draw(g, downImageURI, getIconWidth(), getIconHeight(), new Position(x, y), scaleFactor);
		}
	}
}

package linewars.display.panels;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;

import linewars.display.Animation;
import linewars.display.ImageDrawer;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.Position;

@SuppressWarnings("serial")
public class TechButtonPanel extends Panel
{
	/**
	 * The height and width of the panel
	 */
	private static final int WIDTH = 75;
	private static final int HEIGHT = 25;

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
	public TechButtonPanel(TechPanel techPanel, GameStateProvider stateManager, Animation... anims)
	{
		super(stateManager, WIDTH, HEIGHT, anims);
		
		this.techPanel = techPanel;

		setLayout(new GridLayout(1, 1));
		techButton = new JButton();
		techButton.setFocusable(false);
		techButton.setIcon(new ArrowIcon(animations[0].getImage(0, 0.0), animations[1].getImage(0, 0.0)));
		techButton.setPressedIcon(new ArrowIcon(animations[2].getImage(0, 0.0), animations[3].getImage(0, 0.0)));
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
		super.updateLocation();
		
		if(techPanel.isDisplayed())
			setLocation((getParent().getWidth() / 2) - (getWidth() / 2), (int)(techPanel.scaleFactor * TechPanel.DEFAULT_HEIGHT));
		else
			setLocation((getParent().getWidth() / 2) - (getWidth() / 2), 0);
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
			return HEIGHT;
		}

		@Override
		public int getIconWidth()
		{
			return WIDTH;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			if(techPanel.isDisplayed())
				ImageDrawer.getInstance().draw(g, upImageURI, getIconWidth(), getIconHeight(), new Position(x, y), 1);
			else
				ImageDrawer.getInstance().draw(g, downImageURI, getIconWidth(), getIconHeight(), new Position(x, y), 1);
		}
	}
}

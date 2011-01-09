package linewars.display.panels;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import linewars.configfilehandler.ConfigData;
import linewars.display.ImageDrawer;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.Position;

/**
 * Encapsulates the Exit button.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 * 
 */
@SuppressWarnings("serial")
public class ExitButtonPanel extends Panel
{
	/**
	 * The height and width of the panel
	 */
	private static final int WIDTH = 75;
	private static final int HEIGHT = 25;

	private JFrame frame;
	private JButton exitButton;

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
	public ExitButtonPanel(JFrame frame, GameStateProvider stateManager, ConfigData... anims)
	{
		super(stateManager, WIDTH, HEIGHT, anims);

		this.frame = frame;

		setLayout(new GridLayout(1, 1));
		exitButton = new JButton();
		exitButton.setFocusable(false);
		exitButton.setIcon(new ExitIcon(animations[0].getImage(0, 0.0)));
		exitButton.setPressedIcon(new ExitIcon(animations[1].getImage(0, 0.0)));
		exitButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object[] options = {"Yes",
									"No"};
				int n = JOptionPane.showOptionDialog(getParent(),
						"Are you sure you want to exit?",
						"Exit",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[0]);

				if(n == JOptionPane.YES_OPTION)
				{
					ExitButtonPanel.this.frame.dispose();
				}
			}
		});
		add(exitButton);
	}

	/**
	 * An animation for the exit button
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class ExitIcon implements Icon
	{
		private String imageURI;

		/**
		 * Constructs this icon animation.
		 * 
		 * @param uri
		 */
		public ExitIcon(String uri)
		{
			imageURI = uri;
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
			ImageDrawer.getInstance().draw(g, imageURI, new Position(x, y), 1);
		}
	}
}

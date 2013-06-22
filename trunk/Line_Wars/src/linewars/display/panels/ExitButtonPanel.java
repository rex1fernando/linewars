package linewars.display.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import linewars.display.Animation;
import linewars.display.Display;
import linewars.display.ImageDrawer;
import linewars.display.sound.SoundPlayer;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.Position;
import menu.ContentProvider;

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
	private static final double ASPECT_RATIO = 0.06;
	
	/**
	 * The height and width of the panel
	 */
	private static final int DEFAULT_WIDTH = 500;
	private static final int DEFAULT_HEIGHT = 250;

	private Display display;
	private JButton exitButton;

	/**
	 * Constructs this exit button.
	 * 
	 * @param display
	 *            The JFrame to close on exit.
	 * @param stateManager
	 *            The gamestate manager for this instance of the game.
	 * @param anims
	 *            The list of animations for the button.
	 */
	public ExitButtonPanel(Display display, GameStateProvider stateManager, Animation... anims)
	{
		super(stateManager, DEFAULT_WIDTH, DEFAULT_HEIGHT, anims);

		this.display = display;

		setLayout(new GridLayout(1, 1));
		exitButton = new ExitButton();
		exitButton.setFocusable(false);
		exitButton.setOpaque(false);
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
					ExitButtonPanel.this.display.exitGame();
				}
			}
		});
		add(exitButton);
	}

	@Override
	public void updateLocation()
	{
		scaleFactor = (display.getScreenWidth() * ASPECT_RATIO) / DEFAULT_WIDTH;

		super.updateLocation();

		setLocation(0, 0);
		
		exitButton.setSize((int)(DEFAULT_WIDTH * scaleFactor), (int)(DEFAULT_HEIGHT * scaleFactor));
	}

	/**
	 * A button for the command card.
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class ExitButton extends JButton
	{
		private String text;
		private Font font;

		public ExitButton()
		{
			addActionListener(SoundPlayer.getInstance().getButtonSoundListener());

			this.text = "EXIT";
			
			setOpaque(false);
			setBorder(null);
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			
			DefaultButtonModel model = (DefaultButtonModel)getModel();
			if(model.isPressed())
				getPressedIcon().paintIcon(this, g, 0, 0);
			else
				getIcon().paintIcon(this, g, 0, 0);

			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();
			
			Point pos = ContentProvider.centerText(fm, text, getWidth(), getHeight());
			g.setColor(Color.white);
			g.drawString(text, pos.x, pos.y);
		}
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
			ImageDrawer.getInstance().draw(g, imageURI, getIconWidth(), getIconHeight(), new Position(x, y), scaleFactor);
		}
	}
}

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
import javax.swing.Timer;

import linewars.display.ImageDrawer;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.Position;
import linewars.parser.Parser;

@SuppressWarnings("serial")
public class ExitButtonPanel extends Panel
{
	/**
	 * The height and width of the panel
	 */
	private static final int WIDTH = 75;
	private static final int HEIGHT = 25;
	
	private JFrame frame;
	private Timer timer;
	private JButton exitButton;
	
	public ExitButtonPanel(JFrame frame, Timer toStop, GameStateProvider stateManager, Parser ... anims)
	{
		super(stateManager, WIDTH, HEIGHT, anims);
		
		this.frame = frame;
		this.timer = toStop;
		
		setLayout(new GridLayout(1,1));
		exitButton = new JButton();
		exitButton.setFocusable(false);
		exitButton.setIcon(new ExitIcon(animations[0].getImage(0, 0.0)));
		exitButton.setPressedIcon(new ExitIcon(animations[1].getImage(0, 0.0)));
		exitButton.addActionListener(new ActionListener() {
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
				    options[1]);
				
				if (n == JOptionPane.YES_OPTION)
				{
					ExitButtonPanel.this.frame.dispose();
					timer.stop();
				}
			}
		});
		add(exitButton);
	}
	
	@Override
	public void updateLocation()
	{
		super.updateLocation();
		
		//resize the button
		exitButton.setSize(WIDTH, HEIGHT);

		setLocation(0, 0);
	}
	
	private class ExitIcon implements Icon
	{
		private String imageURI;
		
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
			ImageDrawer.getInstance().draw(g, imageURI, new Position(x, y), 0.0, 1);
		}
	}
}

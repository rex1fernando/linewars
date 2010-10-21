package linewars.display.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import linewars.display.Animation;
import linewars.display.Display;
import linewars.gamestate.GameStateManager;

public class ExitButtonPanel extends Panel
{
	private static final int WIDTH = 75;
	private static final int HEIGHT = 25;
	
	private JFrame frame;
	private JButton exitButton;
	
	public ExitButtonPanel(JFrame frame, GameStateManager stateManager, Animation ... anims)
	{
		super(stateManager, anims);
		
		this.frame = frame;
		
		setSize(WIDTH, HEIGHT);
		
		setLayout(new GridLayout(1,1));
		exitButton = new JButton();
		exitButton.setFocusable(false);
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
				}
			}
		});
		add(exitButton);
	}
	
	@Override
	public void updateLocation()
	{
		setSize(WIDTH, HEIGHT);
		setLocation(0, 0);
		validate();
	}
}

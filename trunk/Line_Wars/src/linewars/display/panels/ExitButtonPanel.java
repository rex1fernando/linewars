package linewars.display.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import linewars.display.Animation;
import linewars.gamestate.GameStateManager;

public class ExitButtonPanel extends Panel
{
	private static final double X_POS = 0.0;
	private static final double Y_POS = 0.0;
	private static final double WIDTH = 0.06;
	private static final double HEIGHT = 0.04;
	
	private JButton exitButton;
	
	public ExitButtonPanel(GameStateManager stateManager, Animation ... anims)
	{
		super(stateManager, X_POS, Y_POS, WIDTH, HEIGHT, anims);
		
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
					System.exit(0);
				}
			}
		});
		add(exitButton);
	}
	
	@Override
	public void updateLocation()
	{
		super.updateLocation();
		validate();
	}
}

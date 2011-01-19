package linewars.display.panels;

import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import linewars.gamestate.tech.TechGraph;

public class TechDisplay extends JPanel
{
	private TechGraph tech;
	private JPanel treeDisplay;
	private TechButton[][] buttons;
	
	public TechDisplay(TechGraph tech)
	{
		this.tech = tech;
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);
		
		treeDisplay = new JPanel(new GridLayout(tech.getMaxX(), tech.getMaxY()));
		scrollPane.add(treeDisplay);
		
		buttons = new TechButton[tech.getMaxX()][tech.getMaxY()];
		for(int r = 0; r < buttons.length; ++r)
		{
			for(int c = 0; c < buttons[r].length; ++c)
			{
				buttons[r][c] = new TechButton();
//				buttons[r][c].setOpaque(false);
//				buttons[r][c].setVisible(false);
				treeDisplay.add(buttons[r][c]);
			}
		}
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
	}
	
	/**
	 * A button for the command card.
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class TechButton extends JButton
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
}

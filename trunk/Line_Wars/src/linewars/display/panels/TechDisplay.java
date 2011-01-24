package linewars.display.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
		
		setLayout(new GridLayout());
		
		GridBagLayout treeLayout = new GridBagLayout();
		GridBagConstraints treeConstraints = new GridBagConstraints();
		treeDisplay = new JPanel(treeLayout);
		
		treeConstraints.gridwidth = 1;
		treeConstraints.gridheight = 1;
		treeConstraints.gridy = 0;
		buttons = new TechButton[tech.getMaxY()][tech.getMaxX()];
		for(int r = 0; r < buttons.length; ++r)
		{
			treeConstraints.gridx = 0;
			for(int c = 0; c < buttons[r].length; ++c)
			{
				buttons[r][c] = new TechButton();
//				buttons[r][c].setOpaque(false);
//				buttons[r][c].setVisible(false);
				treeDisplay.add(buttons[r][c]);
				treeLayout.addLayoutComponent(buttons[r][c], treeConstraints);
				
				++treeConstraints.gridx;
			}

			++treeConstraints.gridy;
		}
		
		JScrollPane scrollPane = new JScrollPane(treeDisplay);
		add(scrollPane);
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(Color.blue);
		g.fillRect(0, 0, getWidth(), getHeight());		
		
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
		public TechButton()
		{
			Dimension size = new Dimension(50, 50);
			
			setPreferredSize(size);
		}
		
		@Override
		public void paint(Graphics g)
		{
			super.paint(g);
			
//			DefaultButtonModel model = (DefaultButtonModel)getModel();
//			if(model.isPressed())
//				getPressedIcon().paintIcon(this, g, 0, 0);
//			else if(model.isSelected())
//				getSelectedIcon().paintIcon(this, g, 0, 0);
//			else if(model.isRollover())
//				getRolloverIcon().paintIcon(this, g, 0, 0);
//			else
//				getIcon().paintIcon(this, g, 0, 0);
		}
	}
}

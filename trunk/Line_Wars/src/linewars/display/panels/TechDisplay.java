package linewars.display.panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JViewport;

import linewars.gamestate.Node;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.abilities.ConstructBuildingDefinition;
import linewars.gamestate.mapItems.abilities.ResearchTechDefinition;
import linewars.gamestate.tech.TechGraph;
import linewars.gamestate.tech.TechGraph.TechNode;
import linewars.network.messages.BuildMessage;
import linewars.network.messages.Message;
import linewars.network.messages.UpgradeMessage;

public class TechDisplay extends JViewport
{
	private final static int TECH_BUTTON_SIZE = 50;
	
	private TechGraph tech;
	private JPanel treeDisplay;
	private TechButton[][] buttons;
	
	public TechDisplay(TechGraph tech)
	{
		this.tech = tech;
		
		initializeDisplay();
		displayGraph();
		
		ViewportDragger dragger = new ViewportDragger();
		addMouseListener(dragger);
		addMouseMotionListener(dragger);
	}

	private void initializeDisplay()
	{
		setOpaque(false);
		
		int xSize = tech.getMaxX();
		int ySize = tech.getMaxY();
		
		treeDisplay = new JPanel();
		treeDisplay.setOpaque(false);
		treeDisplay.setSize(xSize * TECH_BUTTON_SIZE, ySize * TECH_BUTTON_SIZE);
		add(treeDisplay);
		
		buttons = new TechButton[ySize][xSize];
		for(int r = 0; r < buttons.length; ++r)
		{
			for(int c = 0; c < buttons[r].length; ++c)
			{
				buttons[r][c] = new TechButton();
//				buttons[r][c].setOpaque(false);
				buttons[r][c].setVisible(false);
				buttons[r][c].setLocation(c * TECH_BUTTON_SIZE, r * TECH_BUTTON_SIZE);
				buttons[r][c].addActionListener(new ButtonHandler(r, c));
				treeDisplay.add(buttons[r][c]);
			}
		}
	}
	
	private void displayGraph()
	{
		tech.unmarkAll();
		
		TechNode root = tech.getRoot();
		while(root != null)
		{
			displayTechNode(root);
			root = tech.getNextRoot();
		}
		
		tech.unmarkAll();
	}
	
	private void displayTechNode(TechNode node)
	{
		node.mark();
		
		Point p = buttons[node.getY() - 1][node.getX() - 1].getLocation();
		buttons[node.getY() - 1][node.getX() - 1].setVisible(true);
		
		TechNode child = node.getChild();
		while(child != null)
		{
			displayTechNode(child);
			child = node.getNextChild();
		}
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
			Dimension size = new Dimension(TECH_BUTTON_SIZE, TECH_BUTTON_SIZE);
			
			setSize(size);
			setMaximumSize(size);
			setPreferredSize(size);
			setMinimumSize(size);
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
	
	private class ViewportDragger extends MouseAdapter
	{
		private Point lastPoint;
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			lastPoint = e.getPoint();
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			Point vector = e.getPoint();
			vector.translate(-lastPoint.x, -lastPoint.y);
			
			Point p = getViewPosition();
			p.translate(-vector.x, -vector.y);
			
			if(p.x < 0)
				p.setLocation(0, p.y);
			else if(p.x > treeDisplay.getWidth() - getWidth())
				p.setLocation(treeDisplay.getWidth() - getWidth(), p.y);
			
			if(p.y < 0)
				p.setLocation(p.x, 0);
			else if(p.y > treeDisplay.getHeight() - getHeight())
				p.setLocation(p.x, treeDisplay.getHeight() - getHeight());
			
			setViewPosition(p);
		}
	}
	
	private class ButtonHandler implements ActionListener
	{
		private int row;
		private int col;
		
		public ButtonHandler(int row, int col)
		{
			this.row = row;
			this.col = col;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Point p = buttons[row][col].getLocation();
		}
	}
}

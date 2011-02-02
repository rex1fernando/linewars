package linewars.display.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import linewars.display.Display;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.tech.CycleException;
import linewars.gamestate.tech.TechGraph;
import linewars.gamestate.tech.TechGraph.TechNode;

public class TechPanel extends Panel
{
	private static final double ASPECT_RATIO = 0.75;

	private static final int DEFAULT_WIDTH = 1500;
	static final int DEFAULT_HEIGHT = 750;
	
	private Display display;
	
	private JButton[] tabs;
	private TechDisplay[] techs;
	
	private boolean displayed;
	
	public TechPanel(Display display, GameStateProvider stateManager, int pID)
	{
		super(stateManager, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		this.display = display;
		this.displayed = false;
		
		//TEST CODE
		this.tabs = new JButton[2];
		this.techs = new TechDisplay[2];
		
		TechGraph tech1 = new TechGraph();
		TechNode parent1 = tech1.addNode();
		TechNode child1 = tech1.addNode();
		
		parent1.setPosition(1, 1);
		child1.setPosition(15, 5);
		
		try {
			parent1.addChild(child1);
		} catch (CycleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TechGraph tech2 = new TechGraph();
		TechNode parent2 = tech2.addNode();
		TechNode child2 = tech2.addNode();
		
		parent2.setPosition(1, 5);
		child2.setPosition(15, 1);
		
		try {
			parent2.addChild(child2);
		} catch (CycleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.tabs[0] = new JButton("TECH1");
		this.techs[0] = new TechDisplay(pID,tech1);
		
		this.tabs[1] = new JButton("TECH2");
		this.techs[1] = new TechDisplay(pID,tech2);
		//END TEST CODE
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel buttonPanel = new JPanel(new GridLayout());
		buttonPanel.setOpaque(false);
		add(buttonPanel);
		for(int i = 0; i < tabs.length; ++i)
		{
			tabs[i].addActionListener(new TabButtonHandler(i));
			buttonPanel.add(tabs[i]);
		}
		
		GridBagLayout techLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel techPanel = new JPanel(techLayout);
		techPanel.setOpaque(false);
		add(techPanel);
		for(int i = 0; i < techs.length; ++i)
		{
			techs[i].setVisible(false);
			techPanel.add(techs[i]);
			techLayout.addLayoutComponent(techs[i], c);
		}
		
		techs[0].setVisible(true);
	}
	
	boolean isDisplayed()
	{
		return displayed;
	}
	
	void toggleDisplayed()
	{
		displayed = !displayed;
	}
	
	private void displayTechGraph(int index)
	{
		for(int i = 0; i < techs.length; ++i)
		{
			
			techs[i].setVisible(i == index);
		}
	}
	
	@Override
	public void updateLocation()
	{
		scaleFactor = (display.getScreenWidth() * ASPECT_RATIO) / DEFAULT_WIDTH;

		super.updateLocation();
		
		if(isDisplayed())
			setLocation((getParent().getWidth() / 2) - (getWidth() / 2), 0);
		else
			setLocation((getParent().getWidth() / 2) - (getWidth() / 2), (int)(scaleFactor * -DEFAULT_HEIGHT));
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(Color.pink);
		g.fillRect(0, 0, getWidth(), getHeight());		
		
		super.paint(g);
	}
	
	private class TabButtonHandler implements ActionListener
	{
		private int index;
		
		public TabButtonHandler(int i)
		{
			index = i;
		}

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			displayTechGraph(index);
		}	
	}
}

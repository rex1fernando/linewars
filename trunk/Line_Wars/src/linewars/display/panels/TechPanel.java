package linewars.display.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

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
	private static final int DEFAULT_HEIGHT = 750;
	
	private Display display;
	
	private JButton[] tabs;
	private TechDisplay[] techs;
	
	public TechPanel(Display display, GameStateProvider stateManager)
	{
		super(stateManager, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		this.display = display;
		
		//TEST CODE
		this.tabs = new JButton[1];
		this.techs = new TechDisplay[1];
		
		TechGraph tech = new TechGraph();
		TechNode parent = tech.addNode();
		TechNode child = tech.addNode();
		
		parent.setPosition(1, 1);
		child.setPosition(15, 5);
		
		try {
			parent.addChild(child);
		} catch (CycleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.tabs[0] = new JButton("TECH");
		this.techs[0] = new TechDisplay(tech);
		//END TEST CODE
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel buttonPanel = new JPanel(new GridLayout());
		buttonPanel.setOpaque(false);
		add(buttonPanel);
		for(int i = 0; i < tabs.length; ++i)
		{
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
	
	@Override
	public void updateLocation()
	{
		scaleFactor = (display.getScreenWidth() * ASPECT_RATIO) / DEFAULT_WIDTH;

		super.updateLocation();
		
		setLocation((getParent().getWidth() / 2) - (getWidth() / 2), 0);
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(Color.pink);
		g.fillRect(0, 0, getWidth(), getHeight());		
		
		super.paint(g);
	}
}

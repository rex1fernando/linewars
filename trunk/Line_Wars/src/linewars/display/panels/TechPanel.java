package linewars.display.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import configuration.Configuration;

import editor.BigFrameworkGuy;
import editor.URISelector;
import editor.URISelector.SelectorOptions;

import linewars.display.Display;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.BuildingSpot;
import linewars.gamestate.Node;
import linewars.gamestate.tech.CycleException;
import linewars.gamestate.tech.TechConfiguration;
import linewars.gamestate.tech.TechGraph;
import linewars.gamestate.tech.TechGraph.TechNode;

public class TechPanel extends Panel
{
	private static final double ASPECT_RATIO = 0.75;

	private static final int DEFAULT_WIDTH = 1500;
	static final int DEFAULT_HEIGHT = 750;
	
	private Display display;
	private BigFrameworkGuy bfg;
	
	private GridBagLayout techLayout;
	private GridBagConstraints c;
	
	private JPanel tabPanel;
	private JPanel techPanel;
	private JPanel editorComponents;
	
	private ArrayList<JButton> tabs;
	private ArrayList<TechDisplay> techs;
	
	private TechDisplay activeTech;
	
	private JComboBox techSelector;
	private JComboBox unlockStrategySelector;
	
	private boolean displayed;
	
	/**
	 * Constructs the TechPanel for the editors, allows all elements to be edited.
	 * @param bfg The BigFrameworkGuy that contains this panel.
	 */
	public TechPanel(BigFrameworkGuy bfg)
	{
		super(null, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		this.bfg = bfg;

		initialize();
		
		addEditorElements();
		
		addComponentListener(new ResizeListener());
	}

	/**
	 * Constructs the TechPanel for use in the game, does not allow editing, displays the state of techs, and researches them.
	 * @param display The Display that contains this panel.
	 * @param stateManager The GameStateProvider for the current session of the game.
	 * @param pID The ID of the player this TechPanel is displayed for.
	 */
	public TechPanel(Display display, GameStateProvider stateManager, int pID)
	{
		super(stateManager, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		this.display = display;
		this.displayed = false;
		
//TEST CODE
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

		this.tabs.add(new JButton("TECH1"));
		this.techs.add(new TechDisplay(pID, tech1));
		
		this.tabs.add(new JButton("TECH2"));
		this.techs.add(new TechDisplay(pID ,tech2));
//END TEST CODE
		
		initialize();
	}
	
	private void initialize()
	{
		this.tabs = new ArrayList<JButton>();
		this.techs = new ArrayList<TechDisplay>();
	
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		techLayout = new GridBagLayout();
		c = new GridBagConstraints();
		techPanel = new JPanel(techLayout);
		techPanel.setOpaque(false);
		for(int i = 0; i < techs.size(); ++i)
		{
			techs.get(i).setVisible(false);
			techPanel.add(techs.get(i));
			techLayout.addLayoutComponent(techs.get(i), c);
		}
		
		tabPanel = new JPanel(new GridLayout());
		tabPanel.setOpaque(false);
		for(int i = 0; i < tabs.size(); ++i)
		{
			tabs.get(i).addActionListener(new TabButtonHandler(techs.get(i)));
			tabPanel.add(tabs.get(i));
		}
		
		add(tabPanel);
		add(techPanel);
		
		if(!techs.isEmpty())
			techs.get(0).setVisible(true);
	}
	
	private void addEditorElements()
	{
		editorComponents = new JPanel();
		editorComponents.setLayout(new BoxLayout(editorComponents, BoxLayout.X_AXIS));
		add(editorComponents);
		
		JButton addTechGraph = new JButton("Add Tech Graph");
		addTechGraph.addActionListener(new AddTechGraphHandler());
		editorComponents.add(addTechGraph);
		
		JPanel techSelectorPanel = new JPanel();
		JLabel techLabel = new JLabel("Tech:");
		techSelectorPanel.add(techLabel);
		techSelector = new JComboBox();
		techSelector.setPreferredSize(new Dimension(160, 20));
		techSelector.addActionListener(new ComboBoxListener());
		techSelectorPanel.add(techSelector);
		editorComponents.add(techSelectorPanel);
		
//		List<Configuration> techs = bfg.getConfigurationsByType(BigFrameworkGuy.ConfigType.tech);
//		for(Configuration tech : techs)
//		{
//			techSelector.addItem(tech);
//		}
		
		JPanel unlockStrategySelectorPanel = new JPanel();
		JLabel unlockStrategyLabel = new JLabel("Un:");
		unlockStrategySelectorPanel.add(unlockStrategyLabel);
		unlockStrategySelector = new JComboBox();
		unlockStrategySelector.setPreferredSize(new Dimension(160, 20));
		unlockStrategySelector.addActionListener(new ComboBoxListener());
		unlockStrategySelectorPanel.add(unlockStrategySelector);
		editorComponents.add(unlockStrategySelectorPanel);
		
		unlockStrategySelector.addItem("All");
		unlockStrategySelector.addItem("One");
		unlockStrategySelector.addItem("No Syblings");
	}

	boolean isDisplayed()
	{
		return displayed;
	}
	
	void toggleDisplayed()
	{
		displayed = !displayed;
	}
	
	private void setAllTechGraphsInvisible()
	{
		for(int i = 0; i < techs.size(); ++i)
		{
			techs.get(i).setVisible(false);
		}
		
		activeTech.setVisible(true);
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
	
	private class ResizeListener extends ComponentAdapter
	{
		@Override
		public void componentResized(ComponentEvent e)
		{
			int width = getWidth();
			int height = getHeight();
			
			height -= tabPanel.getHeight();
			
			if(editorComponents != null)
				height -= editorComponents.getHeight();
			
			for(TechDisplay td : techs)
			{
				td.setPreferredSize(new Dimension(width, height));
				td.validate();
				td.updateUI();
			}
		}
	}
	
	private class TabButtonHandler implements ActionListener
	{
		private TechDisplay tech;
		
		public TabButtonHandler(TechDisplay tech)
		{
			this.tech = tech;
		}

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			activeTech = tech;
			setAllTechGraphsInvisible();
		}	
	}
	
	private class AddTechGraphHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			String s = (String)JOptionPane.showInputDialog(
					getParent(),
                    "Name the new tech graph",
                    "Tech Graph",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "Tech");
			
			if(s == null)
				return;
			
			s.trim();
			if(s.equals(""))
				return;
			
			TechDisplay tech = new TechDisplay(new TechGraph());
			techs.add(tech);
			techPanel.add(tech);
			techLayout.addLayoutComponent(tech, c);
			
			JButton tab = new JButton(s);
			tab.addActionListener(new TabButtonHandler(tech));
			tabs.add(tab);
			tabPanel.add(tab);
			
			TechPanel.this.validate();
		}
	}
	
	private class ComboBoxListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();

			if(source == techSelector)
			{
				TechConfiguration selected = (TechConfiguration)techSelector.getSelectedItem();
				if(selected != null)
				{
					activeTech.getActiveTech().setTech(selected);
				}
			}
			else if(source == unlockStrategySelector)
			{
				String selected = (String)unlockStrategySelector.getSelectedItem();
				if(selected != null)
				{
					//TODO set the correct UnlockStrategy to the active tech button for the selection
				}
			}
		}
	}
}

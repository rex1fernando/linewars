package linewars.display.panels;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import linewars.gamestate.Race;
import linewars.gamestate.tech.TechGraph;

public class TechDisplay extends JPanel
{
	private TechGraph tech;
	private JPanel treeDisplay;
	
	public TechDisplay(TechGraph tech)
	{
		this.tech = tech;
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);
		
		treeDisplay = new JPanel();
		scrollPane.add(treeDisplay);
		

	}
}

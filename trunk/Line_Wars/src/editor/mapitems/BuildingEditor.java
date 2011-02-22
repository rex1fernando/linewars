package editor.mapitems;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import linewars.display.IconConfiguration;
import linewars.display.IconConfiguration.IconType;
import linewars.gamestate.mapItems.BuildingDefinition;
import configuration.Configuration;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.IconEditor;

/**
 * 
 * @author Connor Schenck
 *
 * This class represents the panel that allows users
 * to edit the specific values related to only
 * buildings.
 */
public class BuildingEditor extends JPanel implements ConfigurationEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4874979209656216145L;
	
	//variable for holding the cost
	private JTextField cost;
	
	//variable for holding the build time
	private JTextField buildTime;
	
	//variables for the icons
	private ConfigurationEditor icons;
	private Configuration iconConfig;
	
	/**
	 * Constructs this building editor.
	 */
	public BuildingEditor()
	{	
		//set up the cost panel
		cost = new JTextField();
		cost.setColumns(10);
		JPanel costPanel = new JPanel();
		costPanel.add(new JLabel("Cost:"));
		costPanel.add(cost);
		
		//set up the build time panel
		buildTime = new JTextField();
		buildTime.setColumns(10);
		JPanel buildTimePanel = new JPanel();
		buildTimePanel.add(new JLabel("Build Time (ms):"));
		buildTimePanel.add(buildTime);
		
		List<IconType> neededIcons = new ArrayList<IconType>();
		List<String> iconDescriptions = new ArrayList<String>();
		neededIcons.add(IconType.regular);
		iconDescriptions.add("");
		neededIcons.add(IconType.highlighted);
		iconDescriptions.add("");
		neededIcons.add(IconType.pressed);
		iconDescriptions.add("");
		neededIcons.add(IconType.rollover);
		iconDescriptions.add("");
		icons = new IconEditor(neededIcons, iconDescriptions);
		iconConfig = icons.instantiateNewConfiguration();
		
		JPanel innerPanel = new JPanel();

		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		innerPanel.add(costPanel);
		innerPanel.add(buildTimePanel);
		innerPanel.add(icons.getPanel());
		
		JScrollPane scroller = new JScrollPane(innerPanel);
		scroller.setPreferredSize(new Dimension(600, 200));
		this.add(scroller);
	}

	@Override
	public void setData(Configuration cd) {
		BuildingDefinition bd = (BuildingDefinition)cd;
		cost.setText(bd.getCost() + "");
		buildTime.setText(bd.getBuildTime() + "");
		iconConfig = bd.getIconConfig();
		icons.setData(iconConfig);
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		cost.setText("");
		buildTime.setText("");
		iconConfig = icons.instantiateNewConfiguration();
		return new BuildingDefinition();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		BuildingDefinition bd = (BuildingDefinition)toSet;
		Scanner parser = new Scanner(cost.getText());
		if(parser.hasNextDouble())
			bd.setCost(parser.nextDouble());
		else
			bd.setCost(0);
		
		parser = new Scanner(buildTime.getText());
		if(parser.hasNextDouble())
			bd.setBuildTime(parser.nextDouble());
		else
			bd.setBuildTime(0);
		
		icons.getData(iconConfig);
		bd.setIconConfig((IconConfiguration) iconConfig);
		
		return ConfigType.building;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.building);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

}

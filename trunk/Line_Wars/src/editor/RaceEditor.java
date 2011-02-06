package editor;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import linewars.gamestate.Race;
import configuration.Configuration;
import editor.BigFrameworkGuy.ConfigType;
import editor.ListURISelector.ListSelectorOptions;
import editor.URISelector.SelectorOptions;


/**
 * An editor allowing the user to define races.
 * 
 * @author Titus Klinge
 */
public class RaceEditor implements ConfigurationEditor
{
	private BigFrameworkGuy superEditor;
	private RacePanel racePanel;
	
	public RaceEditor(BigFrameworkGuy bfg)
	{
		superEditor = bfg;
		racePanel = new RacePanel();
		instantiateNewConfiguration();	// creates a configData object and updates the GUI
	}
	
	@Override
	public void setData(Configuration cd)
	{	
		updatePanel((Race) cd);
	}

	@Override
	public Configuration instantiateNewConfiguration()
	{
		Configuration c = new Race();
		updatePanel((Race) c);
		return c;
	}

	@Override
	public ConfigType getData(Configuration toSet)
	{
		// TODO take stuff from the editor and place into the cd
		return ConfigType.race;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes()
	{
		ArrayList<ConfigType> arr = new ArrayList<ConfigType>(1);
		arr.add(ConfigType.race);
		return arr;
	}

	@Override
	public JPanel getPanel()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	private void updatePanel(Race cd)
	{
		racePanel.nameBox.name.setText(cd.getName());
		racePanel.commandCenter.setSelectedConfiguration(cd.getCommandCenter());
		racePanel.gate.setSelectedConfiguration(cd.getGate());
		racePanel.unit.setSelectedConfigurations(cd.getUnits());
		racePanel.building.setSelectedConfigurations(cd.getBuildings());
		racePanel.tech.setSelectedConfigurations(cd.getTechs());
	}
	
	/**
	 * The panel that contains all the GUI elements of the race editor.
	 */
	private class RacePanel extends JPanel
	{
		private static final long serialVersionUID = -4411534509382555738L;
		private static final int SPACING = 3;
		private NameBox nameBox;
		private ConfigurationSelector commandCenter;
		private ConfigurationSelector gate;
		private ListConfigurationSelector unit;
		private ListConfigurationSelector building;
		private ListConfigurationSelector tech;
		
		public RacePanel()
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			nameBox = new NameBox();
			add(nameBox);
			
			initConfigSelectors();
		}
		
		private void initConfigSelectors()
		{
			commandCenter = createSelector("Command Center", ConfigType.building);
			initURISelector(commandCenter);
			gate = createSelector("Gate", ConfigType.gate);
			initURISelector(gate);
			unit = createListSelector("Unit", superEditor.getUnitURIs());
			initURISelector(unit);
			building = createListSelector("Building", superEditor.getBuildingURIs());
			initURISelector(building);
			tech = createListSelector("Tech", superEditor.getTechURIs());
			initURISelector(tech);
		}
		
		private ConfigurationSelector createSelector(String name, ConfigType t)
		{
			return new ConfigurationSelector(name, superEditor, t);
		}

		private ListConfigurationSelector createListSelector(String name, final String[] options)
		{
			return 
		}
		
		/**
		 * Adds the JPanel to the race panel and adds spacing after it.
		 * 
		 * @param p The panel to be added.
		 */
		private void initURISelector(JPanel p)
		{
			add(p);
			add(Box.createVerticalStrut(SPACING));
		}
	}
	
	/**
	 * A label and text field pair for typing in a name.
	 */
	private class NameBox extends JPanel
	{
		private static final long serialVersionUID = -6205161701141390950L;
		
		/**
		 * The text field holding the name.
		 */
		private JTextField name;
		
		/**
		 * Constructs a new name box with an empty text field.
		 */
		public NameBox()
		{
			add(new JLabel("Name"));
			add(Box.createHorizontalStrut(5));
			
			name = new JTextField();
			name.setPreferredSize(new Dimension(160, 20));
			add(name);
		}
	}
}

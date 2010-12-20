package editor;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import editor.ListURISelector.ListSelectorOptions;
import editor.URISelector.SelectorOptions;

/**
 * An editor allowing the user to define races.
 * 
 * @author Titus Klinge
 */
public class RaceEditor implements ConfigurationEditor
{
	/**
	 * The one editor to control them all.
	 */
	private BigFrameworkGuy superEditor;
	
	/**
	 * The actual panel that is used by the configuration editor.
	 */
	private RacePanel racePanel;

	/**
	 * Creates a new RaceEditor object with a reference to the BigFrameworkGuy
	 * as its parent.
	 * 
	 * @param bfg The parent big framework guy of the race editor.
	 */
	public RaceEditor(BigFrameworkGuy bfg)
	{
		superEditor = bfg;
		racePanel = new RacePanel();
		reset();	// creates a configData object and updates the GUI
	}

	@Override
	public void setData(ConfigData cd)
	{
		if (isValid(cd))
		{
			forceSetData(cd);
		}
		throw new RuntimeException("The configuration file: " + cd + " is invalid.");
	}

	@Override
	public void forceSetData(ConfigData cd)
	{
		updatePanel(cd);
	}

	@Override
	public void reset()
	{
		updatePanel(new ConfigData());
	}

	@Override
	public ConfigData getData()
	{
		return createConfigData(racePanel);
	}

	@Override
	public boolean isValidConfig()
	{
		return isValid(createConfigData(racePanel));
	}

	@Override
	public ParserKeys getType()
	{
		return ParserKeys.raceURI;
	}
	
	/**
	 * Checks to see if the ConfigData object contains all the required
	 * information to save the race it corresponds to.
	 * 
	 * @param cd The ConfigData object to check.
	 * @return True if the data object has all the necessary elements.
	 */
	private boolean isValid(ConfigData cd)
	{
		try
		{
			if (cd.getString(ParserKeys.gateURI) == null
			 || cd.getString(ParserKeys.name) == null
			 || cd.getString(ParserKeys.commandCenterURI) == null
			 || cd.getStringList(ParserKeys.unitURI).size() == 0
			 || cd.getStringList(ParserKeys.buildingURI).size() == 0)
			{
				return false;
			}
		} catch (Exception e)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Populates the GUI with all the information stored within the ConfigData
	 * object.
	 * 
	 * @param cd The config data object to populate the gui with.
	 */
	private void updatePanel(ConfigData cd)
	{
		racePanel.nameBox.name.setText((cd.getDefinedKeys().contains(ParserKeys.name)? cd.getString(ParserKeys.name) : ""));
		racePanel.commandCenter.setSelectedURI((cd.getDefinedKeys().contains(ParserKeys.commandCenterURI)? cd.getString(ParserKeys.commandCenterURI) : ""));
		racePanel.gate.setSelectedURI((cd.getDefinedKeys().contains(ParserKeys.gateURI)? cd.getString(ParserKeys.gateURI) : ""));
		racePanel.unit.setSelectedURIs((cd.getDefinedKeys().contains(ParserKeys.unitURI)? cd.getStringList(ParserKeys.unitURI).toArray(new String[0]) : new String[0]));
		racePanel.building.setSelectedURIs((cd.getDefinedKeys().contains(ParserKeys.buildingURI)? cd.getStringList(ParserKeys.buildingURI).toArray(new String[0]) : new String[0]));
		racePanel.tech.setSelectedURIs((cd.getDefinedKeys().contains(ParserKeys.techURI)? cd.getStringList(ParserKeys.techURI).toArray(new String[0]) : new String[0]));
	}
	
	/**
	 * Creates and returns a config data object using the data stored within the
	 * GUI fields.
	 * 
	 * @param rp The panel to take the data from.
	 * @return The config data corresponding to the data stored within the panel.
	 */
	private ConfigData createConfigData(RacePanel rp)
	{
		ConfigData data = new ConfigData();
		
		data.set(ParserKeys.name, racePanel.nameBox.name.getText());
		data.set(ParserKeys.commandCenterURI, racePanel.commandCenter.getSelectedURI());
		data.set(ParserKeys.gateURI, racePanel.gate.getSelectedURI());
		data.set(ParserKeys.buildingURI, racePanel.building.getSelectedURIs());
		data.set(ParserKeys.techURI, racePanel.tech.getSelectedURIs());
		data.set(ParserKeys.unitURI, racePanel.unit.getSelectedURIs());
		
		return data;
	}

	@Override
	public JPanel getPanel()
	{
		return racePanel;
	}

	/**
	 * The panel that contains all the GUI elements of the race editor.
	 */
	private class RacePanel extends JPanel
	{
		private static final long serialVersionUID = -4411534509382555738L;

		/**
		 * The number of units of space between elements in the panel.
		 */
		private static final int SPACING = 3;
		
		/**
		 * A label and text field combo for typing in the name of the race.
		 */
		private NameBox nameBox;
		
		/**
		 * The selector for the command center.
		 */
		private URISelector commandCenter;
		
		/**
		 * The selector for the gate building.
		 */
		private URISelector gate;
		
		/**
		 * The selector for the race's units.
		 */
		private ListURISelector unit;
		
		/**
		 * The selector for the buildings in the race.
		 */
		private ListURISelector building;
		
		/**
		 * The selector for the techs in the race.
		 */
		private ListURISelector tech;
		
		/**
		 * Creates a new RacePanel that is empty.
		 */
		public RacePanel()
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			nameBox = new NameBox();
			add(nameBox);
			
			initURISelectors();
		}
		
		/**
		 * Initializes all the selectors.
		 */
		private void initURISelectors()
		{
			commandCenter = createSelector("Command Center", superEditor.getCommandCenterURIs());
			initURISelector(commandCenter);
			gate = createSelector("Gate", superEditor.getGateURIs());
			initURISelector(gate);
			unit = createListSelector("Unit", superEditor.getUnitURIs());
			initURISelector(unit);
			building = createListSelector("Building", superEditor.getBuildingURIs());
			initURISelector(building);
			tech = createListSelector("Tech", superEditor.getTechURIs());
			initURISelector(tech);
		}
		
		/**
		 * Creates a new selector with the given name and allowing the selection
		 * of the list of strings.
		 * 
		 * @param name The name for the selector.
		 * @param options The options to select within the selector.
		 * @return The newly constructed selector.
		 */
		private URISelector createSelector(String name, final String[] options)
		{
			return new URISelector(name, new SelectorOptions() {
				public String[] getOptions() { return options; }
				public void uriSelected(String uri) {}
			});
		}

		/**
		 * Similar to the method above, except that it creates a list selector.
		 * 
		 * @param name The name of the selector.
		 * @param options The options to be selected.
		 * @return The selector that was constructed.
		 */
		private ListURISelector createListSelector(String name, final String[] options)
		{
			return new ListURISelector(name, new ListSelectorOptions() {
				public String[] getOptions() { return options; }
				public void uriSelected(String uri) {}
				public void uriRemoved(String uri) {}
				public void uriHighlightChange(String[] uris) {}
			});
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

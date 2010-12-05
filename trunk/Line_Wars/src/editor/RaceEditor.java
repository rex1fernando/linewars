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
	
	private void updatePanel(ConfigData cd)
	{
		racePanel.nameBox.name.setText((cd.getDefinedKeys().contains(ParserKeys.name)? cd.getString(ParserKeys.name) : ""));
		racePanel.commandCenter.setSelectedURI((cd.getDefinedKeys().contains(ParserKeys.commandCenterURI)? cd.getString(ParserKeys.commandCenterURI) : ""));
		racePanel.gate.setSelectedURI((cd.getDefinedKeys().contains(ParserKeys.gateURI)? cd.getString(ParserKeys.gateURI) : ""));
		racePanel.unit.setSelectedURIs((cd.getDefinedKeys().contains(ParserKeys.unitURI)? cd.getStringList(ParserKeys.unitURI).toArray(new String[0]) : new String[0]));
		racePanel.building.setSelectedURIs((cd.getDefinedKeys().contains(ParserKeys.buildingURI)? cd.getStringList(ParserKeys.buildingURI).toArray(new String[0]) : new String[0]));
		racePanel.tech.setSelectedURIs((cd.getDefinedKeys().contains(ParserKeys.techURI)? cd.getStringList(ParserKeys.techURI).toArray(new String[0]) : new String[0]));
	}
	
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

	private class RacePanel extends JPanel
	{
		private static final long serialVersionUID = -4411534509382555738L;

		private static final int SPACING = 3;
		
		private NameBox nameBox;
		
		private URISelector commandCenter;
		private URISelector gate;
		
		private ListURISelector unit;
		private ListURISelector building;
		private ListURISelector tech;
		
		public RacePanel()
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			nameBox = new NameBox();
			add(nameBox);
			
			initURISelectors();
		}
		
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
		
		private URISelector createSelector(String name, final String[] options)
		{
			return new URISelector(name, new SelectorOptions() {
				public String[] getOptions() { return options; }
				public void uriSelected(String uri) {}
			});
		}

		private ListURISelector createListSelector(String name, final String[] options)
		{
			return new ListURISelector(name, new ListSelectorOptions() {
				public String[] getOptions() { return options; }
				public void uriSelected(String uri) {}
				public void uriRemoved(String uri) {}
				public void uriHighlightChange(String[] uris) {}
			});
		}
		
		private void initURISelector(JPanel p)
		{
			add(p);
			add(Box.createVerticalStrut(SPACING));
		}
	}
	
	private class NameBox extends JPanel
	{
		private static final long serialVersionUID = -6205161701141390950L;
		private JTextField name;
		
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

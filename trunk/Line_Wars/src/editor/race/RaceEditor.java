package editor.race;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import linewars.display.panels.TechPanel;
import linewars.gamestate.Race;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.GateDefinition;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.gamestate.tech.TechGraph;
import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.GenericSelector;
import editor.GenericSelector.CustomToString;
import editor.GenericSelector.GenericListCallback;
import editor.ListGenericSelector;


public class RaceEditor implements ConfigurationEditor
{	
	private BigFrameworkGuy bfg;
	private RacePanel racePanel;
	
	public RaceEditor(BigFrameworkGuy bfg)
	{
		this.bfg = bfg;
		racePanel = new RacePanel();
	}
	
	@Override
	public void setData(Configuration cd) {
		Race race = (Race) cd;
		
		resetEditor();
		
		racePanel.nameBox.name.setText(race.getName());
		racePanel.commandCenter.setSelectedObject(race.getCommandCenter());
		racePanel.gate.setSelectedObject(race.getGate());
		racePanel.allUnits.setSelectedObjects(race.getAllUnits());
		racePanel.enabledUnits.setSelectedObjects(race.getUnlockedUnits());
		racePanel.allBuildings.setSelectedObjects(race.getAllBuildings());
		racePanel.enabledBuildings.setSelectedObjects(race.getUnlockedBuildings());
		racePanel.tech.setAllTechGraphs(race.getAllTechGraphs());
		racePanel.tech.setEnabledTechGraphs(race.getUnlockedTechGraphs());
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new Race();
	}
	
	public void resetEditor()
	{
		racePanel.nameBox.name.setText("");
		racePanel.commandCenter.setSelectedObject(null);
		racePanel.gate.setSelectedObject(null);
		racePanel.allUnits.setSelectedObjects(null);
		racePanel.enabledUnits.setSelectedObjects(null);
		racePanel.allBuildings.setSelectedObjects(null);
		racePanel.enabledBuildings.setSelectedObjects(null);
		racePanel.tech.resetTechGraphs();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		Race race = (Race) toSet;
		
		race.setName(racePanel.nameBox.name.getText());
		
		race.setCommandCenter((BuildingDefinition) racePanel.commandCenter.getSelectedObject());
		race.setGate((GateDefinition) racePanel.gate.getSelectedObject());
		
		race.removeAllUnits();
		for (UnitDefinition def : racePanel.allUnits.getSelectedObjects()) {
			boolean enabled = racePanel.enabledUnits.getSelectedObjects().contains(def);
			race.addUnit(def, enabled);
		}
		
		race.removeAllBuildings();
		for (BuildingDefinition def : racePanel.allBuildings.getSelectedObjects()) {
			boolean enabled = racePanel.enabledBuildings.getSelectedObjects().contains(def);
			race.addBuilding(def, enabled);
		}
		
		List<TechGraph> allGraphs = racePanel.tech.getAllTechGraphs();
		List<TechGraph> enabledGraphs = racePanel.tech.getEnabledTechGraphs();
		List<Boolean> en = new ArrayList<Boolean>(allGraphs.size());
		for (int i = 0; i < allGraphs.size(); ++i) {
			boolean enabled = (enabledGraphs.contains(allGraphs.get(i)));
			en.add(enabled);
		}
		race.setTechGraphs(allGraphs, en);
		
		return ConfigType.race;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> list = new ArrayList<ConfigType>();
		list.add(ConfigType.race);
		return list;
	}

	@Override
	public JPanel getPanel() {
		return racePanel;
	}
	
	private class RacePanel extends JPanel
	{
		private static final long serialVersionUID = -4411534509382555738L;

		private static final int SPACING = 3;
		private NameBox nameBox;
		private GenericSelector<BuildingDefinition> commandCenter;
		private GenericSelector<GateDefinition> gate;
		private ListGenericSelector<UnitDefinition> allUnits;
		private ListGenericSelector<UnitDefinition> enabledUnits;
		private ListGenericSelector<BuildingDefinition> allBuildings;
		private ListGenericSelector<BuildingDefinition> enabledBuildings;
		
		private TechPanel tech;
		
		public RacePanel()
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			nameBox = new NameBox();
			add(nameBox);
			
			initConfigSelectors();
			tech = new TechPanel(bfg);
			tech.setPreferredSize(new Dimension(720, 480));
			add(tech);
		}
		
		private void initConfigSelectors()
		{
			commandCenter = initGenericSelector("Command Center", ConfigType.building);
			initConfigSelector(commandCenter);
			gate = initGenericSelector("Gate", ConfigType.gate);
			initConfigSelector(gate);
			
			allUnits = initListGenericSelector("Unit", ConfigType.unit);
			initConfigSelector(allUnits);
			CustomToString<UnitDefinition> unitString = new GenericSelector.ShowBFGName<UnitDefinition>();
			GenericListCallback<UnitDefinition> unitCallback = new GenericListCallback<UnitDefinition>() {
				public List<UnitDefinition> getSelectionList() {
					return allUnits.getSelectedObjects();
				}
			};
			enabledUnits = new ListGenericSelector<UnitDefinition>("Enabled Units", unitCallback, unitString);
			initConfigSelector(enabledUnits);
			
			allBuildings = initListGenericSelector("Building", ConfigType.building);
			initConfigSelector(allBuildings);
			CustomToString<BuildingDefinition> buildingString = new GenericSelector.ShowBFGName<BuildingDefinition>();
			GenericListCallback<BuildingDefinition> buildingCallback = new GenericListCallback<BuildingDefinition>() {
				public List<BuildingDefinition> getSelectionList() {
					return allBuildings.getSelectedObjects();
				}
			};
			enabledBuildings = new ListGenericSelector<BuildingDefinition>("Enabled Buildings", buildingCallback, buildingString);
			initConfigSelector(enabledBuildings);
		}
		
		private <T extends Configuration> GenericSelector<T> initGenericSelector(String label, ConfigType c)
		{
			CustomToString<T> toString = new GenericSelector.ShowBFGName<T>();
			GenericListCallback<T> callback = new GenericSelector.SelectConfigurations<T>(bfg, c);
			GenericSelector<T> selector = new GenericSelector<T>(label, callback, toString);
			
			return selector;
		}
		
		private <T extends Configuration> ListGenericSelector<T> initListGenericSelector(String label, ConfigType c)
		{
			CustomToString<T> toString = new GenericSelector.ShowBFGName<T>();
			GenericListCallback<T> callback = new GenericSelector.SelectConfigurations<T>(bfg, c);
			ListGenericSelector<T> selector = new ListGenericSelector<T>(label, callback, toString);
			
			return selector;
		}
		
		private void initConfigSelector(JPanel p)
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

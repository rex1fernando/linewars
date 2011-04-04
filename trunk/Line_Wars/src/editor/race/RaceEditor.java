package editor.race;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import linewars.display.Animation;
import linewars.display.panels.TechPanel;
import linewars.gamestate.Race;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.GateDefinition;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.gamestate.playerabilities.PlayerAbility;
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
		racePanel.allPlayerAbilities.setSelectedObjects(race.getAllPlayerAbilites());
		racePanel.enabledPlayerAbilities.setSelectedObjects(race.getUnlockedPlayerAbilites());
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
		racePanel.allPlayerAbilities.setSelectedObjects(null);
		racePanel.enabledPlayerAbilities.setSelectedObjects(null);
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
		
		List<PlayerAbility> playerAbilities = new ArrayList<PlayerAbility>();
		List<Boolean> flags = new ArrayList<Boolean>();
		for(PlayerAbility pa : racePanel.allPlayerAbilities.getSelectedObjects())
		{
			boolean enabled = racePanel.enabledPlayerAbilities.getSelectedObjects().contains(pa);
			playerAbilities.add(pa);
			flags.add(enabled);
		}
		race.setPlayerAbilities(playerAbilities, flags);
		
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
		private ListGenericSelector<PlayerAbility> allPlayerAbilities;
		private ListGenericSelector<PlayerAbility> enabledPlayerAbilities;
		
		private TechPanel tech;
		
		public RacePanel()
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			nameBox = new NameBox();
			add(nameBox);
			
			initConfigSelectors();
			
			Animation techPanelBackground = null;
			try {
				techPanelBackground = (Animation)new ObjectInputStream(new FileInputStream(new File("resources/animations/tech_panel.cfg"))).readObject();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			tech = new TechPanel(bfg);
			tech.setPreferredSize(new Dimension(720, 480));
			
			final JButton techButton = new JButton("Show Tech Panel Editor");
			techButton.addActionListener(new ActionListener() {
				private JFrame frame = null;
				@Override
				public void actionPerformed(ActionEvent e) {
					if(frame == null)
						initFrame();
					
					if(frame.isVisible())
					{
						frame.setVisible(false);
						techButton.setText("Show Tech Panel Editor");
					}
					else
					{
						frame.setVisible(true);
						techButton.setText("Hide Tech Panel Editor");
						tech.validate();
						tech.updateUI();
					}
				}
				
				private void initFrame()
				{
					frame = new JFrame("Tech Panel Editor");
					frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					frame.getContentPane().add(tech, BorderLayout.CENTER);
					frame.pack();
					frame.addWindowListener(new WindowListener() {
						public void windowOpened(WindowEvent e) {}
						public void windowIconified(WindowEvent e) {}
						public void windowDeiconified(WindowEvent e) {}
						public void windowDeactivated(WindowEvent e) {}
						public void windowClosing(WindowEvent e) {
							techButton.setText("Show Tech Panel Editor");
						}
						public void windowClosed(WindowEvent e) {}
						public void windowActivated(WindowEvent e) {}
					});

					tech.setVisible(true);
				}
			});
			
			add(techButton);
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
			
			allPlayerAbilities = initListGenericSelector("Player Ability", ConfigType.playerAbility);
			initConfigSelector(allPlayerAbilities);
			CustomToString<PlayerAbility> playerAbilityString = new GenericSelector.ShowBFGName<PlayerAbility>();
			GenericListCallback<PlayerAbility> playerAbilityCallback = new GenericListCallback<PlayerAbility>() {
				public List<PlayerAbility> getSelectionList() {
					return allPlayerAbilities.getSelectedObjects();
				}
			};
			enabledPlayerAbilities = new ListGenericSelector<PlayerAbility>(
					"Enabled Player Abilities", playerAbilityCallback, playerAbilityString);
			initConfigSelector(enabledPlayerAbilities);
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

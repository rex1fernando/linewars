package editor.mapEditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import editor.ConfigurationEditor;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ParserKeys;

public class MapEditor extends JPanel implements ConfigurationEditor
{
	private ConfigData data;
	
	private MapPanel map;
	
	private JCheckBox selectNodes;
	private JCheckBox selectLanes;
	private JCheckBox selectBuildings;
	private JCheckBox selectCommandCenters;

	private JRadioButton createNode;
	private JRadioButton createBuilding;
	private JRadioButton createCommandCenter;

	public MapEditor(int width, int height)
	{
		super(null);

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(layout);
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.gridheight = GridBagConstraints.RELATIVE;
		map = new MapPanel(width, height);
		layout.setConstraints(map, c);
		add(map);
		
		//create check boxes
		selectNodes = new JCheckBox("Nodes");
		selectLanes = new JCheckBox("Lanes");
		selectBuildings = new JCheckBox("Buildings");
		selectCommandCenters = new JCheckBox("Command Centers");
		
		ArrayList<JCheckBox> items = new ArrayList<JCheckBox>();
		items.add(selectNodes);
		items.add(selectLanes);
		items.add(selectBuildings);
		items.add(selectCommandCenters);
		
		//set check boxes to selected by default
		selectNodes.setSelected(true);
		selectLanes.setSelected(true);
		selectBuildings.setSelected(true);
		selectCommandCenters.setSelected(true);
		
		//set the item listener for the check boxes
		ItemListener checkBoxListener = new CheckBoxListener();
		selectNodes.addItemListener(checkBoxListener);
		selectLanes.addItemListener(checkBoxListener);
		selectBuildings.addItemListener(checkBoxListener);
		selectCommandCenters.addItemListener(checkBoxListener);
		
		//add check boxes to JPanel
		JPanel selectedItems = new JPanel(new GridLayout(items.size(), 1));
		for(JCheckBox box : items)
		{
			selectedItems.add(box);
		}
		
		//add the selectable items panel to the editor
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(selectedItems, c);
		add(selectedItems);
		
		//create radio buttons
		createNode = new JRadioButton("Node");
		createBuilding = new JRadioButton("Building");
		createCommandCenter = new JRadioButton("Command Center");
		
		ArrayList<JRadioButton> createables = new ArrayList<JRadioButton>();
		createables.add(createNode);
		createables.add(createBuilding);
		createables.add(createCommandCenter);
		
		//add radio buttons to JPanel
		JPanel createItems = new JPanel(new GridLayout(1, createables.size()));
		for(JRadioButton button : createables)
		{
			createItems.add(button);
		}
		
		//add the creatable items panel to the editor
		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.gridheight = GridBagConstraints.REMAINDER;
		layout.setConstraints(createItems, c);
		add(createItems);
		
		//create and add the map selector button
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridy = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.REMAINDER;
		JButton setMap = new JButton("Set Map");
		layout.setConstraints(setMap, c);
		add(setMap);
	}
	
	@Override
	public void setData(ConfigData cd)
	{
		try
		{
			map.loadConfigFile(cd);
		}
		catch(NoSuchKeyException e)
		{
			//TODO spawn notificatoin window
			e.printStackTrace();
		}
	}

	@Override
	public void forceSetData(ConfigData cd)
	{
		try
		{
			map.loadConfigFile(cd);
		}
		catch(NoSuchKeyException e){}
	}

	@Override
	public void reset()
	{
		//TODO reset the MapPanel
		data = new ConfigData();
	}

	@Override
	public ConfigData getData()
	{
		//TODO get the data from the MapPanel
		return data;
	}

	@Override
	public ParserKeys getType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getPanel()
	{
		return this;
	}
	
	private class CheckBoxListener implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			// TODO Auto-generated method stub
			Object source = e.getItemSelectable();

			if(source == selectNodes)
			{
				map.setNodesVisible(e.getStateChange() == ItemEvent.SELECTED);
			}
			else if(source == selectLanes)
			{
				map.setLanesVisible(e.getStateChange() == ItemEvent.SELECTED);
			}
			else if(source == selectBuildings)
			{
				map.setBuildingsVisible(e.getStateChange() == ItemEvent.SELECTED);
			}
			else if(source == selectCommandCenters)
			{
				map.setCommandCentersVisible(e.getStateChange() == ItemEvent.SELECTED);
			}
		}
	}
}

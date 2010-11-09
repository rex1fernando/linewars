package editor.mapEditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
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
	private MapPanel map;
	
	private JCheckBox selectNodes;
	private JCheckBox selectLanes;
	private JCheckBox selectBuildings;
	private JCheckBox selectCommandCenters;

	private JRadioButton createLane;
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
		
		//the item listener for the check boxes
		ItemListener checkBoxListener = new CheckBoxListener();

		//add check boxes to JPanel
		JPanel selectedItems = new JPanel(new GridLayout(items.size(), 1));
		for(JCheckBox box : items)
		{
			box.setSelected(true);
			box.addItemListener(checkBoxListener);
			selectedItems.add(box);
		}
		
		//add the selectable items panel to the editor
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(selectedItems, c);
		add(selectedItems);
		
		//create radio buttons
		createLane = new JRadioButton("Lane");
		createNode = new JRadioButton("Node");
		createBuilding = new JRadioButton("Building");
		createCommandCenter = new JRadioButton("Command Center");
		
		ArrayList<JRadioButton> createables = new ArrayList<JRadioButton>();
		createables.add(createLane);
		createables.add(createNode);
		createables.add(createBuilding);
		createables.add(createCommandCenter);
		
	    //the group for the radio buttons
	    ButtonGroup group = new ButtonGroup();
	    
	    //the action listener for the radio buttons
	    RadioButtonListener radioButtonListener = new RadioButtonListener();
		
		//add radio buttons to JPanel
		JPanel createItems = new JPanel(new GridLayout(1, createables.size()));
		for(JRadioButton button : createables)
		{
			group.add(button);
			button.addItemListener(radioButtonListener);
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
	}

	@Override
	public ConfigData getData()
	{
		//TODO get the data from the MapPanel
		return map.getData();
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
	
	private class RadioButtonListener implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			Object source = e.getItemSelectable();
			
			if(source == createNode)
			{
				map.setCreateNode(e.getStateChange() == ItemEvent.SELECTED);
			}
			else if(source == createLane)
			{
				map.setCreateLane(e.getStateChange() == ItemEvent.SELECTED);
			}
			else if(source == createBuilding)
			{
				map.setCreateBuilding(e.getStateChange() == ItemEvent.SELECTED);
			}
			else if(source == createCommandCenter)
			{
				map.setCreateCommandCenter(e.getStateChange() == ItemEvent.SELECTED);
			}
		}
	}
}

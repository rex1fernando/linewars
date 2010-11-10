package editor.mapEditor;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.BuildingSpot;
import linewars.gamestate.Node;
import editor.ConfigurationEditor;
import editor.animations.FileCopy;

public class MapEditor extends JPanel implements ConfigurationEditor
{
	private MapPanel map;
	
	private JFrame frame;
	
	private JCheckBox selectNodes;
	private JCheckBox selectLanes;
	private JCheckBox selectBuildings;
	private JCheckBox selectCommandCenters;

	private JRadioButton createLane;
	private JRadioButton createNode;
	private JRadioButton createBuilding;
	private JRadioButton createCommandCenter;
	
	private JPanel placeholder;
	private JSlider laneWidthSlider;
	private JPanel nodeEditorPanel;
	
	private JCheckBox startNode;
	private JComboBox nodeSelector;
	private JComboBox buildingSpotSelector;
	private JComboBox commandCenterSelector;
	private JList containedBuildingSpots;
	private JList containedCommandCenter;
	
	private boolean editingPanelLoaded;

	public MapEditor(JFrame frame)
	{
		super(null);
		setPreferredSize(new Dimension(1200, 800));
		
		this.frame = frame;
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(layout);
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.gridheight = GridBagConstraints.RELATIVE;
		map = new MapPanel(768, 512);
		layout.setConstraints(map, c);
		add(map);
		
		createCheckBoxes(layout, c);
		createRadioButtons(layout, c);
		createEditingPanel(layout, c);
		
		//create and add the map selector button
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridy = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.REMAINDER;
		JButton setMap = new JButton("Set Map");
		setMap.addActionListener(new SetMapListener());
		layout.setConstraints(setMap, c);
		add(setMap);
	}

	private void createEditingPanel(GridBagLayout layout, GridBagConstraints c)
	{
		editingPanelLoaded = false;

		c.gridx = GridBagConstraints.RELATIVE;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		
		//create the place holder panel
		placeholder = new JPanel();
		placeholder.setVisible(true);
		layout.setConstraints(placeholder, c);
		add(placeholder);
		
		//create and add the lane width slider
		laneWidthSlider = new JSlider(JSlider.VERTICAL, 0, 100, 20);
		laneWidthSlider.addChangeListener(new LaneWidthListener());
		laneWidthSlider.setVisible(false);
		layout.setConstraints(laneWidthSlider, c);
		add(laneWidthSlider);
		
		//create the editor panels for editing the building spots that are in a node
		startNode = new JCheckBox("start node");
		nodeSelector = new JComboBox();
		buildingSpotSelector = new JComboBox();
		commandCenterSelector = new JComboBox();
		containedBuildingSpots = new JList();
		containedCommandCenter = new JList();
		
		ArrayList<JComponent> nodeEditors = new ArrayList<JComponent>();
		nodeEditors.add(new JLabel("Nodes"));
		nodeEditors.add(new JPanel());
		nodeEditors.add(nodeSelector);
		nodeEditors.add(startNode);
		nodeEditors.add(new JLabel("Buildings"));
		nodeEditors.add(new JLabel("Command Centers"));
		nodeEditors.add(buildingSpotSelector);
		nodeEditors.add(commandCenterSelector);
		nodeEditors.add(containedBuildingSpots);
		nodeEditors.add(containedCommandCenter);
		
		//add the editor panels to the node editing panel
		nodeEditorPanel = new JPanel(new GridLayout((nodeEditors.size() + 1) / 2, 2, 25, 10));
		for(JComponent panel : nodeEditors)
		{
			nodeEditorPanel.add(panel);
		}
		
		//add the node editing panel to the editor
		nodeEditorPanel.setVisible(false);
		layout.setConstraints(nodeEditorPanel, c);
		add(nodeEditorPanel);
		
		Dimension dim = new Dimension(400, 400);
		placeholder.setPreferredSize(dim);
		laneWidthSlider.setPreferredSize(dim);
		nodeEditorPanel.setPreferredSize(dim);
	}

	private void createRadioButtons(GridBagLayout layout, GridBagConstraints c)
	{
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
	}

	private void createCheckBoxes(GridBagLayout layout, GridBagConstraints c)
	{
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
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		layout.setConstraints(selectedItems, c);
		add(selectedItems);
	}
	
	private void populateNodeEditingPanel()
	{
		nodeSelector.removeAllItems();
		buildingSpotSelector.removeAllItems();
		commandCenterSelector.removeAllItems();
		
		for(Node n : map.getNodes())
		{
			nodeSelector.addItem(n);
		}
		
		for(BuildingSpot b : map.getBuildingSpots())
		{
			buildingSpotSelector.addItem(b);
		}
		
		for(BuildingSpot b : map.getCommandCenters())
		{
			commandCenterSelector.addItem(b);
		}
		
		editingPanelLoaded = true;
	}
	
	@Override
	public void setData(ConfigData cd)
	{
		editingPanelLoaded = false;

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
		editingPanelLoaded = false;

		try
		{
			map.loadConfigFile(cd);
		}
		catch(NoSuchKeyException e){}
	}

	@Override
	public void reset()
	{
		editingPanelLoaded = false;

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
		return ParserKeys.mapURI;
	}

	@Override
	public JPanel getPanel()
	{
		return this;
	}
	
	@Override
	public boolean isValidConfig()
	{
		return map.isValidConfig();
	}

	private class CheckBoxListener implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent e)
		{
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
			boolean selected = e.getStateChange() == ItemEvent.SELECTED;
			
			if(selected)
			{
				placeholder.setVisible(true);
				laneWidthSlider.setVisible(false);
				nodeEditorPanel.setVisible(false);
			}
			
			if(source == createNode)
			{
				map.setCreateNode(selected);
				
				if(selected)
				{
					placeholder.setVisible(false);
					nodeEditorPanel.setVisible(true);
					if(!editingPanelLoaded) populateNodeEditingPanel();
				}
			}
			else if(source == createLane)
			{
				map.setCreateLane(selected);
				
				if(selected)
				{
					placeholder.setVisible(false);
					laneWidthSlider.setVisible(true);
				}
			}
			else if(source == createBuilding)
			{
				map.setCreateBuilding(selected);
				
				if(selected)
				{
					placeholder.setVisible(false);
					nodeEditorPanel.setVisible(true);
					if(!editingPanelLoaded) populateNodeEditingPanel();
				}
			}
			else if(source == createCommandCenter)
			{
				map.setCreateCommandCenter(selected);
				
				if(selected)
				{
					placeholder.setVisible(false);
					nodeEditorPanel.setVisible(true);
					if(!editingPanelLoaded) populateNodeEditingPanel();
				}
			}
		}
	}
	
	private class SetMapListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			String mapURI = null;
			File mapFile = null;
			boolean fileSelected = false;
			while(!fileSelected)
			{
				FileDialog dialog = new FileDialog(frame, "map");
				dialog.setVisible(true);
				
				String directory = dialog.getDirectory();
				mapURI = dialog.getFile();
				
				if(mapURI == null)
					return;
				
				mapFile = new File(directory + mapURI);
				if(!mapFile.exists())
					JOptionPane.showMessageDialog(null, "File could not be loaded!", "ERROR", JOptionPane.ERROR_MESSAGE);
				else
					fileSelected = true;
			}
			
			String relativePath = "resources" + File.separator + "maps" + File.separator + mapURI;
			String moveTo = System.getProperty("user.dir") + File.separator + relativePath;
			if(!mapFile.getAbsolutePath().equals(moveTo))
			{
				try
				{
					FileCopy.copy(mapFile.getAbsolutePath(), moveTo);
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(frame, "File could not be copied!", "ERROR", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			
			map.setMapImage("/resources/maps/" + mapURI);
		}
	}
	
	private class LaneWidthListener implements ChangeListener
	{
		@Override
		public void stateChanged(ChangeEvent e)
		{
			JSlider source = (JSlider)e.getSource();
			
			if(source == laneWidthSlider)
				map.setLaneWidth(laneWidthSlider.getValue());
		}
	}
}

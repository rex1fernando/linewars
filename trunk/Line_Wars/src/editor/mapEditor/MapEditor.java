package editor.mapEditor;

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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import linewars.gamestate.BuildingSpot;
import linewars.gamestate.MapConfiguration;
import linewars.gamestate.NodeConfiguration;
import linewars.gamestate.Position;
import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.animations.FileCopy;

/**
 * The map editor panel for the config file editor.
 * 
 * @author Ryan Tew
 * 
 */
@SuppressWarnings("serial")
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

	private JTextField editWidth;
	private JTextField editHeight;

	private JCheckBox startNode;
	private JComboBox nodeSelector;
	private JComboBox buildingSpotSelector;
	private JComboBox commandCenterSelector;
	private JList containedBuildingSpots;
	private JList containedCommandCenter;
	private JButton addBuilding;
	private JButton removeBuilding;
	private JButton setCommandCenter;
	private JButton removeCommandCenter;

	private boolean editingPanelLoaded;

	/**
	 * Creates the map editor, initializing all of its contained panels.
	 * 
	 * @param frame
	 *            The JFrame that the map editor is contained in.
	 */
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
		c.gridwidth = 1;
		c.gridheight = 2;
		map = new MapPanel(this, 768, 512);
		layout.setConstraints(map, c);
		add(map);

		createCheckBoxes(layout, c);
		createEditingPanel(layout, c);
		createRadioButtons(layout, c);
		createMapEditPanel(layout, c);
	}

	/**
	 * Creates and initializes the map editing panel, including the map size
	 * text fields and the set map button.
	 * 
	 * @param layout
	 *            The GridBagLayout for the map editor.
	 * @param c
	 *            The GridBagConstraints for layout.
	 */
	private void createMapEditPanel(GridBagLayout layout, GridBagConstraints c)
	{
		// create the layout manager
		GridBagLayout mapEditLayout = new GridBagLayout();
		GridBagConstraints mapEditConstraints = new GridBagConstraints();

		// add radio buttons to JPanel
		JPanel mapEdit = new JPanel(mapEditLayout);
		mapEdit.setPreferredSize(new Dimension(400, 50));

		// create the labels
		mapEditConstraints.gridx = 0;
		mapEditConstraints.gridy = 0;
		mapEditConstraints.gridwidth = 1;
		mapEditConstraints.gridheight = 1;
		JLabel label = new JLabel("map width");
		mapEditLayout.setConstraints(label, mapEditConstraints);
		mapEdit.add(label);

		mapEditConstraints.gridx = 1;
		label = new JLabel("map height");
		mapEditLayout.setConstraints(label, mapEditConstraints);
		mapEdit.add(label);

		// create edit boxes
		mapEditConstraints.gridx = 0;
		mapEditConstraints.gridy = 1;
		editWidth = new JFormattedTextField(NumberFormat.getInstance());
		editWidth.setText("100");
		map.setMapWidthTextField(editWidth);
		mapEditLayout.setConstraints(editWidth, mapEditConstraints);
		mapEdit.add(editWidth);

		mapEditConstraints.gridx = 1;
		editHeight = new JFormattedTextField(NumberFormat.getInstance());
		editHeight.setText("100");
		map.setMapHeightTextField(editHeight);
		mapEditLayout.setConstraints(editHeight, mapEditConstraints);
		mapEdit.add(editHeight);

		// create and add the map selector button
		mapEditConstraints.gridx = 2;
		mapEditConstraints.gridy = 0;
		mapEditConstraints.gridheight = 2;
		JButton setMap = new JButton("Set Map Image");
		setMap.addActionListener(new SetMapListener());
		mapEditLayout.setConstraints(setMap, mapEditConstraints);
		mapEdit.add(setMap);

		// add an action listener to the text fields
		MapSizeListener listener = new MapSizeListener();
		editWidth.getDocument().addDocumentListener(listener);
		editHeight.getDocument().addDocumentListener(listener);

		// set the preferred size of the text fields
		editWidth.setPreferredSize(new Dimension(100, editWidth.getMinimumSize().height));
		editHeight.setPreferredSize(new Dimension(100, editHeight.getMinimumSize().height));

		// add the map edit panel to the editor
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		layout.setConstraints(mapEdit, c);
		add(mapEdit);
	}

	/**
	 * Creates and initializes the editing panel, this is the lane editing
	 * panel, the node editing panel, and a placeholder panel.
	 * 
	 * @param layout
	 *            The GridBagLayout for the map editor.
	 * @param c
	 *            The GridBagConstraints for layout.
	 */
	private void createEditingPanel(GridBagLayout layout, GridBagConstraints c)
	{
		editingPanelLoaded = false;

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;

		// create the place holder panel
		placeholder = new JPanel();
		placeholder.setVisible(true);
		layout.setConstraints(placeholder, c);
		add(placeholder);

		// create and add the lane width slider
		laneWidthSlider = new JSlider(JSlider.VERTICAL, 0, 100, 20);
		laneWidthSlider.addChangeListener(new LaneWidthListener());
		laneWidthSlider.setVisible(false);
		layout.setConstraints(laneWidthSlider, c);
		add(laneWidthSlider);
		map.setLaneWidthSlider(laneWidthSlider);

		createNodeEditorPanel();

		// add the node editing panel to the editor
		nodeEditorPanel.setVisible(false);
		layout.setConstraints(nodeEditorPanel, c);
		add(nodeEditorPanel);

		Dimension dim = new Dimension(400, 400);
		placeholder.setPreferredSize(dim);
		laneWidthSlider.setPreferredSize(dim);
		nodeEditorPanel.setPreferredSize(dim);
	}

	/**
	 * Creates and initializes the node editing panel, including combo boxes for
	 * the nodes, buildings, and command centers, the lists of contained
	 * buildings and command center, and the buttons for adding and removing a
	 * building or command center from a node.
	 * 
	 * @param layout
	 *            The GridBagLayout for the map editor.
	 * @param c
	 *            The GridBagConstraints for layout.
	 */
	private void createNodeEditorPanel()
	{
		GridBagLayout layout = new GridBagLayout();
		nodeEditorPanel = new JPanel(layout);

		// create the editor panels for editing the building spots that are in a
		// node
		startNode = new JCheckBox("start node");
		nodeSelector = new JComboBox();
		buildingSpotSelector = new JComboBox();
		commandCenterSelector = new JComboBox();
		containedBuildingSpots = new JList();
		containedCommandCenter = new JList();
		addBuilding = new JButton("add");
		removeBuilding = new JButton("remove");
		setCommandCenter = new JButton("set");
		removeCommandCenter = new JButton("remove");

		JLabel label;
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = 2;
		c.gridheight = 1;

		// add the components to the panel
		c.gridx = 0;
		c.gridy = 0;
		label = new JLabel("nodes", JLabel.CENTER);
		layout.addLayoutComponent(label, c);
		nodeEditorPanel.add(label);

		c.gridx = 0;
		c.gridy = 1;
		layout.addLayoutComponent(nodeSelector, c);
		nodeEditorPanel.add(nodeSelector);
		map.setNodeSelector(nodeSelector);
		c.gridx = 2;
		c.gridwidth = 1;
		layout.addLayoutComponent(startNode, c);
		nodeEditorPanel.add(startNode);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		label = new JLabel("buildings", JLabel.CENTER);
		layout.addLayoutComponent(label, c);
		nodeEditorPanel.add(label);
		c.gridx = 3;
		label = new JLabel("command centers", JLabel.CENTER);
		layout.addLayoutComponent(label, c);
		nodeEditorPanel.add(label);

		c.gridx = 0;
		c.gridy = 3;
		layout.addLayoutComponent(buildingSpotSelector, c);
		nodeEditorPanel.add(buildingSpotSelector);
		map.setBuildingSelector(buildingSpotSelector);
		c.gridx = 3;
		layout.addLayoutComponent(commandCenterSelector, c);
		nodeEditorPanel.add(commandCenterSelector);
		map.setCommandCenterSelector(commandCenterSelector);

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		layout.addLayoutComponent(addBuilding, c);
		nodeEditorPanel.add(addBuilding);
		c.gridx = 1;
		layout.addLayoutComponent(removeBuilding, c);
		nodeEditorPanel.add(removeBuilding);
		c.gridx = 3;
		layout.addLayoutComponent(setCommandCenter, c);
		nodeEditorPanel.add(setCommandCenter);
		c.gridx = 4;
		layout.addLayoutComponent(removeCommandCenter, c);
		nodeEditorPanel.add(removeCommandCenter);

		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		label = new JLabel("buildings in", JLabel.CENTER);
		layout.addLayoutComponent(label, c);
		nodeEditorPanel.add(label);
		c.gridy = 6;
		label = new JLabel("selected node:", JLabel.CENTER);
		layout.addLayoutComponent(label, c);
		nodeEditorPanel.add(label);

		c.gridx = 3;
		c.gridy = 5;
		label = new JLabel("command center", JLabel.CENTER);
		layout.addLayoutComponent(label, c);
		nodeEditorPanel.add(label);
		c.gridy = 6;
		label = new JLabel("for selected node", JLabel.CENTER);
		layout.addLayoutComponent(label, c);
		nodeEditorPanel.add(label);

		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 2;
		c.gridheight = 4;
		layout.addLayoutComponent(containedBuildingSpots, c);
		nodeEditorPanel.add(containedBuildingSpots);
		c.gridx = 3;
		layout.addLayoutComponent(containedCommandCenter, c);
		nodeEditorPanel.add(containedCommandCenter);

		nodeSelector.setPreferredSize(new Dimension(125, (int)nodeSelector.getMinimumSize().getHeight()));
		buildingSpotSelector
				.setPreferredSize(new Dimension(125, (int)buildingSpotSelector.getMinimumSize().getHeight()));
		commandCenterSelector.setPreferredSize(new Dimension(125, (int)commandCenterSelector.getMinimumSize()
				.getHeight()));

		containedBuildingSpots.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		containedCommandCenter.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		CheckBoxListener checkListener = new CheckBoxListener();
		startNode.addItemListener(checkListener);

		ComboBoxListener comboListener = new ComboBoxListener();
		nodeSelector.addActionListener(comboListener);
		buildingSpotSelector.addActionListener(comboListener);
		commandCenterSelector.addActionListener(comboListener);

		NodeEditorButtonListener buttonListener = new NodeEditorButtonListener();
		addBuilding.addActionListener(buttonListener);
		removeBuilding.addActionListener(buttonListener);
		setCommandCenter.addActionListener(buttonListener);
		removeCommandCenter.addActionListener(buttonListener);

		ListListener listListener = new ListListener();
		containedBuildingSpots.addListSelectionListener(listListener);
		containedCommandCenter.addListSelectionListener(listListener);
	}

	/**
	 * Creates and initializes the radio button panel, this is the radio buttons
	 * for selecting what type of map element is eligible for editing.
	 * 
	 * @param layout
	 *            The GridBagLayout for the map editor.
	 * @param c
	 *            The GridBagConstraints for layout.
	 */
	private void createRadioButtons(GridBagLayout layout, GridBagConstraints c)
	{
		// create radio buttons
		createLane = new JRadioButton("Lane");
		createNode = new JRadioButton("Node");
		createBuilding = new JRadioButton("Building");
		createCommandCenter = new JRadioButton("Command Center");

		ArrayList<JRadioButton> createables = new ArrayList<JRadioButton>();
		createables.add(createLane);
		createables.add(createNode);
		createables.add(createBuilding);
		createables.add(createCommandCenter);

		// the group for the radio buttons
		ButtonGroup group = new ButtonGroup();

		// the action listener for the radio buttons
		RadioButtonListener radioButtonListener = new RadioButtonListener();

		// add radio buttons to JPanel
		JPanel createItems = new JPanel(new GridLayout(createables.size() + 1, 1));
		for(JRadioButton button : createables)
		{
			group.add(button);
			button.addItemListener(radioButtonListener);
			createItems.add(button);
		}

		JButton delete = new JButton("delete");
		delete.addActionListener(new DeleteButtonListener());
		createItems.add(delete);

		// add the creatable items panel to the editor
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		layout.setConstraints(createItems, c);
		add(createItems);
	}

	/**
	 * Creates and initializes the check box panel, these are the check boxes
	 * for indicating what map elements to draw.
	 * 
	 * @param layout
	 *            The GridBagLayout for the map editor.
	 * @param c
	 *            The GridBagConstraints for layout.
	 */
	private void createCheckBoxes(GridBagLayout layout, GridBagConstraints c)
	{
		// create check boxes
		selectNodes = new JCheckBox("Nodes");
		selectLanes = new JCheckBox("Lanes");
		selectBuildings = new JCheckBox("Buildings");
		selectCommandCenters = new JCheckBox("Command Centers");

		ArrayList<JCheckBox> items = new ArrayList<JCheckBox>();
		items.add(selectNodes);
		items.add(selectLanes);
		items.add(selectBuildings);
		items.add(selectCommandCenters);

		// the item listener for the check boxes
		ItemListener checkBoxListener = new CheckBoxListener();

		// add check boxes to JPanel
		JPanel selectedItems = new JPanel(new GridLayout(1, items.size()));
		for(JCheckBox box : items)
		{
			box.setSelected(true);
			box.addItemListener(checkBoxListener);
			selectedItems.add(box);
		}

		// add the selectable items panel to the editor
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		layout.setConstraints(selectedItems, c);
		add(selectedItems);
	}

	/**
	 * This populates the combo boxes in the node editing panel with all of the
	 * nodes, buildings, and command centers in the map.
	 */
	private void populateNodeEditingPanel()
	{
		nodeSelector.removeAllItems();
		buildingSpotSelector.removeAllItems();
		commandCenterSelector.removeAllItems();

		for(NodeConfiguration n : map.getNodes())
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

	/**
	 * This populates the list of buildings and command centers contained within
	 * a node.
	 */
	private void populateBuildingLists()
	{
		NodeConfiguration n = (NodeConfiguration)nodeSelector.getSelectedItem();

		if(n != null)
		{
			containedBuildingSpots.setListData(n.buildingSpots().toArray());
			containedCommandCenter.setListData(new BuildingSpot[] {n.getCommandCenterSpot()});
		}
		else
		{
			containedBuildingSpots.setListData(new BuildingSpot[0]);
			containedCommandCenter.setListData(new BuildingSpot[0]);
		}
	}
	
	/**
	 * Refreshes the information in the node editing panel.
	 */
	public void refreshNodeEditingPanel()
	{
		nodeSelector.repaint();
		buildingSpotSelector.repaint();
		commandCenterSelector.repaint();
		
		NodeConfiguration selectedNode = (NodeConfiguration)nodeSelector.getSelectedItem();
		BuildingSpot selectedBuilding = (BuildingSpot)buildingSpotSelector.getSelectedItem();
		BuildingSpot selectedCC = (BuildingSpot)commandCenterSelector.getSelectedItem();
		
		if(selectedNode != null)
			map.setSelectedNode(selectedNode);
			
		if(selectedBuilding != null)
			map.setSelectedBuilding(selectedBuilding);
			
		if(selectedCC != null)
			map.setSelectedCommandCenter(selectedCC);
			
		populateBuildingLists();
	}

	@Override
	public void setData(Configuration c)
	{
		if(!(c instanceof MapConfiguration))
			throw new IllegalArgumentException(c + " is not a Map Configuration");
		
		editingPanelLoaded = false;
		map.loadMap((MapConfiguration)c);
	}

	@Override
	public Configuration instantiateNewConfiguration()
	{
		editingPanelLoaded = false;
		
		MapConfiguration ret = new MapConfiguration();
		map.loadMap(ret);
		return ret;
	}

	@Override
	public ConfigType getData(Configuration toSet)
	{
		if(!(toSet instanceof MapConfiguration))
			throw new IllegalArgumentException(toSet + " is not a MapConfiguration");
		
		return map.getData((MapConfiguration)toSet);
	}

	@Override
	public List<ConfigType> getAllLoadableTypes()
	{
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(BigFrameworkGuy.ConfigType.map);
		return ret;
	}

	@Override
	public JPanel getPanel()
	{
		return this;
	}

	/**
	 * Handles input to the check boxes, when a check box is highlighted its
	 * corresponding element is drawn on the map.
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class CheckBoxListener implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			Object source = e.getItemSelectable();

			if(source == startNode)
			{
				NodeConfiguration n = (NodeConfiguration)nodeSelector.getSelectedItem();
				n.setStartNode(e.getStateChange() == ItemEvent.SELECTED);
			}
			else if(source == selectNodes)
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

	/**
	 * Handles input to the radio buttons, when a radio button is selected its
	 * corresponding element is eligible to be created, deleted, or modified.
	 * 
	 * @author Ryan Tew
	 * 
	 */
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
					if(!editingPanelLoaded)
						populateNodeEditingPanel();
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
					if(!editingPanelLoaded)
						populateNodeEditingPanel();
				}
			}
			else if(source == createCommandCenter)
			{
				map.setCreateCommandCenter(selected);

				if(selected)
				{
					placeholder.setVisible(false);
					nodeEditorPanel.setVisible(true);
					if(!editingPanelLoaded)
						populateNodeEditingPanel();
				}
			}
		}
	}

	/**
	 * Handles input to the node editor buttons, these buttons handle
	 * adding/removing buildings as well as setting/removing the command center
	 * for a node.
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class NodeEditorButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();

			if(source == addBuilding)
			{
				BuildingSpot building = (BuildingSpot)buildingSpotSelector.getSelectedItem();
				NodeConfiguration node = (NodeConfiguration)nodeSelector.getSelectedItem();
				if(building == null || node == null)
					return;

				for(NodeConfiguration n : map.getNodes())
				{
					if(n.buildingSpots().contains(building))
					{
						JOptionPane.showMessageDialog(null, "That building is already owned by a node!", "ERROR",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				node.buildingSpots().add(building);
			}
			else if(source == removeBuilding)
			{
				BuildingSpot building = (BuildingSpot)buildingSpotSelector.getSelectedItem();
				NodeConfiguration node = (NodeConfiguration)nodeSelector.getSelectedItem();
				if(building == null || node == null)
					return;

				node.buildingSpots().remove(building);
			}
			else if(source == setCommandCenter)
			{
				BuildingSpot cc = (BuildingSpot)commandCenterSelector.getSelectedItem();
				NodeConfiguration node = (NodeConfiguration)nodeSelector.getSelectedItem();
				if(cc == null || node == null)
					return;

				for(NodeConfiguration n : map.getNodes())
				{
					if(n.getCommandCenterSpot() == cc)
					{
						JOptionPane.showMessageDialog(null, "That command center is already owned by a node!", "ERROR",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				node.setCommandCenterSpot(cc);
			}
			else if(source == removeCommandCenter)
			{
				NodeConfiguration node = (NodeConfiguration)nodeSelector.getSelectedItem();
				if(node == null)
					return;

				node.setCommandCenterSpot(null);
			}

			populateBuildingLists();
		}
	}

	/**
	 * When the set map button is clicked this handles the selection of the new
	 * map image.
	 * 
	 * @author Ryan Tew
	 * 
	 */
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
					JOptionPane.showMessageDialog(frame, "File could not be loaded!", "ERROR",
							JOptionPane.ERROR_MESSAGE);
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
					JOptionPane.showMessageDialog(frame, "File could not be copied!", "ERROR",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			map.setMapImage("/resources/maps/" + mapURI);
		}
	}

	/**
	 * Listens to the lane width slider and changes the selected lane's width
	 * accordingly.
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class LaneWidthListener implements ChangeListener
	{
		@Override
		public void stateChanged(ChangeEvent e)
		{
			Object source = e.getSource();

			if(source == laneWidthSlider)
				map.setLaneWidth(laneWidthSlider.getValue());
		}
	}

	/**
	 * Handles input to the node editor's combo boxes, when an item is selected
	 * in a combo box its corresponding item is highlighted on the map.
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class ComboBoxListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();

			if(source == nodeSelector)
			{
				NodeConfiguration selected = (NodeConfiguration)nodeSelector.getSelectedItem();
				if(selected != null)
				{
					map.setSelectedNode(selected);
					startNode.setSelected(selected.isStartNode());
					populateBuildingLists();
				}
			}
			else if(source == buildingSpotSelector)
			{
				map.setSelectedBuilding((BuildingSpot)buildingSpotSelector.getSelectedItem());
			}
			else if(source == commandCenterSelector)
			{
				map.setSelectedCommandCenter((BuildingSpot)commandCenterSelector.getSelectedItem());
			}
		}
	}

	/**
	 * Handles input to the node editor's building lists, building is selected
	 * in one of the lists its corresponding buliding is highlighted on the map.
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class ListListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			Object source = e.getSource();

			if(source == containedBuildingSpots)
			{
				buildingSpotSelector.setSelectedItem(containedBuildingSpots.getSelectedValue());
			}
			else if(source == containedCommandCenter)
			{
				commandCenterSelector.setSelectedItem(containedCommandCenter.getSelectedValue());
			}
		}
	}

	/**
	 * Listens to the map size text fields, adjusting the map size when they are
	 * changed
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class MapSizeListener implements DocumentListener
	{
		@Override
		public void changedUpdate(DocumentEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			Object source = e.getDocument();

			if(source == editWidth.getDocument() || source == editHeight.getDocument())
			{
				String sWidth = editWidth.getText().replace(",", "");
				String sHeight = editHeight.getText().replace(",", "");

				if(sWidth.equals(""))
					sWidth = "100";

				if(sHeight.equals(""))
					sHeight = "100";

				map.setMapSize(new Position(Double.valueOf(sWidth), Double.valueOf(sHeight)), false);
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			// TODO Auto-generated method stub

		}
	}

	/**
	 * Listens to the delete button, when it is clicked it deletes the eligible
	 * selected item from the map
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class DeleteButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			map.deleteSelectedItem();
		}
	}
}

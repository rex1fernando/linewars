package editor.mapEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.BezierCurve;
import linewars.gamestate.BuildingSpot;
import linewars.gamestate.Lane;
import linewars.gamestate.Node;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.Circle;
import linewars.gamestate.shapes.Rectangle;
import linewars.gamestate.shapes.Shape;

/**
 * The panel that contains the map in the map editor.
 * 
 * @author Ryan Tew
 * 
 */
@SuppressWarnings("serial")
public class MapPanel extends JPanel
{
	private static final double MAX_ZOOM = 0.15;
	private static final double MIN_ZOOM = 1.5;

	private static int NEXT_NODE_ID = 1;
	
	private MapEditor parent;

	private JSlider laneWidthSlider;
	private JComboBox nodeSelector;
	private JComboBox buildingSelector;
	private JComboBox commandCenterSelector;
	private JTextField mapWidthTextField;
	private JTextField mapHeightTextField;

	private double zoomLevel;
	private double lastDrawTime;

	private String mapURI;
	private double mapWidth;
	private double mapHeight;

	private Position mousePosition;
	private Rectangle2D viewport;
	private Dimension2D mapSize;

	private MapDrawer mapDrawer;
	private LaneDrawer laneDrawer;
	private NodeDrawer nodeDrawer;
	private BuildingDrawer buildingDrawer;

	private ArrayList<Lane> lanes;
	private ArrayList<Node> nodes;
	private ArrayList<BuildingSpot> buildingSpots;
	private ArrayList<BuildingSpot> commandCenters;

	private boolean moving;
	private boolean moveP1;
	private boolean moveP2;
	private boolean resizeW;
	private boolean resizeH;
	private boolean rotating;

	private Lane movingLane;
	private Node movingNode;
	private BuildingSpot movingSpot;

	private Lane selectedLane;
	private Node selectedNode;
	private BuildingSpot selectedBuilding;
	private BuildingSpot selectedCommandCenter;

	private boolean lanesVisible;
	private boolean nodesVisible;
	private boolean buildingsVisible;
	private boolean ccsVisible;

	private boolean createLane;
	private boolean createNode;
	private boolean createBuilding;
	private boolean createCC;

	/**
	 * Constructs this map panel with a default, empty map and sets the
	 * preferred size of the panel.
	 * 
	 * @param parent
	 *            The MapEditor that contains this MapPanel.
	 * @param width
	 *            The desired width of the panel.
	 * @param height
	 *            The desired height of the panel.
	 */
	public MapPanel(MapEditor parent, int width, int height)
	{
		super(null);
		setPreferredSize(new Dimension(width, height));

		this.parent = parent;
		
		laneWidthSlider = null;
		nodeSelector = null;
		buildingSelector = null;
		commandCenterSelector = null;

		// starts the user fully zoomed out
		zoomLevel = 1.0;
		mousePosition = new Position(0, 0);
		mapSize = new Dimension(100, 100);
		viewport = new Rectangle2D.Double(0, 0, 100, 100);

		mapURI = null;
		mapWidth = 100;
		mapHeight = 100;

		mapDrawer = new MapDrawer(this);
		mapDrawer.setMapSize(100, 100);
		laneDrawer = new LaneDrawer(this);
		nodeDrawer = new NodeDrawer(this);
		buildingDrawer = new BuildingDrawer(this);

		lanes = new ArrayList<Lane>();
		nodes = new ArrayList<Node>();
		buildingSpots = new ArrayList<BuildingSpot>();
		commandCenters = new ArrayList<BuildingSpot>();

		moving = false;
		moveP1 = false;
		moveP2 = false;
		resizeW = false;
		resizeH = false;
		rotating = false;

		movingLane = null;
		movingNode = null;
		movingSpot = null;

		selectedLane = null;
		selectedNode = null;

		lanesVisible = true;
		nodesVisible = true;
		buildingsVisible = true;
		ccsVisible = true;

		createLane = false;
		createNode = false;
		createBuilding = false;
		createCC = false;

		// ignores system generated repaints
		setIgnoreRepaint(true);
		setOpaque(false);

		// adds the mouse input handler
		InputHandler ih = new InputHandler();
		addMouseWheelListener(ih);
		addMouseMotionListener(ih);
		addMouseListener(ih);
	}

	/**
	 * Loads a map from a ConfigData.
	 * 
	 * @param data
	 *            The map to load.
	 * @param force
	 *            If true this indicates that all errors should be caught and
	 *            default values should be put in where needed. If false the
	 *            ConfigData is assumed to be correct and valid, if it is not an
	 *            error will most likely be thrown.
	 */
	public void loadConfigFile(ConfigData data, boolean force)
	{
		if(!force && !Boolean.getBoolean(data.getString(ParserKeys.valid)))
			throw new IllegalArgumentException("The config data object is not valid");

		List<ParserKeys> definedKeys = data.getDefinedKeys();

		lanes = new ArrayList<Lane>();
		nodes = new ArrayList<Node>();
		buildingSpots = new ArrayList<BuildingSpot>();
		commandCenters = new ArrayList<BuildingSpot>();

		if(definedKeys.contains(ParserKeys.icon))
			setMapImage(data.getString(ParserKeys.icon));
		else if(force)
		{
			setMapImage(null);
		}
		else
			throw new IllegalArgumentException("The map image is not defined");

		if(definedKeys.contains(ParserKeys.imageWidth) && definedKeys.contains(ParserKeys.imageHeight))
			setMapSize((int)(double)data.getNumber(ParserKeys.imageWidth),
					(int)(double)data.getNumber(ParserKeys.imageHeight), true);
		else if(force)
			setMapSize(100, 100, true);
		else
			throw new IllegalArgumentException("The map size is not defined");

		if(definedKeys.contains(ParserKeys.lanes))
		{
			List<ConfigData> ls = data.getConfigList(ParserKeys.lanes);
			for(ConfigData l : ls)
			{
				Lane lane;
				try
				{
					lane = new Lane(l, force);
				}
				catch (NoSuchKeyException e)
				{
					if(force)
						continue;
					else
						throw new IllegalArgumentException("A lane could not be properly constructed");
				}

				lanes.add(lane);
			}
		}
		else if(!force)
			throw new IllegalArgumentException("There are no lanes defined");

		if(definedKeys.contains(ParserKeys.nodes))
		{
			List<ConfigData> ns = data.getConfigList(ParserKeys.nodes);
			for(ConfigData n : ns)
			{
				Node newNode;
				try
				{
					newNode = new Node(n, lanes.toArray(new Lane[0]), NEXT_NODE_ID++, force);
				}
				catch (NoSuchKeyException e)
				{
					if(force)
						continue;
					else
						throw new IllegalArgumentException("A node could not be properly constructed");
				}

				nodes.add(newNode);

				commandCenters.add(newNode.getCommandCenterSpot());
				for(BuildingSpot s : newNode.getBuildingSpots())
				{
					buildingSpots.add(s);
				}
			}
		}
		else if(!force)
			throw new IllegalArgumentException("There are no nodes defined");

		for(Lane l : lanes)
		{
			Node[] nodes = l.getNodes();
			if(nodes.length == 0)
			{
				if(!force)
					throw new IllegalArgumentException("Lane " + l.getName() + " has no nodes");

				lanes.remove(l);
			}
			else if(nodes.length == 1)
			{
				if(!force)
					throw new IllegalArgumentException("Lane " + l.getName() + " has only one attached node");

				nodes[0].removeAttachedLane(l);
				lanes.remove(l);
			}
		}

		for(Node n : nodes)
		{
			if(n.getAttachedLanes().length == 0)
			{
				if(!force)
					throw new IllegalArgumentException("There is a node with no attached lanes");

				nodes.remove(n);
			}
		}
	}

	/**
	 * Creates a ConfigData that represents the map currently being displayed.
	 * 
	 * @return The ConfigData for the current map.
	 */
	public ConfigData getData()
	{
		ConfigData data = new ConfigData();

		if(mapURI == null)
			createMapImage();

		data.set(ParserKeys.icon, mapURI);
		data.set(ParserKeys.imageWidth, mapWidth);
		data.set(ParserKeys.imageHeight, mapHeight);

		for(Lane l : lanes)
		{
			data.add(ParserKeys.lanes, l.getData(null));
		}

		for(Node n : nodes)
		{
			data.add(ParserKeys.nodes, n.getData(null));
		}

		data.set(ParserKeys.valid, Boolean.toString(isValidConfig()));

		return data;
	}

	/**
	 * Determines if the map is a valid and correct map.
	 * 
	 * @return True if the map is a valid and correct map. False otherwise.
	 */
	public boolean isValidConfig()
	{
		boolean valid = mapURI != null && mapWidth != 0 && mapHeight != 0;

		for(Node n : nodes)
		{
			if(n.getCommandCenterSpot() == null)
				valid = false;
			if(n.getAttachedLanes().length == 0)
				valid = false;
		}

		return valid;
	}

	/**
	 * Creates a default map image using the current configuration of the map as
	 * a guide.
	 */
	private void createMapImage()
	{
		BufferedImage map = new BufferedImage((int)mapWidth, (int)mapHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = map.getGraphics();

		g.setColor(Color.black);
		g.fillRect(0, 0, (int)mapWidth, (int)mapHeight);

		for(Lane l : lanes)
			laneDrawer.createMap(g, l);

		for(Node n : nodes)
			nodeDrawer.createMap(g, n);

		for(BuildingSpot b : buildingSpots)
			buildingDrawer.createMap(g, b, false);

		for(BuildingSpot b : commandCenters)
			buildingDrawer.createMap(g, b, true);

		int i = 0;
		File file = null;
		boolean fileFound = false;
		while(!fileFound)
		{
			file = new File("resources/maps/map" + ++i + ".png");
			fileFound = !file.exists();
		}

		mapURI = "/resources/maps/map" + i + ".png";
		try
		{
			ImageIO.write(map, "png", file);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "The map image file " + mapURI + " could not be saved!", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Gets all of the nodes in the map.
	 * 
	 * @return All of the nodes in the map.
	 */
	public Node[] getNodes()
	{
		return nodes.toArray(new Node[0]);
	}

	/**
	 * Gets all of the buildings in the map.
	 * 
	 * @return All of the buildings in the map.
	 */
	public BuildingSpot[] getBuildingSpots()
	{
		return buildingSpots.toArray(new BuildingSpot[0]);
	}

	/**
	 * Gets all of the command centers in the map.
	 * 
	 * @return All of the command centers in the map.
	 */
	public BuildingSpot[] getCommandCenters()
	{
		return commandCenters.toArray(new BuildingSpot[0]);
	}

	/**
	 * Sets the selected node in the map.
	 * 
	 * @param n
	 *            The new selected node.
	 */
	public void setSelectedNode(Node n)
	{
		selectedNode = n;
	}

	/**
	 * Sets the selected building in the map.
	 * 
	 * @param b
	 *            The new selected building.
	 */
	public void setSelectedBuilding(BuildingSpot b)
	{
		selectedBuilding = b;
	}

	/**
	 * Sets the selected command center in the map.
	 * 
	 * @param b
	 *            The new selected command center.
	 */
	public void setSelectedCommandCenter(BuildingSpot b)
	{
		selectedCommandCenter = b;
	}

	/**
	 * Sets the nodes to be drawn on the map
	 * 
	 * @param b
	 *            True will draw the nodes. False will not.
	 */
	public void setNodesVisible(boolean b)
	{
		nodesVisible = b;
	}

	/**
	 * Sets the lanes to be drawn on the map
	 * 
	 * @param b
	 *            True will draw the lanes. False will not.
	 */
	public void setLanesVisible(boolean b)
	{
		lanesVisible = b;
	}

	/**
	 * Sets the buildings to be drawn on the map
	 * 
	 * @param b
	 *            True will draw the buildings. False will not.
	 */
	public void setBuildingsVisible(boolean b)
	{
		buildingsVisible = b;
	}

	/**
	 * Sets the command centers to be drawn on the map
	 * 
	 * @param b
	 *            True will draw the command centers. False will not.
	 */
	public void setCommandCentersVisible(boolean b)
	{
		ccsVisible = b;
	}

	/**
	 * Sets lanes to be editable.
	 * 
	 * @param b
	 *            True will allow editing of the lanes. False will not.
	 */
	public void setCreateLane(boolean b)
	{
		createLane = b;
		if(!b)
			selectedLane = null;
	}

	/**
	 * Sets nodes to be editable.
	 * 
	 * @param b
	 *            True will allow editing of the nodes. False will not.
	 */
	public void setCreateNode(boolean b)
	{
		createNode = b;
		if(!b)
			selectedNode = null;
	}

	/**
	 * Sets buildings to be editable.
	 * 
	 * @param b
	 *            True will allow editing of the buildings. False will not.
	 */
	public void setCreateBuilding(boolean b)
	{
		createBuilding = b;
	}

	/**
	 * Sets command centers to be editable.
	 * 
	 * @param b
	 *            True will allow editing of the command centers. False will
	 *            not.
	 */
	public void setCreateCommandCenter(boolean b)
	{
		createCC = b;
	}

	/**
	 * Sets the map image for the map.
	 * 
	 * @param mapURI
	 *            The location of the image.
	 */
	public void setMapImage(String mapURI)
	{
		this.mapURI = mapURI;
		Dimension dim = mapDrawer.setMap(mapURI);
		setMapSize(dim.getWidth(), dim.getHeight(), true);
	}

	/**
	 * Sets the size of the map.
	 * 
	 * @param width
	 *            The width of the map in game units.
	 * @param height
	 *            The height of the map in game units.
	 * @param setEdits
	 *            Set the map size text fields?
	 */
	public void setMapSize(double width, double height, boolean setEdits)
	{
		mapWidth = width;
		mapHeight = height;
		mapDrawer.setMapSize(width, height);
		mapSize.setSize(width, height);
		double scale = (getHeight() / height) / (getWidth() / width);
		viewport = new Rectangle2D.Double(0, 0, width, height * scale);

		if(setEdits)
		{
			mapWidthTextField.setText(Double.toString(width));
			mapHeightTextField.setText(Double.toString(height));
		}
	}

	/**
	 * Sets the reference to the JSlider controlling the width of the lane.
	 * 
	 * @param slider
	 *            The JSlider controlling the width of the lane.
	 */
	public void setLaneWidthSlider(JSlider slider)
	{
		laneWidthSlider = slider;
	}

	/**
	 * Sets the reference to the JComboBox that shows the selected node.
	 * 
	 * @param box
	 *            The JComboBox that shows the selected node.
	 */
	public void setNodeSelector(JComboBox box)
	{
		nodeSelector = box;
	}

	/**
	 * Sets the reference to the JComboBox that shows the selected building.
	 * 
	 * @param box
	 *            The JComboBox that shows the selected building.
	 */
	public void setBuildingSelector(JComboBox box)
	{
		buildingSelector = box;
	}

	/**
	 * Sets the reference to the JComboBox that shows the selected command
	 * center.
	 * 
	 * @param box
	 *            The JComboBox that shows the selected command center.
	 */
	public void setCommandCenterSelector(JComboBox box)
	{
		commandCenterSelector = box;
	}

	/**
	 * Sets the reference to the JTextField the displays the width of the map.
	 * 
	 * @param t
	 *            The JTextFeild that displays the width of the map.
	 */
	public void setMapWidthTextField(JTextField t)
	{
		mapWidthTextField = t;
	}

	/**
	 * Sets the reference to the JTextField the displays the height of the map.
	 * 
	 * @param t
	 *            The JTextFeild that displays the height of the map.
	 */
	public void setMapHeightTextField(JTextField t)
	{
		mapHeightTextField = t;
	}

	/**
	 * Sets the width of the selected lane.
	 * 
	 * @param width
	 *            The percentage of the width of the map that the lane width
	 *            will be.
	 */
	public void setLaneWidth(double width)
	{
		if(selectedLane != null)
			selectedLane.setWidth(mapHeight * (width / 100));
	}

	/**
	 * Deletes selected item that is eligible for editing.
	 */
	public void deleteSelectedItem()
	{
		if(createLane)
		{
			Node[] attachedNodes = selectedLane.getNodes();
			for(Node n : attachedNodes)
			{
				n.removeAttachedLane(selectedLane);
			}

			lanes.remove(selectedLane);
			selectedLane = null;
		}
		else if(createNode)
		{
			Lane[] attachedLanes = selectedNode.getAttachedLanes();
			for(Lane l : attachedLanes)
			{
				Node[] attachedNodes = l.getNodes();
				for(Node n : attachedNodes)
				{
					n.removeAttachedLane(l);
				}

				lanes.remove(l);
			}

			nodes.remove(selectedNode);
			nodeSelector.removeItem(selectedNode);
			selectedNode = null;
			
			parent.refreshNodeEditingPanel();
		}
		else if(createBuilding)
		{
			for(Node n : nodes)
			{
				if(n.getBuildingSpots().contains(selectedBuilding))
				{
					n.removeBuildingSpot(selectedBuilding);
				}
			}

			buildingSpots.remove(selectedBuilding);
			buildingSelector.removeItem(selectedBuilding);
			selectedBuilding = null;
			
			parent.refreshNodeEditingPanel();
		}
		else if(createCC)
		{
			for(Node n : nodes)
			{
				if(n.getCommandCenterSpot() == selectedCommandCenter)
				{
					n.removeCommandCenterSpot();
				}
			}

			commandCenters.remove(selectedCommandCenter);
			commandCenterSelector.removeItem(selectedCommandCenter);
			selectedCommandCenter = null;
			
			parent.refreshNodeEditingPanel();
		}
	}

	/**
	 * Converts the given position from screen coordinates to game coordinates.
	 * 
	 * @param screenCoord
	 *            The position to be converted.
	 * @return The position in game coordinates.
	 */
	public Position toGameCoord(Position screenCoord)
	{
		double scale = getWidth() / viewport.getWidth();
		return new Position((screenCoord.getX() / scale) + viewport.getX(), (screenCoord.getY() / scale)
				+ viewport.getY());
	}

	/**
	 * Converts the given position from game coordinates to screen coordinates.
	 * 
	 * @param screenCoord
	 *            The position to be converted.
	 * @return The position in screencoordinates.
	 */
	public Position toScreenCoord(Position gameCoord)
	{
		double scale = getWidth() / viewport.getWidth();
		return new Position((gameCoord.getX() - viewport.getX()) * scale, (gameCoord.getY() - viewport.getY()) * scale);
	}

	/**
	 * Draws everything to the screen.
	 */
	@Override
	public void paint(Graphics g)
	{
		long curTime = System.currentTimeMillis();
		double fps = 1000.0 / (curTime - lastDrawTime);
		lastDrawTime = curTime;

		double scale = getWidth() / viewport.getWidth();
		updateViewPortPan(fps, scale);

		// fill the background black
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());

		// draw the map
		mapDrawer.draw(g, viewport, scale);

		// draw the lanes
		if(lanesVisible)
		{
			for(Lane l : lanes)
			{
				laneDrawer.draw(g, l, selectedLane == l, toGameCoord(mousePosition), scale);
			}
		}

		// draw the nodes
		if(nodesVisible)
		{
			for(Node n : nodes)
			{
				nodeDrawer.draw(g, n, selectedNode == n, toGameCoord(mousePosition), scale);
			}
		}

		// draw the building spots
		if(buildingsVisible)
		{
			for(BuildingSpot b : buildingSpots)
			{
				buildingDrawer.draw(g, b, b == selectedBuilding, toGameCoord(mousePosition), scale);
			}
		}

		// draw the command centers
		if(ccsVisible)
		{
			for(BuildingSpot b : commandCenters)
			{
				buildingDrawer.draw(g, b, b == selectedCommandCenter, toGameCoord(mousePosition), scale, true);
			}
		}

		this.repaint();
	}

	/**
	 * Determines if the viewport needs to be moved and moves it.
	 * 
	 * @param fps
	 *            The current framerate, used to calculate how far to move the
	 *            viewport.
	 * @param scale
	 *            The current scale factor between the veiwport and the screen.
	 */
	private void updateViewPortPan(double fps, double scale)
	{
		if(mousePosition == null)
			return;

		double moveX = 0.0;
		double moveY = 0.0;

		if(mousePosition.getX() < 25)
		{
			moveX = (-1000 / fps) / scale;
		}
		else if(mousePosition.getX() > getWidth() - 25)
		{
			moveX = (1000 / fps) / scale;
		}

		if(mousePosition.getY() < 25)
		{
			moveY = (-1000 / fps) / scale;
		}
		else if(mousePosition.getY() > getHeight() - 25)
		{
			moveY = (1000 / fps) / scale;
		}

		updateViewPort(moveX, moveY, viewport.getWidth(), viewport.getHeight(), false);
	}

	/**
	 * Moves the viewport but makes sure it is still over the map.
	 * 
	 * @param viewX
	 *            The new X position of the viewport.
	 * @param viewY
	 *            The new Y position of the viewport.
	 * @param newW
	 *            The new width position of the viewport.
	 * @param newH
	 *            The new height position of the viewport.
	 * @param zooming
	 *            True if we are zooming. False if we are panning.
	 */
	private void updateViewPort(double viewX, double viewY, double newW, double newH, boolean zooming)
	{
		double oldX = viewport.getX();
		double oldY = viewport.getY();
		double moveX = 0;
		double moveY = 0;

		// calculates the new x for the viewport
		double newX = oldX + viewX;
		if(newX < 0)
		{
			double x = 0;
			moveX += x - newX;
			newX = x;
		}
		if(newX > mapSize.getWidth() - newW)
		{
			double x = mapSize.getWidth() - newW;
			moveX += x - newX;
			newX = x;
		}
		if(newW > mapSize.getWidth())
		{
			double x = (mapSize.getWidth() - newW) / 2;
			moveX += x - newX;
			newX = x;
		}

		// calculates the new y for the viewport
		double newY = oldY + viewY;
		if(newY < 0)
		{
			double y = 0;
			moveY += y - newY;
			newY = y;
		}
		if(newY > mapSize.getHeight() - newH)
		{
			double y = mapSize.getHeight() - newH;
			moveY += y - newY;
			newY = y;
		}
		if(newH > mapSize.getHeight())
		{
			double y = (mapSize.getHeight() - newH) / 2;
			moveY += y - newY;
			newY = y;
		}

		if(zooming)
		{
			moveItem(new Position(moveX, moveY));
		}
		else
		{
			moveItem(new Position(newX - oldX, newY - oldY));
		}

		viewport.setRect(newX, newY, newW, newH);
	}

	/**
	 * Moves the item being modified by the vector change.
	 * 
	 * @param change
	 *            The amount to move the item.
	 */
	private void moveItem(Position change)
	{
		if(movingNode != null)
		{
			moveNode(change);
			parent.refreshNodeEditingPanel();
		}
		else if(movingSpot != null)
		{
			moveSpot(change);
			parent.refreshNodeEditingPanel();
		}
		else if(movingLane != null)
		{
			moveLane(change);
		}
	}

	/**
	 * Adjusts the lane that is being modified.
	 * 
	 * @param change
	 *            The amount to adjust by.
	 */
	private void moveLane(Position change)
	{
		if(moveP1)
		{
			moveP0andP1(change, movingLane);
		}
		else if(moveP2)
		{
			moveP2andP3(change, movingLane);
		}
	}

	/**
	 * Moves P1 of l by the vector change. While doing this it keeps P0 on the
	 * line between the node on that end and P1.
	 * 
	 * @param change
	 *            The amount to move P1 by.
	 * @param l
	 *            The lane to modify.
	 */
	private void moveP0andP1(Position change, Lane l)
	{
		BezierCurve curve = l.getCurve();
		Position newP1 = curve.getP1().add(change);

		Node node = l.getNodes()[0];
		Position nodePos = node.getTransformation().getPosition();
		Position pointingVec = newP1.subtract(nodePos).normalize();
		Position newP0 = pointingVec.scale(node.getBoundingCircle().getRadius()).add(nodePos);

		curve.setP0(newP0);
		curve.setP1(newP1);
	}

	/**
	 * Moves P2 of l by the vector change. While doing this it keeps P3 on the
	 * line between the node on that end and P2.
	 * 
	 * @param change
	 *            The amount to move P2 by.
	 * @param l
	 *            The lane to modify.
	 */
	private void moveP2andP3(Position change, Lane l)
	{
		BezierCurve curve = l.getCurve();
		Position newP2 = curve.getP2().add(change);

		Node node = l.getNodes()[1];
		Position nodePos = node.getTransformation().getPosition();
		Position pointingVec = newP2.subtract(nodePos).normalize();
		Position newP3 = pointingVec.scale(node.getBoundingCircle().getRadius()).add(nodePos);

		curve.setP2(newP2);
		curve.setP3(newP3);
	}

	/**
	 * Adjusts the node that is being modified.
	 * 
	 * @param change
	 *            The amount to adjust by.
	 */
	private void moveNode(Position change)
	{
		Shape s = movingNode.getShape();

		if(moving)
		{
			s = s.transform(new Transformation(change, 0));
		}
		else if(resizeW || resizeH)
		{
			double newRadius = toGameCoord(mousePosition).subtract(s.position().getPosition()).length();
			s = new Circle(s.position(), newRadius);
		}

		movingNode.setShape(s);

		for(Lane l : movingNode.getAttachedLanes())
		{
			Node[] nodeArr = l.getNodes();
			if(movingNode == nodeArr[0])
			{
				moveP0andP1(new Position(0, 0), l);
			}
			else if(movingNode == nodeArr[1])
			{
				moveP2andP3(new Position(0, 0), l);
			}
		}
	}

	/**
	 * Adjusts the BuildingSpot that is being modified.
	 * 
	 * @param change
	 *            The amount to adjust by.
	 */
	private void moveSpot(Position change)
	{
		Rectangle r = movingSpot.getRect();

		if(moving)
		{
			r = (Rectangle)r.transform(new Transformation(change, 0));

			movingSpot.setRect(r);
		}
		else
		{
			if(rotating)
			{
				Position mouse = toGameCoord(mousePosition);
				Position rectPos = r.position().getPosition();

				Position rotatedVector = mouse.subtract(rectPos);
				double newRotation = Math.atan2(rotatedVector.getY(), rotatedVector.getX());
				double currentRotation = r.position().getRotation();
				r = (Rectangle)r.transform(new Transformation(new Position(0, 0), newRotation - currentRotation
						- Math.PI / 4));
				movingSpot.setRect(r);
			}

			double width = r.getWidth();
			double height = r.getHeight();
			if(resizeW)
			{
				Position[] corners = r.getVertexPositions();
				Position axis = corners[3].subtract(r.position().getPosition())
						.add(corners[0].subtract(r.position().getPosition())).normalize();
				Position ray = toGameCoord(mousePosition).subtract(r.position().getPosition());

				width = ray.length() * Math.abs(axis.dot(ray.normalize())) * 2;
			}
			if(resizeH)
			{
				Position[] corners = r.getVertexPositions();
				Position axis = corners[1].subtract(r.position().getPosition())
						.add(corners[0].subtract(r.position().getPosition())).normalize();
				Position ray = toGameCoord(mousePosition).subtract(r.position().getPosition());

				height = ray.length() * Math.abs(axis.dot(ray.normalize())) * 2;
			}

			if(width < 25)
				width = 25;
			if(height < 25)
				height = 25;

			movingSpot.setDim((int)width, (int)height);
		}
	}

	/**
	 * Determines if a lane has been selected and which point on the lane was
	 * selected.
	 * 
	 * @param p
	 *            The position that was clicked.
	 */
	private void selectLane(Position p)
	{
		double scale = getWidth() / viewport.getWidth();
		for(Lane l : lanes)
		{
			BezierCurve curve = l.getCurve();
			Position p1 = curve.getP1();
			Position p2 = curve.getP2();
			Circle c1 = new Circle(new Transformation(p1, 0), 5 / scale);
			Circle c2 = new Circle(new Transformation(p2, 0), 5 / scale);

			if(c1.positionIsInShape(p))
			{
				movingLane = l;
				moveP1 = true;
				break;
			}
			else if(c2.positionIsInShape(p))
			{
				movingLane = l;
				moveP2 = true;
				break;
			}
		}

		selectedLane = movingLane;
		if(selectedLane != null && laneWidthSlider != null)
			laneWidthSlider.setValue((int)(selectedLane.getWidth() / mapHeight * 100));

		if(!moveP1 && !moveP2)
		{
			for(Node n : nodes)
			{
				Circle c = n.getBoundingCircle();
				if(c.positionIsInShape(p))
				{
					selectedNode = n;
					break;
				}
			}
		}
	}

	/**
	 * Determines if a node has been selected and if the node is going to be
	 * moved or resized.
	 * 
	 * @param p
	 *            The position that was clicked.
	 */
	private void selectNode(Position p)
	{
		double scale = getWidth() / viewport.getWidth();
		for(Node n : nodes)
		{
			Circle c = n.getBoundingCircle();
			Circle dragNode = new Circle(c.position(), c.getRadius() - 2.5 / scale);
			Circle resizeNode = new Circle(c.position(), c.getRadius() + 2.5 / scale);

			if(dragNode.positionIsInShape(p))
			{
				movingNode = n;
				moving = true;
				break;
			}
			else if(resizeNode.positionIsInShape(p))
			{
				movingNode = n;
				resizeW = true;
				resizeH = true;
				break;
			}
		}

		if(!moving && !resizeW && !resizeH)
		{
			movingNode = new Node(p, NEXT_NODE_ID++);
			nodes.add(movingNode);
			nodeSelector.addItem(movingNode);

			resizeW = true;
			resizeH = true;
		}

		selectedNode = movingNode;
		nodeSelector.setSelectedItem(movingNode);
	}

	/**
	 * Determines if a building has been selected and if the building is going
	 * to be moved or resized.
	 * 
	 * @param p
	 *            The position that was clicked.
	 */
	private void selectBuilding(Position p)
	{
		selectBuildingSpot(p, buildingSpots);

		if(!moving && !resizeW && !resizeH && !rotating)
		{
			movingSpot = new BuildingSpot(p);
			buildingSpots.add(movingSpot);
			buildingSelector.addItem(movingSpot);

			resizeW = true;
			resizeH = true;
			rotating = true;
		}

		selectedBuilding = movingSpot;
		buildingSelector.setSelectedItem(movingSpot);
	}

	/**
	 * Determines if a command center has been selected and if the command
	 * center is going to be moved or resized.
	 * 
	 * @param p
	 *            The position that was clicked.
	 */
	private void selectCommandCenter(Position p)
	{
		selectBuildingSpot(p, commandCenters);

		if(!moving && !resizeW && !resizeH && !rotating)
		{
			movingSpot = new BuildingSpot(p);
			commandCenters.add(movingSpot);
			commandCenterSelector.addItem(movingSpot);

			resizeW = true;
			resizeH = true;
			rotating = true;
		}

		selectedCommandCenter = movingSpot;
		commandCenterSelector.setSelectedItem(movingSpot);
	}

	/**
	 * Determines if a BuildingSpot has been selected and if the BuildingSpot is
	 * going to be moved or resized.
	 * 
	 * @param p
	 *            The position that was clicked.
	 */
	private void selectBuildingSpot(Position p, ArrayList<BuildingSpot> spots)
	{
		double scale = getWidth() / viewport.getWidth();
		for(BuildingSpot b : spots)
		{
			Rectangle r = b.getRect();
			Rectangle dragSpot = new Rectangle(r.position(), r.getWidth() - 2.5 / scale, r.getHeight() - 2.5 / scale);
			Rectangle resizeWidth = new Rectangle(r.position(), r.getWidth() + 2.5 / scale, r.getHeight() - 2.5 / scale);
			Rectangle resizeHeight = new Rectangle(r.position(), r.getWidth() - 2.5 / scale, r.getHeight() + 2.5
					/ scale);

			Position circlePos = r.getVertexPositions()[0];
			Circle rotate = new Circle(new Transformation(circlePos, 0), 5 / scale);

			if(rotate.positionIsInShape(p))
			{
				movingSpot = b;
				rotating = true;
				break;
			}
			else if(dragSpot.positionIsInShape(p))
			{
				movingSpot = b;
				moving = true;
				break;
			}
			else
			{
				if(resizeWidth.positionIsInShape(p))
				{
					movingSpot = b;
					resizeW = true;
				}
				if(resizeHeight.positionIsInShape(p))
				{
					movingSpot = b;
					resizeH = true;
				}

				if(resizeW || resizeH)
					break;
			}
		}
	}

	/**
	 * Checks to see if the mouse was released within a node, if it was this
	 * will create a new lane with a default width between that node and the
	 * selected node.
	 * 
	 * @param p
	 *            The position the mouse was released at.
	 */
	private void createLane(Position p)
	{
		Node otherNode = null;
		for(Node n : nodes)
		{
			Circle c = n.getBoundingCircle();
			if(c.positionIsInShape(p))
			{
				otherNode = n;
				break;
			}
		}

		if(otherNode != null)
		{
			// determine the order to add the nodes, this prevents the lane
			// from being "twisted" when the file is loaded again
			ArrayList<Node> attachedNodes = new ArrayList<Node>();
			for(Node n : nodes)
			{
				if(n == selectedNode)
					attachedNodes.add(selectedNode);
				else if(n == otherNode)
					attachedNodes.add(otherNode);
			}

			Lane l = new Lane(attachedNodes.get(0), attachedNodes.get(1));
			selectedLane = l;

			if(laneWidthSlider != null)
				laneWidthSlider.setValue((int)(l.getWidth() / mapHeight * 100));

			lanes.add(l);
			selectedNode.addAttachedLane(l);
			otherNode.addAttachedLane(l);
		}
	}

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

	/**
	 * Handles the mouse events for this MapPanel.
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class InputHandler extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			Position p = toGameCoord(new Position(e.getPoint().getX(), e.getPoint().getY()));

			if(createLane)
			{
				selectLane(p);
			}
			else if(createNode)
			{
				selectNode(p);
			}
			else if(createBuilding)
			{
				selectBuilding(p);
			}
			else if(createCC)
			{
				selectCommandCenter(p);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			Position newPos = new Position(e.getPoint().getX(), e.getPoint().getY());
			Position change = toGameCoord(newPos).subtract(toGameCoord(mousePosition));
			mousePosition = newPos;

			moveItem(change);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			Position p = new Position(e.getPoint().getX(), e.getPoint().getY());

			if(createLane && selectedNode != null)
				createLane(toGameCoord(p));

			moving = false;
			moveP1 = false;
			moveP2 = false;
			resizeW = false;
			resizeH = false;
			rotating = false;

			movingNode = null;
			movingSpot = null;
			movingLane = null;

			if(createLane)
			{
				selectedNode = null;
			}
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			mousePosition = new Position(e.getPoint().getX(), e.getPoint().getY());
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			// makes sure the zoom is within the max and min range
			double newZoom = zoomLevel + e.getWheelRotation() * Math.exp(zoomLevel) * 0.04;
			if(newZoom < MAX_ZOOM)
				newZoom = MAX_ZOOM;
			if(newZoom > MIN_ZOOM)
				newZoom = MIN_ZOOM;

			// calculates the ratios of the zoom and position
			double zoomRatio = newZoom / zoomLevel;
			double ratio = viewport.getWidth() / getWidth();

			// converts the mouse point to the game space
			double mouseX = mousePosition.getX() * ratio;
			double mouseY = mousePosition.getY() * ratio;

			// calculates the change in postion of the viewport
			double viewX = mouseX - mouseX * zoomRatio;
			double viewY = mouseY - mouseY * zoomRatio;

			// calculates the new dimension of the viewport
			double newW = viewport.getWidth() * zoomRatio;
			double newH = viewport.getHeight() * zoomRatio;

			updateViewPort(viewX, viewY, newW, newH, true);
			zoomLevel = newZoom;
		}
	}
}

package editor.mapEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Position;

public class MapPanel extends JPanel
{
	private static final double MAX_ZOOM = 0.15;
	private static final double MIN_ZOOM = 1.5;

	/**
	 * Measures how much the user has zoomed in, where 100% is fully zoomed
	 * in and 0% is fully zoomed out.
	 */
	private double zoomLevel;
	
	private double lastDrawTime;

	private Position mousePosition;
	private Position lastClickPosition;
	private Rectangle2D viewport;
	private Dimension2D mapSize;
	
	private TerrainLayer terrain;
	private ColoredEdge laneDrawer;
	private ColoredNode nodeDrawer;
	private BuildingDrawer buildingDrawer;
	
	private ArrayList<Lane> lanes;
	private ArrayList<Node> nodes;
	private ArrayList<BuildingSpot> buildingSpots;
	private ArrayList<BuildingSpot> commandCenters;
	
	private boolean lanesVisible;
	private boolean nodesVisible;
	private boolean buildingsVisible;
	private boolean ccsVisible;
	
	private boolean createLane;
	private boolean createNode;
	private boolean createBuilding;
	private boolean createCC;

	public MapPanel(int width, int height)
	{
		super(null);
		setPreferredSize(new Dimension(width  - 150, height - 75));
		
		// starts the user fully zoomed out
		zoomLevel = 1.0;
		mousePosition = new Position(0, 0);
		lastClickPosition = new Position(0, 0);
		mapSize = new Dimension(0, 0);
		viewport = new Rectangle2D.Double(0, 0, 0, 0);
		
		terrain = new TerrainLayer(this);
		laneDrawer = new ColoredEdge(this);
		nodeDrawer = new ColoredNode(this);
		buildingDrawer = new BuildingDrawer(this);
		
		lanes = new ArrayList<Lane>();
		nodes = new ArrayList<Node>();
		buildingSpots = new ArrayList<BuildingSpot>();
		commandCenters = new ArrayList<BuildingSpot>();
		
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
	
	public void loadConfigFile(ConfigData data)
	{
		lanes = new ArrayList<Lane>();
		nodes = new ArrayList<Node>();

		setMapImage(data.getString(ParserKeys.icon));
		setMapSize((int)(double)data.getNumber(ParserKeys.imageWidth), (int)(double)data.getNumber(ParserKeys.imageHeight));
		
		lanes = new ArrayList<Lane>();
		List<ConfigData> ls = data.getConfigList(ParserKeys.lanes);
		for(ConfigData l : ls)
			lanes.add(new Lane(l));
		
		nodes = new ArrayList<Node>();
		buildingSpots = new ArrayList<BuildingSpot>();
		commandCenters = new ArrayList<BuildingSpot>();
		List<ConfigData> ns = data.getConfigList(ParserKeys.nodes);
		for(ConfigData n : ns)
		{
			Node newNode = new Node(n, lanes.toArray(new Lane[0]), nodes.size());
			nodes.add(newNode);
			
			commandCenters.add(newNode.getCommandCenter());
			for(BuildingSpot s : newNode.getBuildingSpots())
			{
				buildingSpots.add(s);
			}
		}
	}
	
	public void setNodesVisible(boolean b)
	{
		nodesVisible = b;
	}
	
	public void setLanesVisible(boolean b)
	{
		lanesVisible = b;
	}
	
	public void setBuildingsVisible(boolean b)
	{
		buildingsVisible = b;
	}
	
	public void setCommandCentersVisible(boolean b)
	{
		ccsVisible = b;
	}
	
	public void setCreateLane(boolean b)
	{
		createLane = b;
	}
	
	public void setCreateNode(boolean b)
	{
		createNode = b;
	}
	
	public void setCreateBuilding(boolean b)
	{
		createBuilding = b;
	}
	
	public void setCreateCommandCenter(boolean b)
	{
		createCC = b;
	}
	
	public void setMapImage(String mapURI)
	{
		terrain.setMap(mapURI);
	}
	
	public void setMapSize(double width, double height)
	{
		mapSize.setSize(width, height);
		viewport.setRect(0, 0, width, height);
	}

	public Position toGameCoord(Position screenCoord)
	{
		double scale = getWidth() / viewport.getWidth();
		return new Position((screenCoord.getX() / scale) + viewport.getX(), (screenCoord.getY() / scale) + viewport.getY());
	}

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
				
		//fill the background black
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		//draw the map
		terrain.draw(g, viewport, scale);
		
		//draw the lanes
		if(lanesVisible)
		{
			for(Lane l : lanes)
			{
				laneDrawer.draw(g, l, scale);
			}
		}	
		
		//draw the nodes
		if(nodesVisible)
		{
			for(Node n : nodes)
			{
				nodeDrawer.draw(g, n, scale);
			}
		}
		
		//draw the building spots
		if(buildingsVisible)
		{
			for(BuildingSpot b : buildingSpots)
			{
				buildingDrawer.draw(g, b, scale);
			}
		}
		
		//draw the command centers
		if(ccsVisible)
		{
			for(BuildingSpot b : commandCenters)
			{
				buildingDrawer.draw(g, b, scale, true);
			}
		}
		
		this.repaint();
	}
	
	private void updateViewPortPan(double fps, double scale)
	{
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
		
		updateViewPort(moveX, moveY, viewport.getWidth(), viewport.getHeight());
	}

	private void updateViewPort(double viewX, double viewY, double newW, double newH)
	{
		// calculates the new x for the viewport
		double newX = viewport.getX() + viewX;
		if(newX < 0)
			newX = 0;
		if(newX > mapSize.getWidth() - newW)
			newX = mapSize.getWidth() - newW;
		if(newW > mapSize.getWidth())
			newX = (mapSize.getWidth() - newW) / 2;
	
		// calculates the new y for the viewport
		double newY = viewport.getY() + viewY;
		if(newY < 0)
			newY = 0;
		if(newY > mapSize.getHeight() - newH)
			newY = mapSize.getHeight() - newH;
		if(newH > mapSize.getHeight())
			newY = (mapSize.getHeight() - newH) / 2;
	
		viewport.setRect(newX, newY, newW, newH);
	}

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

	private class InputHandler extends MouseAdapter
	{
		@Override
		public void mouseReleased(MouseEvent e)
		{
			Position p = new Position(e.getPoint().getX(), e.getPoint().getY());
			lastClickPosition = toGameCoord(p);
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			mousePosition = new Position(e.getPoint().getX(),e.getPoint().getY());
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

			updateViewPort(viewX, viewY, newW, newH);
			zoomLevel = newZoom;
		}
	}
}

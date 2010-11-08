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
import linewars.gamestate.BezierCurve;
import linewars.gamestate.BuildingSpot;
import linewars.gamestate.Lane;
import linewars.gamestate.Node;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.Circle;
import linewars.gamestate.shapes.Rectangle;
import linewars.gamestate.shapes.Shape;

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
	
	private MapDrawer mapDrawer;
	private LaneDrawer laneDrawer;
	private NodeDrawer nodeDrawer;
	private BuildingDrawer buildingDrawer;
	
	private ArrayList<Lane> lanes;
	private ArrayList<Node> nodes;
	private ArrayList<BuildingSpot> buildingSpots;
	private ArrayList<BuildingSpot> commandCenters;
	
	private boolean moving;
	private boolean resizeW;
	private boolean resizeH;
	private boolean rotating;
	private BezierCurve movingCurve;
	private Node movingNode;
	private BuildingSpot movingSpot;
	
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
		
		mapDrawer = new MapDrawer(this);
		laneDrawer = new LaneDrawer(this);
		nodeDrawer = new NodeDrawer(this);
		buildingDrawer = new BuildingDrawer(this);
		
		lanes = new ArrayList<Lane>();
		nodes = new ArrayList<Node>();
		buildingSpots = new ArrayList<BuildingSpot>();
		commandCenters = new ArrayList<BuildingSpot>();
		
		moving = false;
		resizeW = false;
		resizeH = false;
		rotating = false;
		
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
			Node newNode = new Node(n, lanes.toArray(new Lane[0]));
			nodes.add(newNode);
			
			commandCenters.add(newNode.getCommandCenterSpot());
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
		mapDrawer.setMap(mapURI);
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
		mapDrawer.draw(g, viewport, scale);
		
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
		
		updateViewPort(moveX, moveY, viewport.getWidth(), viewport.getHeight(), false);
	}

	private void updateViewPort(double viewX, double viewY, double newW, double newH, boolean zooming)
	{
		double oldX = viewport.getX();
		double oldY = viewport.getY() ;
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

	private void moveItem(Position change)
	{
		if(movingNode != null)
		{
			moveNode(change);
		}
		else if(movingSpot != null)
		{
			moveSpot(change);
		}
		else if(movingCurve != null)
		{
			
		}
	}

	private void moveNode(Position change)
	{
		//TODO adjust attached lane control points
		//TODO adjust contained building spots
		//TODO adjust command center
		//TODO make sure only one command center is in the node
		
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
	}
	
	private void moveSpot(Position change)
	{
		Rectangle r = movingSpot.getRect();
		
		if(moving)
		{
			r = (Rectangle)r.transform(new Transformation(change, 0));
			
			movingSpot.setRect(r);
		}
		else if(rotating)
		{
			Position axis = new Position(1, 0);
			Position ray = toGameCoord(mousePosition).subtract(r.position().getPosition());
			//TODO find the rotation
		}
		else
		{
			double width = r.getWidth();
			double height = r.getHeight();
			if(resizeW)
			{
				double rotation = r.position().getRotation();
				Position axis = new Position(Math.cos(rotation), Math.sin(rotation));
				Position ray = toGameCoord(mousePosition).subtract(r.position().getPosition());
				
				width = ray.length() * Math.abs(axis.dot(ray.normalize())) * 2;
			}
			if(resizeH)
			{
				double rotation = r.position().getRotation();
				Position axis = new Position(Math.sin(rotation), Math.cos(rotation));
				Position ray = toGameCoord(mousePosition).subtract(r.position().getPosition());
				
				height = ray.length() * Math.abs(axis.dot(ray.normalize())) * 2;
			}
			
			movingSpot.setDim((int)width, (int)height);
		}
	}

	private void selectNode(Position p)
	{
		for(Node n : nodes)
		{
			Circle c = n.getBoundingCircle();
			Circle dragNode = new Circle(c.position(), c.getRadius() - 25);
			Circle resizeNode = new Circle(c.position(), c.getRadius() + 25);
			
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
			movingNode = new Node(p);
			nodes.add(movingNode);
			
			resizeW = true;
			resizeH = true;
		}
	}

	private void selectBuilding(Position p)
	{
		selectBuildingSpot(p, buildingSpots);
		
		if(!moving && !resizeW && !resizeH && !rotating)
		{
			movingSpot = new BuildingSpot(p);
			buildingSpots.add(movingSpot);
			
			resizeW = true;
			resizeH = true;
		}
	}
	
	private void selectCommandCenter(Position p)
	{
		selectBuildingSpot(p, commandCenters);
		
		if(!moving && !resizeW && !resizeH && !rotating)
		{
			movingSpot = new BuildingSpot(p);
			commandCenters.add(movingSpot);
			
			resizeW = true;
			resizeH = true;
		}
	}

	private void selectBuildingSpot(Position p, ArrayList<BuildingSpot> spots)
	{
		for(BuildingSpot b : spots)
		{
			Rectangle r = b.getRect();
			Rectangle dragSpot = new Rectangle(r.position(), r.getWidth() - 25, r.getHeight() - 25);
			Rectangle resizeWidth = new Rectangle(r.position(), r.getWidth() + 25, r.getHeight());
			Rectangle resizeHeight = new Rectangle(r.position(), r.getWidth(), r.getHeight() + 25);
			
			Position rectPos = r.position().getPosition();
			Position circlePos = new Position(rectPos.getX() + r.getWidth() / 2, rectPos.getY());
			Circle rotate = new Circle(new Transformation(circlePos, 0), 25);
			
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

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

	private class InputHandler extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			Position p = toGameCoord(new Position(e.getPoint().getX(), e.getPoint().getY()));
			
			if(createLane)
			{
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
			Position newPos = new Position(e.getPoint().getX(),e.getPoint().getY());
			Position change = toGameCoord(newPos).subtract(toGameCoord(mousePosition));
			mousePosition = newPos;

			moveItem(change);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			Position p = new Position(e.getPoint().getX(), e.getPoint().getY());
			lastClickPosition = toGameCoord(p);

			moving = false;
			resizeW = false;
			resizeH = false;
			rotating = false;
			movingNode = null;
			movingSpot = null;
			movingCurve = null;
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

			updateViewPort(viewX, viewY, newW, newH, true);
			zoomLevel = newZoom;
		}
	}
}

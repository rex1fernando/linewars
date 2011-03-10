package linewars.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import linewars.display.layers.GraphLayer;
import linewars.display.layers.ILayer;
import linewars.display.layers.MapItemLayer;
import linewars.display.layers.MapItemLayer.MapItemType;
import linewars.display.layers.TerrainLayer;
import linewars.display.panels.CommandCardPanel;
import linewars.display.panels.ExitButtonPanel;
import linewars.display.panels.NodeStatusPanel;
import linewars.display.panels.ResourceDisplayPanel;
import linewars.display.panels.TechButtonPanel;
import linewars.display.panels.TechPanel;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.BezierCurve;
import linewars.gamestate.GameState;
import linewars.gamestate.Lane;
import linewars.gamestate.Map;
import linewars.gamestate.Node;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.Race;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.shapes.Rectangle;
import linewars.network.MessageReceiver;
import linewars.network.messages.AdjustFlowDistributionMessage;

/**
 * Encapsulates the display information.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
@SuppressWarnings("serial")
public class Display extends JFrame implements Runnable
{
	private static final boolean DEBUG_MODE = true;

	/**
	 * The threshold when zooming out where the view switches from tactical view
	 * to strategic view and vice versa.
	 */
	private static final double ZOOM_THRESHOLD = 1.0;

	private static final double MAX_ZOOM = 0.15;
	private static final double MIN_ZOOM = 1.5;

	private GameStateProvider gameStateProvider;
	private MessageReceiver messageReceiver;
	private GamePanel gamePanel;

	private int adjustingFlowDist;
	private boolean adjustingFlow1;
	private boolean clicked;

	private int playerIndex;

	/**
	 * Creates and initializes the Display.
	 * 
	 * @param provider
	 *            The GameStateProvider for the display.
	 * @param receiver
	 *            The MessageReceiver to send messages to.
	 * @param curPlayer
	 *            The index of the player this Display belongs to.
	 */
	public Display(GameStateProvider provider, MessageReceiver receiver, int curPlayer)
	{
		super("Line Wars");

		playerIndex = curPlayer;
		adjustingFlowDist = -1;
		clicked = false;

		messageReceiver = receiver;
		gameStateProvider = provider;
		gamePanel = new GamePanel();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(gamePanel);
		setSize(new Dimension(800, 600));
		setUndecorated(!DEBUG_MODE);
	}

	@Override
	public void run()
	{
		// shows the display
		setVisible(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	/**
	 * Gets the width of the GamePanel.
	 * 
	 * @return The width of the GamePanel.
	 */
	public int getScreenWidth()
	{
		return gamePanel.getWidth();
	}

	/**
	 * Gets the height of the GamePanel.
	 * 
	 * @return The height of the GamePanel.
	 */
	public int getScreenHeight()
	{
		return gamePanel.getHeight();
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
		double scale = gamePanel.getWidth() / gamePanel.viewport.getWidth();
		return new Position((screenCoord.getX() / scale) + gamePanel.viewport.getX(), (screenCoord.getY() / scale)
				+ gamePanel.viewport.getY());
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
		double scale = gamePanel.getWidth() / gamePanel.viewport.getWidth();
		return new Position((gameCoord.getX() - gamePanel.viewport.getX()) * scale,
				(gameCoord.getY() - gamePanel.viewport.getY()) * scale);
	}
	
	public void loadDisplayResources()
	{
		GameState state = gameStateProvider.getCurrentGameState();
		
		for(Player p : state.getPlayers())
		{
			//TODO call a recursive method that looks for all configurations within the configuration given to it
			//for each configuration do one of two things:
			//	1. if it is a display configuration load all of its resources
			//	2. otherwise call the recursive function on that configuration
			//
			// make sure you keep a record of what configurations have already been loaded
		}
	}

	/**
	 * The main content panel for the main window. It is responsible for drawing
	 * everything in the game.
	 */
	private class GamePanel extends JPanel
	{
		private List<ILayer> strategicView;
		private List<ILayer> tacticalView;

		/**
		 * Measures how much the user has zoomed in, where 100% is fully zoomed
		 * in and 0% is fully zoomed out.
		 */
		private double zoomLevel;

		private Position mousePosition;
		private Position lastClickPosition;
		private Rectangle2D viewport;
		private Dimension2D mapSize;

		private CommandCardPanel commandCardPanel;
		private ExitButtonPanel exitButtonPanel;
		private ResourceDisplayPanel resourceDisplayPanel;
		private NodeStatusPanel nodeStatusPanel;
		private TechPanel techPanel;
		private TechButtonPanel techButtonPanel;
		
		private boolean panLeft;
		private boolean panRight;
		private boolean panUp;
		private boolean panDown;

		private long lastTime;

		/**
		 * Constructs and initializes this GamePanel
		 */
		public GamePanel()
		{
			super(null);

			// starts the user fully zoomed out
			zoomLevel = 1;

			mousePosition = null;
			lastClickPosition = null;
			
			panLeft = false;
			panRight = false;
			panUp = false;
			panDown = false;

			// ignores system generated repaints
			setIgnoreRepaint(true);
			setOpaque(false);

			Animation leftUIPanel = null;
			Animation rightUIPanel = null;
			Animation exitButton = null;
			Animation exitButtonClicked = null;
			try
			{
				leftUIPanel = (Animation)new ObjectInputStream(new FileInputStream(new File("resources/animations/left_ui_panel.cfg"))).readObject();
				rightUIPanel = (Animation)new ObjectInputStream(new FileInputStream(new File("resources/animations/right_ui_panel.cfg"))).readObject();
				exitButton = (Animation)new ObjectInputStream(new FileInputStream(new File("resources/animations/Exit_Button.cfg"))).readObject();
				exitButtonClicked = (Animation)new ObjectInputStream(new FileInputStream(new File("resources/animations/Exit_Button_Clicked.cfg"))).readObject();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}

			gameStateProvider.lockViewableGameState();

			commandCardPanel = new CommandCardPanel(Display.this, playerIndex, gameStateProvider, messageReceiver, rightUIPanel);
			add(commandCardPanel);
			nodeStatusPanel = new NodeStatusPanel(Display.this, gameStateProvider, leftUIPanel);
			add(nodeStatusPanel);
			resourceDisplayPanel = new ResourceDisplayPanel(gameStateProvider, playerIndex);
			add(resourceDisplayPanel);
			exitButtonPanel = new ExitButtonPanel(Display.this, gameStateProvider, exitButton, exitButtonClicked);
			add(exitButtonPanel);
			techPanel = new TechPanel(Display.this, gameStateProvider, playerIndex);
			add(techPanel);
			techButtonPanel = new TechButtonPanel(techPanel, gameStateProvider, exitButton, exitButtonClicked, exitButton, exitButtonClicked);
			add(techButtonPanel);

			GameState state = gameStateProvider.getCurrentGameState();
			Map map = state.getMap();
			int numPlayers = state.getNumPlayers();

			// calculates the visible screen size based off of the zoom level
			Position mapDim = map.getDimensions();
			double mapWidth = mapDim.getX();
			double mapHeight = mapDim.getY();
			mapSize = new Dimension();
			mapSize.setSize(mapWidth, mapHeight);

			Dimension2D visibleSize = new Dimension();
			visibleSize.setSize(zoomLevel * mapSize.getWidth(), zoomLevel * mapSize.getHeight());
			viewport = new Rectangle2D.Double(0, 0, visibleSize.getWidth(), visibleSize.getHeight());

			// add the map image to the MapItemDrawer
			String mapURI = map.getConfig().getImageURI();

			gameStateProvider.unlockViewableGameState();

			strategicView = new ArrayList<ILayer>(2);
			strategicView.add(new GraphLayer(Display.this, playerIndex, numPlayers));

			tacticalView = new ArrayList<ILayer>();
			tacticalView.add(new TerrainLayer(mapURI, Display.this, mapWidth, mapHeight));
			tacticalView.add(new MapItemLayer(MapItemType.BUILDING, Display.this));
			tacticalView.add(new MapItemLayer(MapItemType.UNIT, Display.this));
			tacticalView.add(new MapItemLayer(MapItemType.PROJECTILE, Display.this));
			tacticalView.add(new MapItemLayer(MapItemType.LANEBORDER, Display.this));

			addComponentListener(new ResizeListener());

			// adds the mouse input handler
			InputHandler ih = new InputHandler();
			addMouseWheelListener(ih);
			addMouseMotionListener(ih);
			addMouseListener(ih);
			
			KeyboardHandler keyListener = new KeyboardHandler();
			addKeyListener(keyListener);
		}

		/**
		 * Draws everything to the screen.
		 */
		@Override
		public void paint(Graphics g)
		{
			long curTime = System.currentTimeMillis();
			double fps = 1000.0 / (curTime - lastTime);
			lastTime = curTime;

			double scale = getWidth() / viewport.getWidth();
			updateViewPortPan(fps, scale);

			gameStateProvider.lockViewableGameState();
			GameState gamestate = gameStateProvider.getCurrentGameState();

			detectFlowIndicatorChange(gamestate);

			List<ILayer> currentView = (zoomLevel > ZOOM_THRESHOLD) ? strategicView : tacticalView;

			// fill the background black
			g.setColor(Color.black);
			g.fillRect(0, 0, getWidth(), getHeight());

			// draws layers to scale
			for(int i = 0; i < currentView.size(); i++)
			{
				currentView.get(i).draw(g, gamestate, viewport, scale);
			}

			if(DEBUG_MODE)
			{
				g.setColor(Color.white);
				g.drawString("Logic ups: " + Double.toString(gameStateProvider.getUpdateRate()), 125, 25);
				g.drawString("Display fps: " + Double.toString(fps), 125, 50);
			}

			// draws the panels if they are shown
			updatePanels(g, gamestate, scale);

			// paints other things on top
			super.paint(g);

			// we are done with the gamestate, we should unlock it ASAP
			gameStateProvider.unlockViewableGameState();

			this.repaint();
		}

		/**
		 * Detects if the mouse was clicked on a flow indicator and changes the
		 * flow accordingly.
		 * 
		 * @param state
		 */
		private void detectFlowIndicatorChange(GameState state)
		{
			if(lastClickPosition == null)
				return;

			Lane[] lanes = state.getMap().getLanes();
			if(clicked)
			{
				clicked = false;
				Position clickPos = toScreenCoord(lastClickPosition);
				for(int i = 0; i < lanes.length; ++i)
				{
					BezierCurve curve = lanes[i].getCurve();
					Player p = state.getPlayer(playerIndex);
					Node startNode = p.getStartNode(lanes[i]);
					double flow = p.getFlowDist(lanes[i]);

					Position origin1 = toScreenCoord(curve.getP0());
					Position origin2 = toScreenCoord(curve.getP3());
					Position point1 = toScreenCoord(curve.getP0());
					Position point2 = toScreenCoord(curve.getP3());

					Node[] nodes = lanes[i].getNodes();
					if(startNode == nodes[0])
					{
						Position destination = toScreenCoord(curve.getP1());
						Position scale = destination.subtract(origin1).normalize();
						point1 = origin1.add(scale.scale(flow * 2));
					}
					else if(startNode == nodes[1])
					{
						Position destination = toScreenCoord(curve.getP2());
						Position scale = destination.subtract(origin2).normalize();
						point2 = origin2.add(scale.scale(flow * 2));
					}

					if(point1.subtract(clickPos).length() <= 10)
					{
						adjustingFlowDist = i;
						adjustingFlow1 = true;
						break;
					}
					else if(point2.subtract(clickPos).length() <= 10)
					{
						adjustingFlowDist = i;
						adjustingFlow1 = false;
						break;
					}
				}
			}
			else if(adjustingFlowDist != -1)
			{
				double flow = 0;
				BezierCurve curve = lanes[adjustingFlowDist].getCurve();
				int startNode;
				if(adjustingFlow1)
				{
					Position p0 = toScreenCoord(curve.getP0());
					Position axis = toScreenCoord(curve.getP1()).subtract(p0).normalize();
					Position ray = mousePosition.subtract(p0);

					flow = ray.length() * axis.dot(ray.normalize()) / 2;
					if(flow > 100)
						flow = 100;
					if(flow < 0)
						flow = 0;

					startNode = lanes[adjustingFlowDist].getNodes()[0].getID();
				}
				else
				{
					Position p3 = toScreenCoord(curve.getP3());
					Position axis = toScreenCoord(curve.getP2()).subtract(p3).normalize();
					Position ray = mousePosition.subtract(p3);

					flow = ray.length() * axis.dot(ray.normalize()) / 2;
					if(flow > 100)
						flow = 100;
					if(flow < 0)
						flow = 0;

					startNode = lanes[adjustingFlowDist].getNodes()[1].getID();
				}

				messageReceiver.addMessage(new AdjustFlowDistributionMessage(playerIndex, adjustingFlowDist, flow,
						startNode));
			}
		}

		/**
		 * Determines if the viewport needs to be moved and moves it.
		 * 
		 * @param fps
		 *            The current framerate, used to calculate how far to move
		 *            the viewport.
		 * @param scale
		 *            The current scale factor between the veiwport and the
		 *            screen.
		 */
		private void updateViewPortPan(double fps, double scale)
		{
			if(mousePosition == null)
				return;

			double moveX = 0.0;
			double moveY = 0.0;

			if(mousePosition.getX() < 25 || panLeft)
			{
				moveX = (-1000 / fps) / scale;
			}
			else if(mousePosition.getX() > getWidth() - 25 || panRight)
			{
				moveX = (1000 / fps) / scale;
			}

			if(mousePosition.getY() < 25 || panUp)
			{
				moveY = (-1000 / fps) / scale;
			}
			else if(mousePosition.getY() > getHeight() - 25 || panDown)
			{
				moveY = (1000 / fps) / scale;
			}

			updateViewPort(moveX, moveY, viewport.getWidth(), viewport.getHeight());
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
		 */
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

		/**
		 * Updates the CommandCardPanel and the NodeStatusPanel. Also draws a
		 * box around the selected node or command center if there is one.
		 * 
		 * @param g
		 *            The Graphics object to draw to.
		 * @param gamestate
		 *            The current GameState object.
		 * @param scale
		 *            The current scale factor between the map and screen.
		 */
		private void updatePanels(Graphics g, GameState gamestate, double scale)
		{
			// checks for selected node
			Node node = getSelectedNode(gamestate);
			if(node == null)
			{
				nodeStatusPanel.setVisible(false);
				commandCardPanel.setVisible(false);
			}
			else
			{
				Building cc = node.getCommandCenter();

				nodeStatusPanel.setVisible(true);
				nodeStatusPanel.updateNodeStatus(node, gamestate.getTime() * 1000);

				if(cc == null || (cc.getOwner().getPlayerID() != playerIndex && !DEBUG_MODE))
				{
					commandCardPanel.setVisible(false);
				}
				else
				{
					commandCardPanel.setVisible(true);
					commandCardPanel.updateButtons(gamestate, node);
				}

				int recX;
				int recY;
				int recW;
				int recH;
				if(zoomLevel <= ZOOM_THRESHOLD && cc != null)
				{
					Position p = cc.getPosition();

					recX = (int)(p.getX() - cc.getWidth() / 2);
					recY = (int)(p.getY() - cc.getHeight() / 2);
					recW = (int)(cc.getWidth() * scale);
					recH = (int)(cc.getHeight() * scale);
				}
				else
				{
					Position p = node.getTransformation().getPosition();
					double radius = node.getBoundingCircle().getRadius();

					recX = (int)(p.getX() - radius);
					recY = (int)(p.getY() - radius);
					recW = (int)(2 * radius * scale);
					recH = (int)(2 * radius * scale);
				}

				// draws a rectangle around the command center
				g.setColor(Color.red);
				Position pos = toScreenCoord(new Position(recX, recY));
				g.drawRect((int)pos.getX(), (int)pos.getY(), recW, recH);
			}

			requestFocusInWindow();
		}

		@Override
		public void update(Graphics g)
		{
			paint(g);
		}

		/**
		 * Returns the currently selected command center index for this player.
		 * If no command center is selected, this method returns -1.
		 * 
		 * @param gs
		 *            The current gamestate.
		 * @return The currently selected command center index or -1 if nothing
		 *         is selected.
		 */
		private Node getSelectedNode(GameState gs)
		{
			if(lastClickPosition == null)
				return null;

			Node[] nodes = gs.getMap().getNodes();
			for(Node n : nodes)
			{
//				if(n.getOwner() != null)
//				{
					double radius = n.getBoundingCircle().getRadius();
					Rectangle rect = new Rectangle(n.getTransformation(), 2 * radius, 2 * radius);

					if(rect.positionIsInShape(lastClickPosition))
					{
						return n;
					}
//				}
			}

			// List<CommandCenter> ccs = gs.getCommandCenters();
			// for(int i = 0; i < ccs.size(); i++)
			// {
			// CommandCenter cc = ccs.get(i);
			// if(cc != null)
			// {
			// if(cc.getOwner().getPlayerID() == playerIndex ||
			// OPPONENTS_NODES_SELECTABLE)
			// {
			// Rectangle rect;
			// if(zoomLevel <= ZOOM_THRESHOLD)
			// {
			// rect = new Rectangle(new Transformation(cc.getPosition(), 0),
			// cc.getWidth(), cc.getHeight());
			// }
			// else
			// {
			// Node node = cc.getNode();
			// double radius = node.getBoundingCircle().getRadius();
			// rect = new Rectangle(node.getTransformation(), 2 * radius, 2 *
			// radius);
			// }
			//
			// if(rect.positionIsInShape(lastClickPosition))
			// {
			// return cc.getNode();
			// }
			// }
			// }
			// }

			return null;
		}

		/**
		 * Updates the size and location of the contained panels on a resize
		 * event.
		 * 
		 * @author Titus Klinge
		 * 
		 */
		private class ResizeListener extends ComponentAdapter
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				Dimension2D visibleSize = new Dimension();
				visibleSize.setSize(zoomLevel * mapSize.getWidth(), zoomLevel * mapSize.getHeight());
				double scale = (getHeight() / visibleSize.getHeight()) / (getWidth() / visibleSize.getWidth());
				viewport = new Rectangle2D.Double(0, 0, visibleSize.getWidth(), visibleSize.getHeight() * scale);

				commandCardPanel.updateLocation();
				nodeStatusPanel.updateLocation();
				resourceDisplayPanel.updateLocation();
				exitButtonPanel.updateLocation();
				techPanel.updateLocation();
				techButtonPanel.updateLocation();
			}
		}

		/**
		 * Handles the mouse events for this GamePanel.
		 * 
		 * @author Titus Klinge
		 * @author Ryan Tew
		 * 
		 */
		private class InputHandler extends MouseAdapter
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				Position p = new Position(e.getPoint().getX(), e.getPoint().getY());
				lastClickPosition = toGameCoord(p);
				clicked = true;
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				Position p = new Position(e.getPoint().getX(), e.getPoint().getY());
				lastClickPosition = toGameCoord(p);
				adjustingFlowDist = -1;
			}

			@Override
			public void mouseDragged(MouseEvent e)
			{
				Position p = new Position(e.getPoint().getX(), e.getPoint().getY());
				mousePosition = p;
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

				updateViewPort(viewX, viewY, newW, newH);
				zoomLevel = newZoom;
			}
		}
		
		private class KeyboardHandler extends KeyAdapter
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				int code = e.getKeyCode();
				switch(code)
				{
				case KeyEvent.VK_LEFT:
					panLeft = true;
					break;
				case KeyEvent.VK_RIGHT:
					panRight = true;
					break;
				case KeyEvent.VK_UP:
					panUp = true;
					break;
				case KeyEvent.VK_DOWN:
					panDown = true;
					break;
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				int code = e.getKeyCode();
				switch(code)
				{
				case KeyEvent.VK_LEFT:
					panLeft = false;
					break;
				case KeyEvent.VK_RIGHT:
					panRight = false;
					break;
				case KeyEvent.VK_UP:
					panUp = false;
					break;
				case KeyEvent.VK_DOWN:
					panDown = false;
					break;
				}
			}
		}
	}
}

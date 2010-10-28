package linewars.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import linewars.display.layers.GraphLayer;
import linewars.display.layers.ILayer;
import linewars.display.layers.MapItemLayer;
import linewars.display.layers.MapItemLayer.MapItemType;
import linewars.display.layers.TerrainLayer;
import linewars.display.panels.CommandCardPanel;
import linewars.display.panels.ExitButtonPanel;
import linewars.display.panels.NodeStatusPanel;
import linewars.display.panels.ResourceDisplayPanel;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.GameState;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.CommandCenter;
import linewars.network.MessageReceiver;
import linewars.parser.ConfigFile;
import linewars.parser.Parser;
import linewars.parser.Parser.InvalidConfigFileException;
import linewars.parser.ParserKeys;

/**
 * Encapsulates the display information.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
@SuppressWarnings("serial")
public class Display extends JFrame implements Runnable
{
	/**
	 * The number of milliseconds in-between draw events.
	 */
	private static final int DRAW_DELAY = 100;

	/**
	 * The threshold when zooming out where the view switches from tactical view
	 * to strategic view and vice versa.
	 */
	private static final double ZOOM_THRESHOLD = 0.80;

	private static final double MAX_ZOOM = 0.15;
	private static final double MIN_ZOOM = 1.5;

	private GameStateProvider gameStateProvider;
	private MessageReceiver messageReceiver;
	private GamePanel gamePanel;

	public Display(GameStateProvider provider, MessageReceiver receiver)
	{
		super("Line Wars");
		gameStateProvider = provider;
		gamePanel = new GamePanel();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(gamePanel);
		setSize(new Dimension(800, 600));
		// setUndecorated(true);
	}

	@Override
	public void run()
	{
		// shows the display
		setVisible(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		// spawns the paint driver for the display
		new Timer(DRAW_DELAY, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				gamePanel.repaint();
			}
		}).start();
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

		private Point2D mousePosition;
		private Point2D lastClickPosition;
		private Rectangle2D viewport;
		private Dimension2D mapSize;

		private CommandCardPanel commandCardPanel;
		private ExitButtonPanel exitButtonPanel;
		private ResourceDisplayPanel resourceDisplayPanel;
		private NodeStatusPanel nodeStatusPanel;

		public GamePanel()
		{
			super(null);

			mousePosition = new Point2D.Double();
			lastClickPosition = new Point2D.Double();

			// ignores system generated repaints
			setIgnoreRepaint(true);

			Parser leftUIPanel = null;
			Parser rightUIPanel = null;
			Parser exitButton = null;
			Parser exitButtonClicked = null;
			try
			{
				leftUIPanel = new Parser(new ConfigFile("resources/animations/left_ui_panel.cfg"));
				rightUIPanel = new Parser(new ConfigFile("resources/animations/right_ui_panel.cfg"));
				exitButton = new Parser(new ConfigFile("resources/animations/Exit_Button.cfg"));
				exitButtonClicked = new Parser(new ConfigFile("resources/animations/Exit_Button_Clicked.cfg"));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (InvalidConfigFileException e)
			{
				e.printStackTrace();
			}

			setOpaque(false);

			TerrainLayer terrain = new TerrainLayer();

			strategicView = new ArrayList<ILayer>(2);
			strategicView.add(terrain);
			strategicView.add(new GraphLayer());

			tacticalView = new ArrayList<ILayer>();
			tacticalView.add(terrain);
			tacticalView.add(new MapItemLayer(MapItemType.BUILDING));
			tacticalView.add(new MapItemLayer(MapItemType.UNIT));
			tacticalView.add(new MapItemLayer(MapItemType.PROJECTILE));

			// get the map parser from the gamestate
			gameStateProvider.lockViewableGameState();
			Parser mapParser = gameStateProvider.getCurrentGameState().getMap().getParser();
			gameStateProvider.unlockViewableGameState();

			// add the map image to the MapItemDrawer
			String mapURI = mapParser.getStringValue(ParserKeys.icon);
			int mapWidth = (int)mapParser.getNumericValue(ParserKeys.imageWidth);
			int mapHeight = (int)mapParser.getNumericValue(ParserKeys.imageHeight);
			try
			{
				MapItemDrawer.getInstance().addImage(mapURI, mapWidth, mapHeight);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			// starts the user fully zoomed out
			zoomLevel = 1;
			viewport = null;

			commandCardPanel = new CommandCardPanel(gameStateProvider, rightUIPanel);
			add(commandCardPanel);
			nodeStatusPanel = new NodeStatusPanel(gameStateProvider, leftUIPanel);
			add(nodeStatusPanel);
			resourceDisplayPanel = new ResourceDisplayPanel(gameStateProvider, null);
			add(resourceDisplayPanel);
			exitButtonPanel = new ExitButtonPanel(Display.this, gameStateProvider, exitButton, exitButtonClicked);
			add(exitButtonPanel);

			addComponentListener(new ResizeListener());

			// adds the mouse input handler
			InputHandler ih = new InputHandler();
			addMouseWheelListener(ih);
			addMouseMotionListener(ih);
		}

		/**
		 * Draws everything to the screen.
		 * 
		 * NOTE: We are assuming the game space is identical to the pixel space
		 * when drawing each frame.
		 */
		@Override
		public void paint(Graphics g)
		{
			gameStateProvider.lockViewableGameState();
			GameState gamestate = gameStateProvider.getCurrentGameState();
			// TODO make sure to change this back so it uses strategic view as
			// well!
			List<ILayer> currentView = tacticalView;// (zoomLevel >=
													// ZOOM_THRESHOLD) ?
													// strategicView :
													// tacticalView;

			// calculates the visible screen size based off of the zoom level
			if(viewport == null)
			{
				mapSize = gamestate.getMapSize();
				Dimension2D visibleSize = new Dimension();
				visibleSize.setSize(zoomLevel * mapSize.getWidth(), zoomLevel * mapSize.getHeight());
				viewport = new Rectangle2D.Double(0, 0, visibleSize.getWidth(), visibleSize.getHeight());
			}

			// double buffer implementation
			Image buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics bufferedG = buffer.getGraphics();
			bufferedG.setColor(Color.black);
			bufferedG.fillRect(0, 0, getWidth(), getHeight());

			// draws layers to scale
			double scaleX = getWidth() / viewport.getWidth();
			double scaleY = getHeight() / viewport.getHeight();
			for(int i = 0; i < currentView.size(); i++)
			{
				currentView.get(i).draw(bufferedG, gamestate, viewport, scaleX, scaleY);
			}

			// draws the panels if they are shown
			updatePanels(g, gamestate);

			// we are done with the gamestate, we should unlock it ASAP
			gameStateProvider.unlockViewableGameState();

			// draws the offscreen image to the graphics object
			g.drawImage(buffer, 0, 0, getWidth(), getHeight(), Display.this);

			// paints other things on top
			super.paint(g);
		}

		private void updatePanels(Graphics g, GameState gamestate)
		{
			// checks for selected node
			int nodeIndex = getSelectedNode(gamestate);
			if(nodeIndex == -1)
			{
				nodeStatusPanel.setVisible(false);
				commandCardPanel.setVisible(false);
			}
			else
			{
				CommandCenter node = gamestate.getCommandCenters().get(nodeIndex);

				nodeStatusPanel.setVisible(true);
				// TODO populate status panel

				commandCardPanel.setVisible(true);
				commandCardPanel.updateButtons(node, nodeIndex);

				// draws a rectangle around the command center
				Position p = node.getPosition();
				int recX = (int)((p.getX() - node.getWidth() / 2) * viewport.getWidth() / mapSize.getWidth());
				int recY = (int)((p.getY() - node.getHeight() / 2) * viewport.getHeight() / mapSize.getHeight());
				int recW = (int)(node.getWidth() * viewport.getWidth() / mapSize.getWidth());
				int recH = (int)(node.getHeight() * viewport.getHeight() / mapSize.getHeight());
				g.setColor(Color.red);
				g.drawRect(recX, recY, recW, recH);
			}
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
		private int getSelectedNode(GameState gs)
		{
			double scaleX = viewport.getWidth() / mapSize.getWidth();
			double scaleY = viewport.getHeight() / mapSize.getHeight();
			Point2D point = new Point2D.Double(lastClickPosition.getX() * scaleX, lastClickPosition.getY() * scaleY);

			List<CommandCenter> ccs = gs.getCommandCenters();
			for(int i = 0; i < ccs.size(); i++)
			{
				// TODO remove this comment
				CommandCenter cc = ccs.get(i);
				if(cc != null)
				{
					Position p = cc.getPosition();
					if(point.getX() > p.getX() && point.getY() > p.getY())
					{
						if(point.getX() < p.getX() + ccs.get(i).getWidth()
								&& point.getY() < p.getY() + ccs.get(i).getHeight())
						{
							return i;
						}
					}
				}
			}

			return -1;
		}

		private class ResizeListener extends ComponentAdapter
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				commandCardPanel.updateLocation();
				nodeStatusPanel.updateLocation();
				resourceDisplayPanel.updateLocation();
				exitButtonPanel.updateLocation();
			}
		}

		private class InputHandler extends MouseAdapter
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				lastClickPosition = e.getLocationOnScreen();
			}

			@Override
			public void mouseMoved(MouseEvent e)
			{
				mousePosition = e.getLocationOnScreen();
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
				double xRatio = viewport.getWidth() / getWidth();
				double yRatio = viewport.getHeight() / getHeight();

				// converts the mouse point to the game space
				double mouseX = mousePosition.getX() * xRatio;
				double mouseY = mousePosition.getY() * yRatio;

				// calculates the change in postion of the viewport
				double viewX = mouseX - mouseX * zoomRatio;
				double viewY = mouseY - mouseY * zoomRatio;

				// calculates the new dimension of the viewport
				double newW = viewport.getWidth() * zoomRatio;
				double newH = viewport.getHeight() * zoomRatio;

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
				zoomLevel = newZoom;
			}
		}
	}
}

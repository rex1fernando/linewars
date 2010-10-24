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
import javax.swing.SwingUtilities;
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
import linewars.gamestate.GameState;
import linewars.gamestate.GameStateManager;
import linewars.gamestate.mapItems.CommandCenter;
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
public class Display
{
	/**
	 * The number of milliseconds in-between draw events.
	 */
	private static final int DRAW_DELAY = 100;
	
	/**
	 * The threshold when zooming out where the view switches
	 * from tactical view to strategic view and vice versa.
	 */
	private static final double ZOOM_THRESHOLD = 0.80;
	
	private static final double MAX_ZOOM = 0.15;
	private static final double MIN_ZOOM = 1.5;
	
	/**
	 * The entry point for the program.
	 * 
	 * TODO Add instantiation of other threads and game object
	 *      within this method.
	 * 
	 * @param args Command line arguments are not used currently.
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(buildGUI());
	}

	/**
	 * Constructs a runnable object that creates the swing
	 * components that drive the graphics for the program.
	 * 
	 * @return The runnable object.
	 */
	private static Runnable buildGUI()
	{
		return new Runnable()
		{
			public void run()
			{
				JFrame f = new JFrame("Line Wars");
				GamePanel panel = new GamePanel(f);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setContentPane(panel);
				f.setSize(new Dimension(800, 600));
				//f.setUndecorated(true);
				f.setVisible(true);
				f.setExtendedState(JFrame.MAXIMIZED_BOTH);
			}
		};
	}
	
	/**
	 * The main content panel for the main window.  It is responsible
	 * for drawing everything in the game.
	 */
	@SuppressWarnings("serial")
	private static class GamePanel extends JPanel
	{
		private JFrame parent;
		
		private GameStateManager stateManager;
		
		private List<ILayer> strategicView;
		private List<ILayer> tacticalView;
		
		/**
		 * Measures how much the user has zoomed in, where
		 * 100% is fully zoomed in and 0% is fully zoomed out.
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
		
		public GamePanel(JFrame parent)
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
				leftUIPanel = new Parser(new ConfigFile("resources/display/left_ui_panel.cfg"));
				rightUIPanel = new Parser(new ConfigFile("resources/display/right_ui_panel.cfg"));
				exitButton = new Parser(new ConfigFile("resources/display/Exit_Button.cfg"));
				exitButtonClicked = new Parser(new ConfigFile("resources/display/Exit_Button_Clicked.cfg"));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (InvalidConfigFileException e)
			{
				e.printStackTrace();
			}
			
			MapItemDrawer drawer = MapItemDrawer.getInstance();
			try
			{
				drawer.addImage(leftUIPanel);
				drawer.addImage(rightUIPanel);
				drawer.addImage(exitButton);
				drawer.addImage(exitButtonClicked);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			
			this.parent = parent;
			
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
			
			stateManager = new GameStateManager();
			
			// starts the user fully zoomed out
			zoomLevel = 1;
			viewport = null;
			
			commandCardPanel = new CommandCardPanel(stateManager, new Animation(new String[]{rightUIPanel.getStringValue(ParserKeys.icon)}, new double[]{1}, 0), null, null);
			add(commandCardPanel);
			nodeStatusPanel = new NodeStatusPanel(stateManager, new Animation(new String[]{leftUIPanel.getStringValue(ParserKeys.icon)}, new double[]{1}, 0), null, null);
			add(nodeStatusPanel);
			resourceDisplayPanel = new ResourceDisplayPanel(stateManager, null, null, null, null);
			add(resourceDisplayPanel);
			exitButtonPanel = new ExitButtonPanel(parent, stateManager, new Animation(new String[]{exitButton.getStringValue(ParserKeys.icon)}, new double[]{1}, 0), new Animation(new String[]{exitButtonClicked.getStringValue(ParserKeys.icon)}, new double[]{1}, 0), null);
			add(exitButtonPanel);
			
			addComponentListener(new ResizeListener());
			
			// spawns the paint driver for the display
			new Timer(DRAW_DELAY, new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					repaint();
				}
			}).start();
			
			// adds the mouse input handler
			InputHandler ih = new InputHandler();
			addMouseWheelListener(ih);
			addMouseMotionListener(ih);
		}
		
		/**
		 * Draws everything to the screen.
		 * 
		 * NOTE: We are assuming the game space is identical to the
		 *       pixel space when drawing each frame.
		 */
		@Override
		public void paint(Graphics g)
		{
			// draws the background black
			g.setColor(Color.black);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			GameState gamestate = stateManager.getDisplayGameState();
			List<ILayer> currentView = (zoomLevel >= ZOOM_THRESHOLD) ? strategicView : tacticalView;
			
			// calculates the visible screen size based off of the zoom level
			if (viewport == null)
			{
				mapSize = gamestate.getMapSize();
				Dimension2D visibleSize = new Dimension();
				visibleSize.setSize(zoomLevel * mapSize.getWidth(), zoomLevel * mapSize.getHeight());
				viewport = new Rectangle2D.Double(0, 0, visibleSize.getWidth(), visibleSize.getHeight());
			}
			
			// double buffer implementation
			Image buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics bufferedG = buffer.getGraphics();
			
			double scaleX = getWidth() / viewport.getWidth();
			double scaleY = getHeight() / viewport.getHeight();
			
			// draws layers to scale
			for (int i = 0; i < currentView.size(); i++)
			{
				currentView.get(i).draw(bufferedG, gamestate, viewport, scaleX, scaleY);
			}
			
			// checks for selected node
			CommandCenter node = getSelectedNode(gamestate);
			if (node != null)
			{
				
			}
			
			g.drawImage(buffer, 0, 0, getWidth(), getHeight(), parent);
			
			super.paint(g);
		}
		
		private CommandCenter getSelectedNode(GameState gs)
		{
			for (CommandCenter cc : gs.getCommandCenters())
			{
				
			}
			
			return null;
		}
		
		@Override
		public void update(Graphics g)
		{
			paint(g);
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
				if (newZoom < MAX_ZOOM) newZoom = MAX_ZOOM;
				if (newZoom > MIN_ZOOM) newZoom = MIN_ZOOM;
				
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
				if (newX < 0) newX = 0;
				if (newX > mapSize.getWidth() - newW) newX = mapSize.getWidth() - newW;
				if (newW > mapSize.getWidth()) newX = (mapSize.getWidth() - newW) / 2;
				
				// calculates the new y for the viewport
				double newY = viewport.getY() + viewY;
				if (newY < 0) newY = 0;
				if (newY > mapSize.getHeight() - newH) newY = mapSize.getHeight() - newH;
				if (newH > mapSize.getHeight()) newY = (mapSize.getHeight() - newH) / 2;
				
				viewport.setRect(newX, newY, newW, newH);
				zoomLevel = newZoom;
			}
		}
	}
}

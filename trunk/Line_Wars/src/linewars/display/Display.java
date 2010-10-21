package linewars.display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import linewars.display.layers.GraphLayer;
import linewars.display.layers.ILayer;
import linewars.display.layers.MapItemLayer;
import linewars.display.layers.MapItemLayer.MapItemType;
import linewars.display.panels.CommandCardPanel;
import linewars.display.panels.ExitButtonPanel;
import linewars.display.panels.NodeStatusPanel;
import linewars.display.panels.ResourceDisplayPanel;
import linewars.gamestate.GameState;
import linewars.gamestate.GameStateManager;

/**
 * Encapsulates the display information.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
public class Display
{
	/**
	 * The threshold when zooming out where the view switches
	 * from tactical view to strategic view and vice versa.
	 */
	private static final double ZOOM_THRESHOLD = 0.80;
	
	private static final double MAX_ZOOM = 0.01;
	private static final double MIN_ZOOM = 1.0;
	
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
		
		/**
		 * The (x,y) location of the upper left corner of the visible
		 * screen.  This changes as the user pans around the game map.
		 */
		private Point2D screenPosition;
		
		private CommandCardPanel commandCardPanel;
		private ExitButtonPanel exitButtonPanel;
		private ResourceDisplayPanel resourceDisplayPanel;
		private NodeStatusPanel nodeStatusPanel;
		
		public GamePanel(JFrame parent)
		{
			String leftpane = File.separator + "resources" + File.separator + "display" + File.separator + "left_ui_panel.png";
			String rightpane = File.separator + "resources" + File.separator + "display" + File.separator + "right_ui_panel.png";
			
			try
			{
				MapItemDrawer.getInstance().addImage(leftpane);
				MapItemDrawer.getInstance().addImage(rightpane);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			
			
			this.parent = parent;
			
			setOpaque(false);
			
			strategicView = new ArrayList<ILayer>(2);
			strategicView.add(new GraphLayer());
			
			tacticalView = new ArrayList<ILayer>();
			tacticalView.add(new MapItemLayer(MapItemType.BUILDING));
			tacticalView.add(new MapItemLayer(MapItemType.UNIT));
			tacticalView.add(new MapItemLayer(MapItemType.PROJECTILE));
			
			stateManager = new GameStateManager();
			
			// starts the user fully zoomed out
			zoomLevel = 1;
			screenPosition = new Point2D.Double(0,0);
			
			commandCardPanel = new CommandCardPanel(stateManager, new Animation(new String[]{rightpane}, new double[]{1}, 0), null, null);
			add(commandCardPanel);
			nodeStatusPanel = new NodeStatusPanel(stateManager, new Animation(new String[]{leftpane}, new double[]{1}, 0), null, null);
			add(nodeStatusPanel);
//			resourceDisplayPanel = new ResourceDisplayPanel(stateManager, null, null, null);
//			add(resourceDisplayPanel);
//			exitButtonPanel = new ExitButtonPanel(parent, stateManager, null, null, null);
//			add(exitButtonPanel);
			
			addComponentListener(new ResizeListener());
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
			GameState gamestate = stateManager.getDisplayGameState();
			List<ILayer> currentView = (zoomLevel >= ZOOM_THRESHOLD) ? strategicView : tacticalView;
			
			// calculates the visible screen size based off of the zoom level
			Dimension2D mapSize = gamestate.getMapSize();
			Dimension2D visibleSize = new Dimension();
			visibleSize.setSize(zoomLevel * mapSize.getWidth(), zoomLevel * mapSize.getHeight());
			Rectangle2D visibleScreen = new Rectangle2D.Double(screenPosition.getX(), screenPosition.getY(), visibleSize.getWidth(), visibleSize.getHeight());
			
			
			// double buffer implementation
			Image buffer = new BufferedImage((int) visibleScreen.getWidth(), (int) visibleScreen.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics bufferedG = buffer.getGraphics();
			
			for (int i = 0; i < currentView.size(); i++)
			{
				currentView.get(i).draw(bufferedG, gamestate, visibleScreen);
			}
			
			g.drawImage(buffer, 0, 0, getWidth(), getHeight(), parent);
			
			super.paint(g);
		}
		
		private class ResizeListener extends ComponentAdapter
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				commandCardPanel.updateLocation();
				nodeStatusPanel.updateLocation();
//				resourceDisplayPanel.updateLocation();
//				exitButtonPanel.updateLocation();
			}
		}
	}
}

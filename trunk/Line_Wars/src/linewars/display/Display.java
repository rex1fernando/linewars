package linewars.display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import linewars.display.layers.ILayer;
import linewars.gamestate.GameState;
import linewars.gamestate.GameStateManager;

/**
 * TODO add class javadoc
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
	private static final double ZOOM_THRESHOLD = 20;
	
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
				// f.setUndecorated(true);
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
		private Point screenPosition;
		
		
		public GamePanel(JFrame parent)
		{
			this.parent = parent;
			
			strategicView = new ArrayList<ILayer>(2);
			// TODO add terrain and graph layers to list
			
			tacticalView = new ArrayList<ILayer>();
			// TODO add tactical layers to list (terrain layer and units, building, etc.)
			
			stateManager = new GameStateManager();
			
			// starts the user fully zoomed out
			zoomLevel = 0;
			screenPosition = new Point(0,0);
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
			List<ILayer> currentView = (zoomLevel <= ZOOM_THRESHOLD) ? strategicView : tacticalView;
			
			// calculates the visible screen size based off of the zoom level
			double scale = zoomLevel / 100;
			Dimension mapSize = gamestate.getMapSize();
			Dimension visibleSize = new Dimension((int) (scale * mapSize.width), (int) (scale * mapSize.height));
			Rectangle visibleScreen = new Rectangle(screenPosition, visibleSize);
			
			// double buffer implementation
			Image buffer = new BufferedImage(visibleScreen.width, visibleScreen.height, BufferedImage.TYPE_INT_ARGB);
			Graphics bufferedG = buffer.getGraphics();
			
			for (int i = 0; i < currentView.size(); i++)
			{
				currentView.get(i).draw(bufferedG, gamestate, visibleScreen);
			}
			
			g.drawImage(buffer, 0, 0, getWidth(), getHeight(), parent);
		}
	}
}

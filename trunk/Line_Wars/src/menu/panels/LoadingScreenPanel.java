package menu.panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import menu.ContentProvider;
import menu.ContentProvider.MenuImage;
import menu.GameInitializer;
import menu.WindowManager;
import menu.components.CustomProgressBar;

public class LoadingScreenPanel extends JPanel
{
	private static final double IMAGE_SCALE = 0.2;
	private static final long PERIOD = 10;
	private static final long REVOLUTION_TIME = 2000;
	private static final double VERTICAL_POSITION = 0.4;
	
	private WindowManager wm;
	private Timer timer;
	
	private long creationTime;
	
	private CustomProgressBar progressBar;
	
	public LoadingScreenPanel(final WindowManager wm)
	{
		this.wm = wm;
		progressBar = new CustomProgressBar();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(screenSize);
		setMaximumSize(screenSize);
		setMinimumSize(screenSize);
		
		double screenRatio = 0.6;
		
		add(Box.createRigidArea(new Dimension(0, (int) (screenRatio * screenSize.height))));
		add(progressBar);
	}
	
	public void start(GameInitializer gameInit)
	{
		gameInit.setProgressBar(progressBar);
		
		// initialize spinner
		creationTime = System.currentTimeMillis();
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			public void run() {
				wm.repaint();
			}
		}, 0, PERIOD);
		
		gameInit.execute();
	}
	
	public void stop()
	{
		timer.cancel();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		// get spinner
		Image spinner = ContentProvider.getImageResource(MenuImage.loading_spinner);
		
		// get rotation
		long currentTime = System.currentTimeMillis();
		double theta = ((currentTime - creationTime) % REVOLUTION_TIME) / (REVOLUTION_TIME * 1.0) * 2 * Math.PI;
		
		// get spinner width, height, x, and y
		int imageWidth = (int) (spinner.getWidth(null) * IMAGE_SCALE);
		int imageHeight = (int) (spinner.getHeight(null) * IMAGE_SCALE);
		int x = (getWidth() - imageWidth) / 2;
		int y = (int) (getHeight() * VERTICAL_POSITION) / 2;
		
		// get graphics
		Graphics2D g2 = (Graphics2D) g;
		int px = (x + imageWidth / 2);
		int py = (y + imageHeight / 2);
		g2.rotate(theta, px, py);
		
		// draw spinner
		g2.drawImage(spinner, x, y, imageWidth, imageHeight, null);
		
		g2.rotate(-theta, px, py);
	}
}

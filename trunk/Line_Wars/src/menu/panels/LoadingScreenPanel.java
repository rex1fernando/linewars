package menu.panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import menu.ContentProvider;
import menu.ContentProvider.MenuImage;
import menu.WindowManager;

public class LoadingScreenPanel extends JPanel
{
	private static final double IMAGE_SCALE = 0.1;
	private static final long PERIOD = 10;
	private static final long REVOLUTION_TIME = 2000;
	
	private WindowManager wm;
	
	private long creationTime;
	
	public LoadingScreenPanel(final WindowManager wm)
	{
		setPreferredSize(new Dimension(1024, 640));
		setMaximumSize(new Dimension(1024, 640));
		setMinimumSize(new Dimension(1024, 640));
		this.wm = wm;
		creationTime = System.currentTimeMillis();
		
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			public void run() {
				wm.repaint();
			}
		}, 0, PERIOD);
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
		int y = (getHeight() - imageHeight) / 2;
		
		// get graphics
		Graphics2D g2 = (Graphics2D) g;
		g2.rotate(theta, (x + imageWidth / 2), (y + imageHeight / 2));
		
		// draw spinner
		g2.drawImage(spinner, x, y, imageWidth, imageHeight, null);
	}
}

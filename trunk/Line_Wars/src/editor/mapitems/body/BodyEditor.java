package editor.mapitems.body;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import linewars.display.DisplayConfiguration;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItemState;
import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.GenericSelector;
import editor.GenericSelector.GenericListCallback;

public class BodyEditor extends JPanel implements ConfigurationEditor {
	
	public interface DisplayConfigurationCallback {
		public DisplayConfiguration getDisplayConfiguration();
	}
	
	//variables for drawing the image
	private Canvas canvas;
	private BufferStrategy strategy;
	
	private Object imageLock = new Object();
	private Image[] images;
	private long[] imagetimes;
	
	private boolean running;
	private Thread animationThread;
	
	private BigFrameworkGuy bfg;
	private DisplayConfigurationCallback dcc;
	private String imagePath;
	
	private GenericSelector<MapItemState> animationState;
	private JTextField scalingFactor; //TODO
	
	public BodyEditor(BigFrameworkGuy bfg, DisplayConfigurationCallback dcc, String imagePath)
	{
		this.bfg = bfg;
		this.dcc = dcc;
		this.imagePath = imagePath;
		
		//set up the canvas
		canvas = new Canvas();
		canvas.setSize(800, 600);
		canvas.addMouseListener(new MouseEventListener());
		
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);
		
		animationState = new GenericSelector<MapItemState>("Animation to show", new GenericListCallback<MapItemState>() {
			public List<MapItemState> getSelectionList()
			{
				return BodyEditor.this.dcc.getDisplayConfiguration().getDefinedStates();
			}
		});
		
		//TODO add more stuff to be constructed
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if(visible)
			startDisplayAnimation();
		else
			stopDisplayAnimation();
	}
	
	public void startDisplayAnimation()
	{
		running = true;
		animationThread = new Thread(new Runnable() {
			@Override
			public void run() {
				canvas.setIgnoreRepaint(true);
				canvas.requestFocus();
				canvas.setFocusTraversalKeysEnabled(false);
				canvas.createBufferStrategy(3);
				strategy = canvas.getBufferStrategy();
				
				//load the images
				loadImages(animationState.getSelectedObject());
				
				long lastTime = System.currentTimeMillis();
				int currentImage = 0;
				while(running)
				{
					//get graphics object to draw to
					Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
					g.setColor(Color.black);
					g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
					
					synchronized(imageLock)
					{
						//this keeps any changes to the images being display from causing out of bounds errors
						currentImage = currentImage%images.length;
						
						//calculate the width and height scaling and pick the smallest
						double widthScale = (double)canvas.getWidth()/images[currentImage].getWidth(null);
						double heightScale = (double)canvas.getHeight()/images[currentImage].getHeight(null);
						double scale = Math.min(widthScale, heightScale);
						
						Position imageDim = new Position(images[currentImage].getWidth(null)*scale, images[currentImage].getHeight(null)*scale);
						Position canvasCenter = new Position(canvas.getWidth()/2, canvas.getHeight()/2);
						Position destUpperLeft = canvasCenter.subtract(imageDim.scale(0.5));
						Position destLowerRight = canvasCenter.add(imageDim.scale(0.5));
						g.drawImage(images[currentImage],
								(int)destUpperLeft.getX(), (int)destUpperLeft.getY(), 
								(int)destLowerRight.getX(), (int)destLowerRight.getY(), 
								0, 0, 
								images[currentImage].getWidth(null), images[currentImage].getHeight(null), 
								null);
						
						//check to see if we need to go to the next frame
						if(System.currentTimeMillis() - lastTime > imagetimes[currentImage])
						{
							currentImage = (currentImage + 1)%images.length;
							lastTime = System.currentTimeMillis();
						}
					}
					
					//TODO update the shapes being drawn
					
					//flip the buffers
					g.dispose();
					strategy.show();
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}
				}
				
			}
		});
		animationThread.start();
	}
	
	public void stopDisplayAnimation()
	{
		running = false;
		try {
			animationThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void loadImages(MapItemState mis)
	{
		//TODO, don't forget to lock the images while loading
	}

	@Override
	public void setData(Configuration cd) {
		// TODO Auto-generated method stub

	}

	@Override
	public Configuration instantiateNewConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class MouseEventListener implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		BodyEditor be = new BodyEditor(null);
		frame.setContentPane(be);	
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
		be.setVisible(true);
		be.setVisible(false);
		be.setVisible(true);
		be.setVisible(false);
	}

}

package editor.mapitems.body;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;

import linewars.display.Animation;
import linewars.display.DisplayConfiguration;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItemState;
import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.GenericSelector;
import editor.GenericSelector.GenericListCallback;
import editor.GenericSelector.SelectionChangeListener;

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
	private JTextField scalingFactor;
	
	private JTree containerTree;
	private BodyEditorNode root;
	private boolean mouseState;
	
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
		animationState.setSelectedObject(MapItemState.Idle);
		animationState.addSelectionChangeListener(new SelectionChangeListener<MapItemState>() {
			@Override
			public void selectionChanged(MapItemState newSelection) {
				loadImages(animationState.getSelectedObject());
			}
		});
		
		scalingFactor = new JTextField(20);
		JPanel scalePanel = new JPanel();
		scalePanel.setLayout(new BoxLayout(scalePanel, BoxLayout.Y_AXIS));
		scalePanel.add(new JLabel("How wide in game units should this image be?"));
		scalePanel.add(scalingFactor);
		
		JPanel southPanel = new JPanel();
		southPanel.add(scalePanel);
		southPanel.add(animationState);
		
		this.add(southPanel, BorderLayout.SOUTH);
		
		root = new BodyEditorNode("Root");
//		root.add(new BodyEditorNode("child1", new Circle()));
		root.add(new BodyEditorNode("child1", new Rectangle()));
		containerTree = new JTree(root);
		containerTree.setPreferredSize(new Dimension(150, 600));
		
		this.add(containerTree, BorderLayout.WEST);
		
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
					
					drawShapes(g, root);
					
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
	
	private void drawShapes(Graphics2D g, BodyEditorNode ben)
	{
		if(ben.getShape() != null)
		{
			Position canvasCenter = new Position(canvas.getWidth(), canvas.getHeight()).scale(0.5);
			Point mousePos = canvas.getMousePosition();
			if(mousePos == null)
				mousePos = new Point(0, 0);
			ben.getShape().drawActive(g, canvasCenter, mousePos, mouseState);
		}
		else
		{
			for(int i = 0; i < ben.getChildCount(); i++)
				drawShapes(g, (BodyEditorNode)ben.getChildAt(i));
		}
	}
	
	private void loadImages(MapItemState mis)
	{
		Animation a = dcc.getDisplayConfiguration().getAnimation(mis);
		synchronized(imageLock)
		{
			Image[] oldImages = images;
			long[] oldImagetimes = imagetimes;
			images = new Image[a.getNumImages()];
			imagetimes = new long[a.getNumImages()];
			for(int i = 0; i < a.getNumImages(); i++)
			{
				try {
					images[i] = ImageIO.read(new File(new File(imagePath), a.getImage(i)));
				} catch (IOException e) {
					e.printStackTrace();
					images = oldImages;
					imagetimes = oldImagetimes;
					return;
				}
				imagetimes[i] = (long) a.getImageTime(i);
			}
		}
		
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
			if(arg0.getButton() == MouseEvent.BUTTON1)
				mouseState = true;
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			if(arg0.getButton() == MouseEvent.BUTTON1)
				mouseState = false;
		}
		
	}
	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		
		Animation a1 = new Animation();
		a1.addFrame("Explosion 1.png", 500);
		a1.addFrame("Explosion 2.png", 500);
		
		Animation a2 = new Animation();
		a2.addFrame("Explosion 7.png", 500);
		a2.addFrame("Explosion 8.png", 500);
		
		final DisplayConfiguration dc = new DisplayConfiguration();
		dc.setAnimation(MapItemState.Idle, a1);
		dc.setAnimation(MapItemState.Active, a2);
		
		BodyEditor be = new BodyEditor(null, new DisplayConfigurationCallback() {
			@Override
			public DisplayConfiguration getDisplayConfiguration() {
				return dc;
			}
		}, "resources/animations");
		frame.setContentPane(be);	
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
		be.setVisible(true);
	}

}

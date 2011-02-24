package editor.mapitems.body;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import linewars.display.Animation;
import linewars.display.DisplayConfiguration;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemAggregateDefinition;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.shapes.CircleConfiguration;
import linewars.gamestate.shapes.RectangleConfiguration;
import linewars.gamestate.shapes.ShapeAggregateConfiguration;
import linewars.gamestate.shapes.ShapeConfiguration;
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
	
	public enum Inputs { shift, alt, ctrl, leftMouse }
	
	//variables for drawing the image
	private Canvas canvas;
	private BufferStrategy strategy;
	
	private Object imageLock = new Object();
	private Image[] images;
	private long[] imagetimes;
	
	private boolean isAggregate = false;
	
	private boolean running;
	private Thread animationThread;
	
	private BigFrameworkGuy bfg;
	private DisplayConfigurationCallback dcc;
	private String imagePath;
	
	private GenericSelector<MapItemState> animationState;
	private JTextField scalingFactor;
	
	private JTree containerTree;
	private BodyEditorNode root;
	private BodyEditorNode selectedNode = null;
	private JPopupMenu treePopupMenu;
	
	private List<Inputs> currentInputs = Collections.synchronizedList(new ArrayList<Inputs>());
	
	public BodyEditor(BigFrameworkGuy bfg, DisplayConfigurationCallback dcc, String imagePath)
	{
		this.bfg = bfg;
		this.dcc = dcc;
		this.imagePath = imagePath;
		
		//set up the canvas
		canvas = new Canvas();
		canvas.setSize(800, 600);
		canvas.addMouseListener(new MouseEventListener());
		canvas.addKeyListener(new KeyEventListener());
		
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
		containerTree = new JTree(root);
		containerTree.setPreferredSize(new Dimension(150, 600));
		containerTree.addTreeSelectionListener(new TreeEventListener());
		containerTree.addMouseListener(new MouseListener() {	
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3 && selectedNode != null)
					treePopupMenu.show(containerTree, e.getX(), e.getY());
			}
		});
		treePopupMenu = constructPopupMenu();
		
		JScrollPane scroller = new JScrollPane(containerTree);
		
		this.add(scroller, BorderLayout.WEST);
		
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
					
					synchronized (currentInputs)
					{
						drawShapes(g, root);
						Position canvasCenter = new Position(canvas.getWidth(), canvas.getHeight()).scale(0.5);
						Point mousePos = canvas.getMousePosition();
						if(mousePos == null)
							mousePos = new Point(0, 0);
						if(selectedNode != null && selectedNode.getShape() != null)
							selectedNode.getShape().drawActive(g, canvasCenter, mousePos, currentInputs);
					}
					
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
			if(ben != selectedNode)
				ben.getShape().drawInactive(g, canvasCenter);
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
	
	private JPopupMenu constructPopupMenu()
	{
		JMenuItem remove = new JMenuItem("Remove");
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectedNode != root)
				{
					selectedNode.removeFromParent();
					containerTree.validate();
					containerTree.updateUI();
					selectedNode = null;
				}
				else
					JOptionPane.showMessageDialog(BodyEditor.this,
						    "Cannot remove the root.",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
			}
		});
		
		JMenuItem add = new JMenuItem("Add Child");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectedNode == null)
					return;
				if(isAggregate)
				{
					//TODO
				}
				else
				{
					if(selectedNode == root)
					{
						Object[] options = { "Cirlce", "Rectangle", "Cancel" };
						int n = JOptionPane.showOptionDialog(BodyEditor.this,
								"Which shape would you like to add?",
								"Add Shape", JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[2]);
						BodyEditorNode ben;
						if(n == 0)
							ben = new BodyEditorNode(getNodeName("Circle"), new CircleDisplay());
						else if(n == 1)
							ben = new BodyEditorNode(getNodeName("Rectangle"), new RectangleDisplay());
						else
							return;
						root.add(ben);
						containerTree.validate();
						containerTree.updateUI();
					}
					else
						JOptionPane.showMessageDialog(BodyEditor.this,
							    "Cannot add children to anything but the root.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JMenuItem rename = new JMenuItem("Rename");
		rename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectedNode == null)
					return;
				String s = (String) JOptionPane.showInputDialog(
						BodyEditor.this, "Please enter a new name", "Rename",
						JOptionPane.PLAIN_MESSAGE, null, null,
						selectedNode.getUserObject());
				if(isNameUnique(s, root))
				{
					selectedNode.setUserObject(s);
					containerTree.validate();
					containerTree.updateUI();
				}
				else
					JOptionPane.showMessageDialog(BodyEditor.this,
						    "Cannot rename: name already in use.",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
			}
		});
		
		JPopupMenu ret = new JPopupMenu();
		ret.add(remove);
		ret.add(add);
		ret.add(rename);
		return ret;
	}
	
	private String getNodeName(String baseName)
	{
		String ret = baseName;		
		for(int i = 0; !isNameUnique(ret, root); i++)
			ret = baseName + i;
		return ret;
	}
	
	private boolean isNameUnique(String name, BodyEditorNode root)
	{
		if(name.equals(root.getUserObject()))
			return false;
		for(int i = 0; i < root.getChildCount(); i++)
			if(!isNameUnique(name, (BodyEditorNode) root.getChildAt(i)))
				return false;
		
		return true;
	}

	@Override
	public void setData(Configuration cd) {
		if(cd instanceof MapItemAggregateDefinition<?>)
		{
			isAggregate = true;
			//TODO
		}
		else
		{
			isAggregate = false;
			ShapeConfiguration sc = ((MapItemDefinition<? extends MapItem>) cd).getBodyConfig();
			if(sc instanceof CircleConfiguration)
				root.add(new BodyEditorNode("Circle", new CircleDisplay((CircleConfiguration) sc)));
			else if(sc instanceof RectangleConfiguration)
				root.add(new BodyEditorNode("Rectangle", new RectangleDisplay((RectangleConfiguration) sc)));
			else if(sc instanceof ShapeAggregateConfiguration)
			{
				
			}
			//TODO
		}
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
			if(arg0.getButton() == MouseEvent.BUTTON1 && !currentInputs.contains(Inputs.leftMouse))
				currentInputs.add(Inputs.leftMouse);
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			if(arg0.getButton() == MouseEvent.BUTTON1 && currentInputs.contains(Inputs.leftMouse))
				currentInputs.remove(Inputs.leftMouse);
		}
		
	}
	
	private class KeyEventListener implements KeyListener
	{

		@Override
		public void keyPressed(KeyEvent ke) {
			if(ke.getKeyCode() == KeyEvent.VK_SHIFT && !currentInputs.contains(Inputs.shift))
				currentInputs.add(Inputs.shift);
			if(ke.getKeyCode() == KeyEvent.VK_ALT && !currentInputs.contains(Inputs.alt))
				currentInputs.add(Inputs.alt);
			if(ke.getKeyCode() == KeyEvent.VK_CONTROL && !currentInputs.contains(Inputs.ctrl))
				currentInputs.add(Inputs.ctrl);
		}

		@Override
		public void keyReleased(KeyEvent ke) {
			if(ke.getKeyCode() == KeyEvent.VK_SHIFT && currentInputs.contains(Inputs.shift))
				currentInputs.remove(Inputs.shift);
			if(ke.getKeyCode() == KeyEvent.VK_ALT && currentInputs.contains(Inputs.alt))
				currentInputs.remove(Inputs.alt);
			if(ke.getKeyCode() == KeyEvent.VK_CONTROL && currentInputs.contains(Inputs.ctrl))
				currentInputs.remove(Inputs.ctrl);
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class TreeEventListener implements TreeSelectionListener
	{

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			selectedNode = (BodyEditorNode) e.getNewLeadSelectionPath().getLastPathComponent();
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

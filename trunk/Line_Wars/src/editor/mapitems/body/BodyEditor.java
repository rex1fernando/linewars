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
import java.util.Scanner;

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
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemAggregateDefinition;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.PartAggregateDefinition;
import linewars.gamestate.mapItems.PartDefinition;
import linewars.gamestate.shapes.CircleConfiguration;
import linewars.gamestate.shapes.RectangleConfiguration;
import linewars.gamestate.shapes.ShapeAggregateConfiguration;
import linewars.gamestate.shapes.ShapeConfiguration;
import configuration.Configuration;
import configuration.Property;
import configuration.Usage;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.GenericSelector;
import editor.GenericSelector.GenericListCallback;
import editor.GenericSelector.SelectionChangeListener;

//Left TODO
//-allow enabling/disabling
//-show animations for each sub piece
//-integrate into map item commonalities editor
//--make sure setData gets called whenever instantiate is

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
	
	private boolean isAggregate = true;
	
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
	//TODO add a hashmap for enabled/disabled
	
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
		
		root = new BodyEditorNode("Root", (ShapeDisplay)null);
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
						Position canvasCenter = new Position(canvas.getWidth(), canvas.getHeight()).scale(0.5);
						Point mousePosition = canvas.getMousePosition();
						if(mousePosition == null)
							mousePosition = new Point(0, 0);
						Position mousePos = new Position(mousePosition.x, mousePosition.y);
						root.drawShape(g, canvasCenter, mousePos, currentInputs);
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
					if(selectedNode.getMapItemDefinition() == null) //this node is an aggregate
					{
						Object[] options = { "Aggregate", "Part/Turret", "Cancel" };
						int n = JOptionPane.showOptionDialog(BodyEditor.this,
								"What would you like to add?",
								"Add Node", JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[2]);
						if(n == 0)
							selectedNode.add(new BodyEditorNode(getNodeName("Part Aggregate"), BodyEditorNode.DEFAULT_TRANS));
						else if(n == 1)
						{
							//TODO show part/turret selection box
							selectedNode.add(new BodyEditorNode(
									getNodeName((String) testMapItem
											.getPropertyForName("bfgName")
											.getValue()), testMapItem, BodyEditorNode.DEFAULT_TRANS,
									scalingFactor, canvas));
						}
						containerTree.validate();
						containerTree.updateUI();
					}
					else
						JOptionPane.showMessageDialog(BodyEditor.this,
							    "Cannot add children to this node.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
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
	
	private void decomposeMapItemAggregate(BodyEditorNode parent, MapItemAggregateDefinition<?> miad)
	{
		List<MapItemDefinition<?>> defs = miad.getAllContainedItems();
		List<Transformation> trans = miad.getAllRelativeTransformations();
		List<String> names = miad.getAllNames();
		
		for(int i = 0; i < defs.size(); i++)
		{
			BodyEditorNode ben;
			if(defs.get(i) instanceof MapItemAggregateDefinition<?>)
			{
				ben = new BodyEditorNode(names.get(i), trans.get(i));
				decomposeMapItemAggregate(ben, (MapItemAggregateDefinition<?>) defs.get(i));
			}
			else
				ben = new BodyEditorNode(names.get(i), defs.get(i), trans.get(i), scalingFactor, canvas);
			parent.add(ben);
		}
	}

	@Override
	public void setData(Configuration cd) {
		root.removeAllChildren();
		selectedNode = null;
		if(cd instanceof MapItemAggregateDefinition<?>)
		{
			isAggregate = true;
			decomposeMapItemAggregate(root, (MapItemAggregateDefinition<?>) cd);
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
				ShapeAggregateConfiguration sac = (ShapeAggregateConfiguration) sc;
				for(String name : sac.getDefinedShapeNames())
				{
					sc = sac.getShapeConfigurationForName(name);
					if(sc instanceof CircleConfiguration)
						root.add(new BodyEditorNode("Circle", new CircleDisplay((CircleConfiguration) sc)));
					else if(sc instanceof RectangleConfiguration)
						root.add(new BodyEditorNode("Rectangle", new RectangleDisplay((RectangleConfiguration) sc)));
					else
						throw new IllegalStateException("Shape aggregates should never contain other shape aggregates");
				}
			}
		}
		containerTree.validate();
		containerTree.updateUI();
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		root.removeAllChildren();
		selectedNode = null;
		return null;
	}
	
	private void fillAggregates(BodyEditorNode parent, MapItemAggregateDefinition<?> tofill)
	{
		List<MapItemDefinition<?>> mids = new ArrayList<MapItemDefinition<?>>();
		List<Transformation> relativeTrans = new ArrayList<Transformation>();
		List<String> names = new ArrayList<String>();
		List<Boolean> enabledFlags = new ArrayList<Boolean>();
		for(int i = 0; i < parent.getChildCount(); i++)
		{
			if(((BodyEditorNode)parent.getChildAt(i)).getMapItemDefinition() == null)
			{
				PartAggregateDefinition pad = new PartAggregateDefinition();
				fillAggregates((BodyEditorNode) parent.getChildAt(i), pad);
				mids.add(pad);
			}
			else
				mids.add(((BodyEditorNode)parent.getChildAt(i)).getMapItemDefinition());
			relativeTrans.add(((BodyEditorNode)parent.getChildAt(i)).getShape().getTransformation());
			names.add((String) ((BodyEditorNode)parent.getChildAt(i)).getUserObject());
			enabledFlags.add(true);
		}
		tofill.setFullContainedList(mids, relativeTrans, names, enabledFlags);
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		if(isAggregate)
		{
			fillAggregates(root, (MapItemAggregateDefinition<?>) toSet);
		}
		else
		{
			MapItemDefinition<?> mid = (MapItemDefinition<?>) toSet;
			ShapeConfiguration sc = null;
			double scalingFactor = 1;
			Scanner s = new Scanner(this.scalingFactor.getText());
			if(s.hasNextDouble())
				scalingFactor = s.nextDouble()/canvas.getWidth();
			if(root.getChildCount() == 0)
				mid.setBody(null);
			else if(root.getChildCount() == 1)
				mid.setBody(((BodyEditorNode) root.getChildAt(0)).getShape().generateConfiguration(scalingFactor));
			else
			{
				ShapeAggregateConfiguration sac = new ShapeAggregateConfiguration();
				for(int i = 0; i < root.getChildCount(); i++)
					sac.setShapeConfigurationForName(
							(String) ((BodyEditorNode) root.getChildAt(i))
									.getUserObject(), ((BodyEditorNode) root
									.getChildAt(i)).getShape()
									.generateConfiguration(scalingFactor), true);
				mid.setBody(sac);
			}
		}
		return null;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.building);
		ret.add(ConfigType.gate);
		ret.add(ConfigType.part);
		ret.add(ConfigType.projectile);
		ret.add(ConfigType.turret);
		ret.add(ConfigType.unit);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}
	
	private class MouseEventListener implements MouseListener
	{
		@Override
		public void mouseClicked(MouseEvent arg0) {}
		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
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
		public void keyTyped(KeyEvent arg0) {}
		
	}
	
	private class TreeEventListener implements TreeSelectionListener
	{

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if(selectedNode != null)
				selectedNode.setActive(false);
			selectedNode = (BodyEditorNode) e.getNewLeadSelectionPath().getLastPathComponent();
			if(selectedNode != null)
				selectedNode.setActive(true);
		}
		
	}
	
	private static PartDefinition testMapItem;
	
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
		
		testMapItem = new PartDefinition();
		testMapItem.setPropertyForName("bfgName", new Property(Usage.STRING, "TEST"));
		ShapeConfiguration sc = new RectangleConfiguration(100, 50, new Transformation(new Position(0, 0), 0)); 
		testMapItem.setBody(sc);
		if(sc != testMapItem.getBodyConfig())
			return;
		
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

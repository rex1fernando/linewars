package editor.mapitems.body;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.gamestate.shapes.configurations.CircleConfiguration;
import linewars.gamestate.shapes.configurations.RectangleConfiguration;
import linewars.gamestate.shapes.configurations.ShapeAggregateConfiguration;
import linewars.gamestate.shapes.configurations.ShapeConfiguration;
import configuration.Configuration;
import configuration.Property;
import configuration.Usage;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.GenericSelector;
import editor.GenericSelector.GenericListCallback;



public class BodyEditor extends JPanel implements ConfigurationEditor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6947204546386124876L;

	public interface DisplayConfigurationCallback {
		public DisplayConfiguration getDisplayConfiguration();
	}
	
	public enum Inputs { shift, alt, ctrl, leftMouse }
	
	//variables for drawing the image
	private Canvas canvas;
	private BufferStrategy strategy;
	
	private AnimationDrawer drawer;
	
	private boolean isAggregate = false;
	
	private boolean running;
	private Thread animationThread;
	
	private GenericListCallback<MapItemDefinition<?>> partTurretCallback;
	private DisplayConfigurationCallback dcc;
	
	private GenericSelector<MapItemState> animationState;
	private JTextField scalingFactor;
	
	private JTree containerTree;
	private BodyEditorNode root;
	private BodyEditorNode selectedNode = null;
	private JPopupMenu treePopupMenu;
	
	private List<Inputs> currentInputs = Collections.synchronizedList(new ArrayList<Inputs>());
	
	public BodyEditor(final DisplayConfigurationCallback dcc, String imagePath, GenericListCallback<MapItemDefinition<?>> partTurretCallback)
	{
		this.partTurretCallback = partTurretCallback;
		
		AnimationDrawer.setImagePath(imagePath);
		this.dcc = dcc;
		
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
				return dcc.getDisplayConfiguration().getDefinedStates();
			}
		});
		animationState.setSelectedObject(MapItemState.Idle);
		
		scalingFactor = new JTextField(20);
		JPanel scalePanel = new JPanel();
		scalePanel.setLayout(new BoxLayout(scalePanel, BoxLayout.Y_AXIS));
		scalePanel.add(new JLabel("How wide in game units should this image be?"));
		scalePanel.add(scalingFactor);
		
		JButton reloadImages = new JButton("Reload Images");
		reloadImages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnimationDrawer.reloadAllImages();
			}
		});
		
		JPanel southPanel = new JPanel();
		southPanel.add(scalePanel);
		southPanel.add(animationState);
		southPanel.add(reloadImages);
		
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
				
				while(running)
				{
					//get graphics object to draw to
					Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
					g.setColor(Color.black);
					g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
					
					Position canvasDim = new Position(canvas.getWidth(), canvas.getHeight());
					AnimationDrawer.drawImage(g, canvasDim.scale(0.5), 0,
							canvasDim, canvasDim, animationState.getSelectedObject(), dcc);
					
					synchronized(currentInputs)
					{
						Position mousePosition = new Position(0, 0);
						Point mp = canvas.getMousePosition();
						if(mp != null)
							mousePosition = new Position(mp.x, mp.y);
						root.drawShape(g, canvasDim.scale(0.5), canvasDim, mousePosition, currentInputs, getScale());
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
		animationThread.setDaemon(true);
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
			@SuppressWarnings("unchecked")
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
							Entry<String, MapItemDefinition<?>>[] possibilities = convertToEntryList(partTurretCallback.getSelectionList());
							Entry<String, MapItemDefinition<?>> entry = (Entry<String, MapItemDefinition<?>>) JOptionPane
									.showInputDialog(
											BodyEditor.this,
											"Please select a part or turret to add",
											"Selection",
											JOptionPane.PLAIN_MESSAGE, null,
											possibilities, null);
							
							selectedNode.add(new BodyEditorNode(
									getNodeName(entry.getKey()), entry.getValue(),
									BodyEditorNode.DEFAULT_TRANS,
									canvas));
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
		
		JMenuItem enable = new JMenuItem("Enable/Disable");
		enable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(selectedNode == null || selectedNode == root)
					return;
				selectedNode.setEnabled(!selectedNode.getEnabled());
				containerTree.validate();
				containerTree.updateUI();
			}
		});
		
		JMenuItem moveUp = new JMenuItem("Move up");
		moveUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectedNode == null || selectedNode == root)
					return;
				int n = selectedNode.getParent().getIndex(selectedNode);
				if(n > 0)
				{
					((BodyEditorNode)selectedNode.getParent()).insert(selectedNode, n - 1);
					containerTree.validate();
					containerTree.updateUI();
				}
			}
		});
		
		JMenuItem moveDown = new JMenuItem("Move down");
		moveDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectedNode == null || selectedNode == root)
					return;
				int n = selectedNode.getParent().getIndex(selectedNode);
				if(n < selectedNode.getParent().getChildCount() - 1)
				{
					((BodyEditorNode)selectedNode.getParent()).insert(selectedNode, n + 1);
					containerTree.validate();
					containerTree.updateUI();
				}
			}
		});
		
		JPopupMenu ret = new JPopupMenu();
		ret.add(remove);
		ret.add(add);
		ret.add(rename);
		ret.add(enable);
		ret.add(moveUp);
		ret.add(moveDown);
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
	
	private Entry<String, MapItemDefinition<?>>[] convertToEntryList(List<MapItemDefinition<?>> lst)
	{
		@SuppressWarnings("unchecked")
		Entry<String, MapItemDefinition<?>>[] ret = new Entry[lst.size()];
		for(int i = 0; i < lst.size();  i++)
			ret[i] = new Pair<String, MapItemDefinition<?>>((String)lst.get(i).getPropertyForName("bfgName").getValue(), lst.get(i));
		
		return ret;
	}
	
	private double getScale()
	{
		Scanner s = new Scanner(scalingFactor.getText());
		if(s.hasNextDouble())
			return s.nextDouble()/canvas.getWidth();
		else
			return 1;
	}
	
	private void decomposeMapItemAggregate(BodyEditorNode parent, MapItemAggregateDefinition<?> miad, double scale)
	{
		List<MapItemDefinition<?>> defs = miad.getAllContainedItems();
		List<Transformation> trans = miad.getAllRelativeTransformations();
		List<String> names = miad.getAllNames();
		List<Boolean> enabledFlags = miad.getAllEnabledFlags();
		
		for(int i = 0; i < defs.size(); i++)
		{
			BodyEditorNode ben;
			if(defs.get(i) instanceof MapItemAggregateDefinition<?>)
			{
				ben = new BodyEditorNode(names.get(i), trans.get(i));
				decomposeMapItemAggregate(ben, (MapItemAggregateDefinition<?>) defs.get(i), scale);
			}
			else
				ben = new BodyEditorNode(names.get(i), defs.get(i), 
						new Transformation(trans.get(i).getPosition().scale(1/scale), trans.get(i).getRotation()), canvas);
			ben.setEnabled(enabledFlags.get(i));
			parent.add(ben);
		}
	}

	@Override
	public void setData(Configuration cd) {
		root.removeAllChildren();
		selectedNode = null;
		
		//its not possible to not have the dimensions of the displayconfig set
		//and have parts or all of the body set
		DisplayConfiguration dc = (DisplayConfiguration) ((MapItemDefinition<?>)cd).getDisplayConfiguration();
		if(dc == null || dc.getDimensions() == null)
		{
			isAggregate = (cd instanceof MapItemAggregateDefinition<?>);
			containerTree.validate();
			containerTree.updateUI();
			return;
		}
		
		scalingFactor.setText(dc.getDimensions().getX() + "");
		double scale = getScale();
		
		if(cd instanceof MapItemAggregateDefinition<?>)
		{
			isAggregate = true;
			decomposeMapItemAggregate(root, (MapItemAggregateDefinition<?>) cd, scale);
		}
		else
		{
			isAggregate = false;
			ShapeConfiguration sc = ((MapItemDefinition<? extends MapItem>) cd).getBodyConfig();
			if(sc instanceof CircleConfiguration)
				root.add(new BodyEditorNode("Circle", new CircleDisplay((CircleConfiguration) sc, scale)));
			else if(sc instanceof RectangleConfiguration)
				root.add(new BodyEditorNode("Rectangle", new RectangleDisplay((RectangleConfiguration) sc, scale)));
			else if(sc instanceof ShapeAggregateConfiguration)
			{
				ShapeAggregateConfiguration sac = (ShapeAggregateConfiguration) sc;
				for(String name : sac.getDefinedShapeNames())
				{
					sc = sac.getShapeConfigurationForName(name);
					BodyEditorNode ben = null;
					if(sc instanceof CircleConfiguration)
						ben = new BodyEditorNode("Circle", new CircleDisplay((CircleConfiguration) sc, scale));
					else if(sc instanceof RectangleConfiguration)
						ben = new BodyEditorNode("Rectangle", new RectangleDisplay((RectangleConfiguration) sc, scale));
					else
						throw new IllegalStateException("Shape aggregates should never contain other shape aggregates");
					ben.setEnabled(sac.isInitiallyEnabled(name));
					root.add(ben);
				}
			}
		}
		containerTree.validate();
		containerTree.updateUI();
	}
	
	public void resetEditor()
	{
		root.removeAllChildren();
		selectedNode = null;
		containerTree.validate();
		containerTree.updateUI();
	}

	@Override
	public Configuration instantiateNewConfiguration() {
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
			//if the child is a part aggregate
			if(((BodyEditorNode)parent.getChildAt(i)).getMapItemDefinition() == null)
			{
				PartAggregateDefinition pad = new PartAggregateDefinition();
				fillAggregates((BodyEditorNode) parent.getChildAt(i), pad);
				mids.add(pad);
			}
			else //it the child is a part or turret
				mids.add(((BodyEditorNode)parent.getChildAt(i)).getMapItemDefinition());
			//don't forget to scale the positions
			Transformation trans = ((BodyEditorNode)parent.getChildAt(i)).getShape().getTransformation();
			trans = new Transformation(trans.getPosition().scale(getScale()), trans.getRotation());
			relativeTrans.add(trans);
			names.add((String) ((BodyEditorNode)parent.getChildAt(i)).getUserObject());
			enabledFlags.add(((BodyEditorNode)parent.getChildAt(i)).getEnabled());
		}
		tofill.setFullContainedList(mids, relativeTrans, names, enabledFlags);
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		MapItemDefinition<?> mid = (MapItemDefinition<?>) toSet;
		if(isAggregate)
		{
			fillAggregates(root, (MapItemAggregateDefinition<?>) toSet);
		}
		else
		{
			ShapeConfiguration sc = null;
			double scalingFactor = getScale();
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
									.generateConfiguration(scalingFactor),
							((BodyEditorNode) root.getChildAt(i)).getEnabled());
				mid.setBody(sac);
			}
		}
		
		if(mid.getDisplayConfiguration() == null)
			mid.setDisplayConfiguration(new DisplayConfiguration());
		((DisplayConfiguration)mid.getDisplayConfiguration()).setDimensions(
				new Position(getScale()*canvas.getWidth(), getScale()*canvas.getHeight()));
		
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
	
	private class Pair<E, V> implements Entry<E, V>
	{
		private E key;
		private V value;
		
		public Pair(E k, V v)
		{
			key = k;
			value = v;
		}
		
		@Override
		public E getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V arg0) {
			V oldV = value;
			value = arg0;
			return oldV;
		}
		
		@Override
		public String toString()
		{
			return key.toString();
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
		dc.setDimensions(new Position(400, 300));
		
		final DisplayConfiguration dc2 = new DisplayConfiguration();
		dc2.setAnimation(MapItemState.Idle, a1);
		dc2.setAnimation(MapItemState.Active, a2);
		dc2.setDimensions(new Position(800, 600));
		
		final PartDefinition testMapItem;
		testMapItem = new PartDefinition();
		testMapItem.setPropertyForName("bfgName", new Property(Usage.STRING, "TEST"));
		ShapeConfiguration sc = new RectangleConfiguration(100, 50, new Transformation(new Position(0, 0), 0)); 
		testMapItem.setBody(sc);
		testMapItem.setDisplayConfiguration(dc);
		
		final UnitDefinition testMapItem2;
		testMapItem2 = new UnitDefinition();
		testMapItem2.setPropertyForName("bfgName", new Property(Usage.STRING, "TEST2"));
		testMapItem2.setDisplayConfiguration(dc2);
		
		BodyEditor be = new BodyEditor(new DisplayConfigurationCallback() {
			@Override
			public DisplayConfiguration getDisplayConfiguration() {
				return dc;
			}
		}, "resources/animations",
		new GenericListCallback<MapItemDefinition<?>>() {
			@Override
			public List<MapItemDefinition<?>> getSelectionList() {
				List<MapItemDefinition<?>> ret = new ArrayList<MapItemDefinition<?>>();
				ret.add(testMapItem);
				return ret;
			}
		});
		be.isAggregate = true;
		frame.setContentPane(be);	
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
		be.setVisible(true);
		
		Scanner stdin = new Scanner(System.in);
		stdin.nextLine();
		System.out.println("saving");
		be.getData(testMapItem2);
		System.out.println("clearing");
		be.instantiateNewConfiguration();
		stdin.nextLine();
		System.out.println("reloading");
		be.setData(testMapItem2);
	}

}

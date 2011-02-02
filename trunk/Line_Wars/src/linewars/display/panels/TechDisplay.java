package linewars.display.panels;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JViewport;

import linewars.display.ImageDrawer;
import linewars.gamestate.Position;
import linewars.gamestate.tech.Tech;
import linewars.gamestate.tech.TechGraph;
import linewars.gamestate.tech.TechGraph.TechNode;

public class TechDisplay extends JViewport
{
	private final static int TECH_BUTTON_SIZE = 50;
	
	private TechGraph techGraph;
	private JPanel treeDisplay;
	private TechButton[] buttons;
	
	public TechDisplay(TechGraph techGraph)
	{
		this.techGraph = techGraph;
		
		initializeDisplay();
		
		ViewportDragger dragger = new ViewportDragger();
		addMouseListener(dragger);
		addMouseMotionListener(dragger);
	}

	private void initializeDisplay()
	{
		setOpaque(false);
		
		int xSize = techGraph.getMaxX() + 1;
		int ySize = techGraph.getMaxY() + 1;
		
		GridBagLayout treeLayout = new GridBagLayout();
		GridBagConstraints treeConstraints = new GridBagConstraints();
		treeDisplay = new JPanel(treeLayout);
		treeDisplay.setOpaque(false);
		add(treeDisplay);
		
		List<TechNode> orderedTechList = techGraph.getOrderedList();
		Iterator<TechNode> orderedListIterator = orderedTechList.iterator();
		buttons = new TechButton[orderedTechList.size()];
		
		TechNode current = null;
		if(orderedListIterator.hasNext())
			current = orderedListIterator.next();

		treeConstraints.gridwidth = 1;
		treeConstraints.gridheight = 1;
		treeConstraints.gridy = 0;
		int i = 0;
		for(int r = 0; r < ySize; ++r)
		{
			treeConstraints.gridx = 0;
			for(int c = 0; c < xSize; ++c)
			{
				if(current != null && current.getX() == c && current.getY() == r)
				{
					Tech tech = current.getTech();
					
					buttons[i] = new TechButton();
					buttons[i].setOpaque(false);
					buttons[i].setIcon(new ButtonIcon(buttons[i], tech.getIconURI()));
					buttons[i].setPressedIcon(new ButtonIcon(buttons[i], tech.getPressedIconURI()));
					buttons[i].setRolloverIcon(new ButtonIcon(buttons[i], tech.getRolloverIconURI()));
					buttons[i].setSelectedIcon(new ButtonIcon(buttons[i], tech.getSelectedIconURI()));
					buttons[i].addActionListener(new ButtonHandler(i));
					treeDisplay.add(buttons[i]);
					treeLayout.addLayoutComponent(buttons[i], treeConstraints);
					
					++i;
					if(orderedListIterator.hasNext())
						current = orderedListIterator.next();
					else
						current = null;
				}
				else
				{
					Component box = Box.createRigidArea(new Dimension(TECH_BUTTON_SIZE, TECH_BUTTON_SIZE));
					treeDisplay.add(box);
					treeLayout.addLayoutComponent(box, treeConstraints);
				}
				
				++treeConstraints.gridx;
			}

			++treeConstraints.gridy;
		}
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		((Graphics2D)g).setStroke(new BasicStroke(5));
		
		techGraph.unmarkAll();
		TechNode root = techGraph.getRoot();
		while(root != null)
		{
			drawDependencyLines(g, root);
			root = techGraph.getNextRoot();
		}
		
		techGraph.unmarkAll();
		
	}
	
	private void drawDependencyLines(Graphics g, TechNode node)
	{
		if(node.isMarked())
			return;
		
		node.mark();
		
		TechNode child = node.getChild();
		while(child != null)
		{
			int startX = node.getX() * TECH_BUTTON_SIZE + TECH_BUTTON_SIZE;
			int startY = node.getY() * TECH_BUTTON_SIZE + TECH_BUTTON_SIZE / 2;
			int endX = child.getX() * TECH_BUTTON_SIZE;
			int endY = child.getY() * TECH_BUTTON_SIZE + TECH_BUTTON_SIZE / 2;
			
			Position vector = new Position(startX - endX, startY - endY);
			vector = vector.normalize().scale(15);
			
			g.drawLine(startX, startY, endX, endY);	

			vector = vector.rotateAboutPosition(new Position(0, 0), Math.PI / 4);
			g.drawLine(endX, endY, endX + (int)vector.getX(), endY + (int)vector.getY());

			vector = vector.rotateAboutPosition(new Position(0, 0), -Math.PI / 2);
			g.drawLine(endX, endY, endX + (int)vector.getX(), endY + (int)vector.getY());
			
			drawDependencyLines(g, child);
			child = node.getNextChild();
		}
	}
	
	/**
	 * A button for the command card.
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class TechButton extends JButton
	{
		public TechButton()
		{
			Dimension size = new Dimension(TECH_BUTTON_SIZE, TECH_BUTTON_SIZE);
			
			setSize(size);
			setMaximumSize(size);
			setPreferredSize(size);
			setMinimumSize(size);
		}
		
		@Override
		public void paint(Graphics g)
		{
			DefaultButtonModel model = (DefaultButtonModel)getModel();
			if(model.isPressed())
				getPressedIcon().paintIcon(this, g, 0, 0);
			else if(model.isSelected())
				getSelectedIcon().paintIcon(this, g, 0, 0);
			else if(model.isRollover())
				getRolloverIcon().paintIcon(this, g, 0, 0);
			else
				getIcon().paintIcon(this, g, 0, 0);
		}
	}
	
	/**
	 * An icon for a Command Button
	 * 
	 * @author Ryan Tew
	 * 
	 */
	private class ButtonIcon implements Icon
	{
		private JButton button;
		private String uri;

		/**
		 * Constructs the icon.
		 * 
		 * @param b
		 *            The button this icon is on.
		 */
		public ButtonIcon(JButton b, String uri)
		{
			this.button = b;
			this.uri = uri;
		}

		@Override
		public int getIconHeight()
		{
			return getHeight();
		}

		@Override
		public int getIconWidth()
		{
			return getWidth();
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			ImageDrawer.getInstance().draw(g, uri + button.getWidth() + button.getHeight(), new Position(x, y), 1);
		}
	}

	private class ViewportDragger extends MouseAdapter
	{
		private Point lastPoint;
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			lastPoint = e.getPoint();
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			Point vector = e.getPoint();
			vector.translate(-lastPoint.x, -lastPoint.y);
			
			Point p = getViewPosition();
			p.translate(-vector.x, -vector.y);
			
			if(p.x < 0)
				p.setLocation(0, p.y);
			else if(p.x > treeDisplay.getWidth() - getWidth())
				p.setLocation(treeDisplay.getWidth() - getWidth(), p.y);
			
			if(p.y < 0)
				p.setLocation(p.x, 0);
			else if(p.y > treeDisplay.getHeight() - getHeight())
				p.setLocation(p.x, treeDisplay.getHeight() - getHeight());
			
			setViewPosition(p);
			lastPoint = e.getPoint();
		}
	}
	
	private class ButtonHandler implements ActionListener
	{
		private int index;
		
		public ButtonHandler(int index)
		{
			this.index = index;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Point p = buttons[index].getLocation();
		}
	}
}

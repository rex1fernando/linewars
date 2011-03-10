package linewars.gamestate.tech;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class TechGraph implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1615922455457654069L;
	
	private List<TechNode> roots;
	private Iterator<TechNode> rootIterator;
	private int maxX;
	private int maxY;
	private String name;
	private boolean enabled;
	
	public TechGraph()
	{
		this("tech");
	}
	
	public TechGraph(String name)
	{
		roots = new ArrayList<TechNode>();
		rootIterator = roots.iterator();
		this.name = name;
		enabled = true;
	}
	
	public TechNode addNode()
	{
		TechNode node = new TechNode();
		roots.add(node);
		return node;
	}
	
	public TechNode getRoot()
	{
		rootIterator = roots.iterator();
		
		return getNextRoot();
	}
	
	public TechNode getNextRoot()
	{
		if(rootIterator.hasNext())
			return rootIterator.next();
		
		return null;		
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void unmarkAll()
	{
		TechNode root = getRoot();
		while(root != null)
		{
			root.unmarkAll();
			root = getNextRoot();
		}
	}
	
	public List<TechNode> getOrderedList()
	{
		List<TechNode> orderedList = new ArrayList<TechGraph.TechNode>(roots);
		
		if(orderedList.isEmpty())
			return orderedList;
		
		unmarkAll();
		int i = 0;
		while(i < orderedList.size())
		{
			TechNode current = orderedList.get(i++);
			TechNode child = current.getChild();
			while(child != null)
			{
				if(!child.isMarked())
				{
					child.mark();
					orderedList.add(child);
				}
				child = current.getNextChild();
			}
			
		}
		
		unmarkAll();
		
		Collections.sort(orderedList);
		return orderedList;
	}
	
	public int getMaxX()
	{
		return maxX;
	}
	
	public int getMaxY()
	{
		return maxY;
	}
	
	public class TechNode implements Comparable<TechNode>
	{
		private TechConfiguration techConfig;
		private UnlockStrategy strat;
		
		private List<TechNode> parents;
		private List<TechNode> children;
		
		private Iterator<TechNode> parentIterator;
		private Iterator<TechNode> childIterator;
		
		private boolean marked;
		private boolean researched;
		
		private int x;
		private int y;
		
		private TechNode()
		{
			this.x = -1;
			this.y = -1;
			this.techConfig = null;
			this.strat = null;
			this.parents = new ArrayList<TechNode>();
			this.children = new ArrayList<TechNode>();
			this.parentIterator = parents.iterator();
			this.childIterator = children.iterator();
		}
		
		private TechNode(int x, int y)
		{
			this();
			this.x = x;
			this.y = y;
			
			if(maxX < x)
				maxX = x;
			if(maxY < y)
				maxY = y;
		}
		
		private TechNode(TechConfiguration techConfig, UnlockStrategy strat)
		{
			this();
			this.techConfig = techConfig;
			this.strat = strat;
		}
		
		private TechNode(TechConfiguration techConfig, UnlockStrategy strat, List<TechNode> parents)
		{
			this();
			this.techConfig = techConfig;
			this.strat = strat;
			this.parents = parents;
		}
		
		@Override
		public int compareTo(TechNode o)
		{
			if(this.y - o.y != 0)
				return this.y - o.y;
			
			return this.x - o.x;
		}

		public void mark()
		{
			marked = true;
		}
		
		public boolean isMarked()
		{
			return marked;
		}
		
		private void unmarkAll()
		{
			if(!marked)
				return;
			
			marked = false;
			
			TechNode child = getChild();
			while(child != null)
			{
				child.unmarkAll();
				child = getNextChild();
			}
		}
		
		public boolean isResearched()
		{
			return researched;
		}
		
		public void research()
		{
			//TODO send message to resarch tech
//			Message message = new UpgradeMessage(pID, null, buttons[index].tech.getTechConfig().getID);

			researched = true;
		}
		
		public void setPosition(int x, int y)
		{
			this.x = x;
			this.y = y;
			
			if(maxX < x)
				maxX = x;
			if(maxY < y)
				maxY = y;
		}
		
		public void setTech(TechConfiguration techConfig)
		{
			this.techConfig = techConfig;
		}
		
		public void setUnlockStrategy(UnlockStrategy strat)
		{
			this.strat = strat;
		}
		
		public boolean isUnlocked()
		{
			return true; //getUnlockStrategy().isUnlocked(this);
		}

		public void addChild(TechNode node) throws CycleException
		{
			TechGraph.this.unmarkAll();
			
			if(this == node || node.isAncestor(this))
			{
				TechGraph.this.unmarkAll();
				throw new CycleException("Adding that child to this node will create a cycle.");
			}
			
			TechGraph.this.unmarkAll();

			children.add(node);
			node.parents.add(this);
			TechGraph.this.roots.remove(node);
			
		}
		
		public int getX()
		{
			return x;
		}
		
		public int getY()
		{
			return y;
		}
		
		public TechConfiguration getTechConfig()
		{
			return techConfig;
		}
		
		public UnlockStrategy getUnlockStrategy()
		{
			return strat;
		}
		
		public TechNode getParent()
		{
			parentIterator = parents.iterator();
			
			return getNextParent();
		}

		public TechNode getChild()
		{
			childIterator = children.iterator();
			
			return getNextChild();
		}
		
		public TechNode getNextParent()
		{
			if(parentIterator.hasNext())
				return parentIterator.next();
			
			return null;		
		}
		
		public TechNode getNextChild()
		{
			if(childIterator.hasNext())
				return childIterator.next();
			
			return null;
		}
		
		/**
		 * Checks to see if this TechNode is an ancestor of the potential child.
		 * @param potentialChild The TechNode that is potentially a child of this TechNode
		 * @return true if this TechNode is an ancestor of the potential child,
		 * 			false otherwise.
		 */
		private boolean isAncestor(TechNode potentialChild)
		{
			mark();
			
			TechNode child = getChild();
			while(child != null)
			{
				if(potentialChild == child)
					return true;
				
				if(child.isAncestor(potentialChild))
					return true;
				
				child = getNextChild();
			}
			
			return false;
		}
	}
}

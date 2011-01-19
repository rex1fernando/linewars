package linewars.gamestate.tech;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TechGraph
{
	private List<TechNode> roots;
	private Iterator<TechNode> rootIterator;
	private int maxX;
	private int maxY;
	
	public TechGraph()
	{
		roots = new ArrayList<TechNode>();
		rootIterator = roots.iterator();
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
	
	public int getMaxX()
	{
		return maxX;
	}
	
	public int getMaxY()
	{
		return maxY;
	}
	
	public class TechNode
	{
		private Tech tech;
		private UnlockStrategy strat;
		
		private List<TechNode> parents;
		private List<TechNode> children;
		
		private Iterator<TechNode> parentIterator;
		private Iterator<TechNode> childIterator;
		
		private int x;
		private int y;
		
		private TechNode()
		{
			this.x = -1;
			this.y = -1;
			this.tech = null;
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
		
		private TechNode(Tech tech, UnlockStrategy strat)
		{
			this();
			this.tech = tech;
			this.strat = strat;
		}
		
		private TechNode(Tech tech, UnlockStrategy strat, List<TechNode> parents)
		{
			this();
			this.tech = tech;
			this.strat = strat;
			this.parents = parents;
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
		
		public void setTech(Tech tech)
		{
			this.tech = tech;
		}
		
		public void setUnlockStrategy(UnlockStrategy strat)
		{
			this.strat = strat;
		}

		public void addChild(TechNode node) throws CycleException
		{ 
			if(this == node || node.isAncestor(this))
				throw new CycleException("Adding that child to this node will create a cycle.");

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
		
		public Tech getTech()
		{
			return tech;
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
		
		private boolean isAncestor(TechNode parent)
		{
			TechNode child = parent.getChild();
			while(child != null)
			{
				if(this == child)
					return true;
				
				isAncestor(child);
				
				child = parent.getNextChild();
			}
			
			return false;
		}
	}
}

package linewars.gamestate.tech;

import linewars.gamestate.tech.TechGraph.TechNode;

public class UnlockStrategyNoSyblings extends UnlockStrategy
{
	public UnlockStrategyNoSyblings()
	{
		super("No Syblings");
	}
	
	@Override
	public boolean isUnlocked(TechNode node)
	{
		TechNode parent = node.getParent();
		while(parent != null)
		{
			TechNode child = parent.getChild();
			while(child != null)
			{
				if(child.isResearched())
					return false;
				
				child = parent.getNextChild();
			}
			
			parent = node.getNextParent();
		}
		
		return true;
	}
}

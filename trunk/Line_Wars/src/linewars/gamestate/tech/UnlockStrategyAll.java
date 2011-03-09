package linewars.gamestate.tech;

import linewars.gamestate.tech.TechGraph.TechNode;

public class UnlockStrategyAll extends UnlockStrategy
{
	public UnlockStrategyAll()
	{
		super("All");
	}
	
	@Override
	public boolean isUnlocked(TechNode node)
	{
		TechNode parent = node.getParent();
		while(parent != null)
		{
			if(!parent.isResearched())
				return false;
			
			parent = node.getNextParent();
		}
		
		return true;
	}
}

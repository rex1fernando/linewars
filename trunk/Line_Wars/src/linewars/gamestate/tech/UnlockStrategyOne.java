package linewars.gamestate.tech;

import linewars.gamestate.tech.TechGraph.TechNode;

public class UnlockStrategyOne extends UnlockStrategy
{
	public UnlockStrategyOne()
	{
		super("One");
	}
	
	@Override
	public boolean isUnlocked(TechNode node)
	{
		TechNode parent = node.getParent();
		while(parent != null)
		{
			if(parent.isResearched())
				return true;
			
			parent = node.getNextParent();
		}
		
		return false;
	}
}

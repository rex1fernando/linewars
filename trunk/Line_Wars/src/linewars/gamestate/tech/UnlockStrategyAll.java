package linewars.gamestate.tech;

import linewars.gamestate.tech.TechGraph.TechNode;

public class UnlockStrategyAll extends UnlockStrategy
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3078337058202212687L;

	public UnlockStrategyAll()
	{
		super("All");
	}
	
	@Override
	public boolean isUnlocked(TechNode node)
	{
		if(node.isResearched())
			return false;
		
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

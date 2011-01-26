package linewars.gamestate.tech;

import linewars.gamestate.tech.TechGraph.TechNode;

public interface UnlockStrategy
{
	public boolean isUnlocked(TechNode node);
}

package linewars.gamestate.tech;

import linewars.gamestate.tech.TechGraph.TechNode;

public abstract class UnlockStrategy
{
	private String name;
	
	public UnlockStrategy(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public abstract boolean isUnlocked(TechNode node);
}

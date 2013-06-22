package linewars.gamestate.tech;

import java.io.Serializable;

import linewars.gamestate.tech.TechGraph.TechNode;

public abstract class UnlockStrategy implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5543783626381261723L;
	
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

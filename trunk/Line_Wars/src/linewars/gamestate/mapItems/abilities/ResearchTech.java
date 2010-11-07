package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.tech.Tech;

/**
 * 
 * @author cschenck
 *
 * This class represents the ability that researches a tech.
 */
public strictfp class ResearchTech implements Ability {

	private Tech tech;
	private boolean researched = false;
	
	public ResearchTech(Tech t, boolean dud)
	{
		tech = t;
		//dud is whether or not this is a "dud" research attempt i.e. the player
		//doesn't have enough stuff so this ability gets added and immediately removed
		researched = dud;
	}
	
	@Override
	public void update() {
		if(!researched)
		{
			researched = true;
			tech.research();
		}
	}

	@Override
	public boolean killable() {
		return true;
	}

	@Override
	public boolean finished() {
		return researched;
	}

}

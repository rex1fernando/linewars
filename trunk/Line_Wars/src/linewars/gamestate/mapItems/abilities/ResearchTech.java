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
	private long researchTime;
	private long startTime;
	private boolean researched = false;
	
	public ResearchTech(Tech t, boolean dud)
	{
		tech = t;
		researchTime = 0;
		startTime = (long)(tech.getMapItemDefinition().getGameState().getTime()*1000);
		//dud is whether or not this is a "dud" research attempt i.e. the player
		//doesn't have enough stuff so this ability gets added and immediately removed
		researched = dud;
	}
	
	@Override
	public void update() {
		if(!researched && (long)(tech.getMapItemDefinition().getGameState().getTime()*1000) - startTime >= researchTime)
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

package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.Tech;

public class ResearchTech implements Ability {

	private Tech tech;
	private long researchTime;
	private long startTime;
	private boolean researched = false;
	
	public ResearchTech(Tech t, long rTime, boolean dud)
	{
		tech = t;
		researchTime = rTime;
		startTime = System.currentTimeMillis();
		//dud is whether or not this is a "dud" research attempt i.e. the player
		//doesn't have enough stuff so this ability gets added and immediately removed
		researched = dud;
	}
	
	@Override
	public void update() {
		if(!researched && System.currentTimeMillis() - startTime >= researchTime)
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

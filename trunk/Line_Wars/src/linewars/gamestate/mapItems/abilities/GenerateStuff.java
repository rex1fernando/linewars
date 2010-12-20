package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.Player;

/**
 * 
 * @author Connor Schenck
 * 
 * This class is the ability that generates stuff (currency)
 *
 */
public class GenerateStuff implements Ability {
	
	private GenerateStuffDefinition gsd;
	private Player owner;
	private double startTime;
	
	public GenerateStuff(GenerateStuffDefinition gsd, Player p)
	{
		this.gsd = gsd;
		owner = p;
		startTime = gsd.getGameState().getTime();
	}

	@Override
	public void update() {
		owner.addStuff((gsd.getGameState().getTime() - startTime)*gsd.getStuffIncome());
		startTime = gsd.getGameState().getTime();
	}

	@Override
	public boolean killable() {
		return true;
	}

	@Override
	public boolean finished() {
		return false;
	}

}

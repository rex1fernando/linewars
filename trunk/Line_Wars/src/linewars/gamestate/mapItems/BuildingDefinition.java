package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.GameState;
import linewars.gamestate.Node;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines buildings. It is a type of MapItemDefinition.
 * It knows how much a building costs and how long it takes to
 * build it.
 */
public strictfp class BuildingDefinition extends MapItemDefinition {
	
	private double cost;
	private double buildTime;

	public BuildingDefinition(String URI, Player owner, GameState gameState)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner, gameState);
	}

	/**
	 * Creates a building at the given transformation and in the
	 * given node.
	 * 
	 * @param t	the transformation to place the building at
	 * @param n	the node that contains this building
	 * @return	the created building
	 */
	public Building createBuilding(Transformation t, Node n) {
		Building b = new Building(t, this, n);
		for(AbilityDefinition ad : this.getAbilityDefinitions())
			if(ad.startsActive())
				b.addActiveAbility(ad.createAbility(b));
		return b;
	}
	
	/**
	 * 
	 * @return	the cost of this building
	 */
	public double getCost()
	{
		return cost;
	}
	
	/**
	 * 
	 * @return	the time it takes to build this building
	 */
	public double getBuildTime()
	{
		return buildTime;
	}
	
	/**
	 * 
	 * @param cost	the new cost of this building
	 */
	public void setCost(double cost)
	{
		this.cost = cost;
	}
	
	/**
	 * 
	 * @param buildTime	the new build time to set this building to
	 */
	public void setBuildTime(double buildTime)
	{
		this.buildTime = buildTime;
	}

	@Override
	protected void forceSubclassReloadConfigData() {
		cost = super.getParser().getNumber(ParserKeys.cost);
		buildTime = super.getParser().getNumber(ParserKeys.buildTime);		
	}

}

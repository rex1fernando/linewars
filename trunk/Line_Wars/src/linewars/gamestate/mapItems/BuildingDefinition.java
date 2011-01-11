package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import configuration.Property;
import configuration.Usage;

import linewars.gamestate.GameState;
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
public strictfp class BuildingDefinition extends MapItemAggregateDefinition<Building> {
	
	private double cost;
	private double buildTime;

	public BuildingDefinition() {
		super();
		
		super.setPropertyForName("cost", new Property(Usage.NUMERIC_FLOATING_POINT, null));
		super.setPropertyForName("buildTime", new Property(Usage.NUMERIC_FLOATING_POINT, null));
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
	protected Building createMapItemAggregate(Transformation t, Player owner, GameState gameState) {
		Building b = new Building(t, this, owner, gameState);
		return b;
	}

	@Override
	protected void forceAggregateSubReloadConfigData() {
		cost = (Double)super.getPropertyForName("cost").getValue();
		buildTime = (Double)super.getPropertyForName("buildTime").getValue();
	}

}

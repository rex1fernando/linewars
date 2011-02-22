package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import configuration.Property;
import configuration.Usage;

import linewars.display.IconConfiguration;
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
	private IconConfiguration iconConfig;

	public BuildingDefinition() {
		super();
		
		super.setPropertyForName("cost", new Property(Usage.NUMERIC_FLOATING_POINT, null));
		super.setPropertyForName("buildTime", new Property(Usage.NUMERIC_FLOATING_POINT, null));
		super.setPropertyForName("iconConfig", new Property(Usage.CONFIGURATION));
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
	
	public IconConfiguration getIconConfig()
	{
		return iconConfig;
	}
	
	/**
	 * 
	 * @param cost	the new cost of this building
	 */
	public void setCost(double cost)
	{
		super.setPropertyForName("cost", new Property(Usage.NUMERIC_FLOATING_POINT, cost));
	}
	
	/**
	 * 
	 * @param buildTime	the new build time to set this building to
	 */
	public void setBuildTime(double buildTime)
	{
		super.setPropertyForName("buildTime", new Property(Usage.NUMERIC_FLOATING_POINT, buildTime));
	}
	
	public void setIconConfig(IconConfiguration ic)
	{
		super.setPropertyForName("iconConfig", new Property(Usage.CONFIGURATION, ic));
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
		iconConfig = (IconConfiguration)super.getPropertyForName("iconConfig").getValue();
	}

}

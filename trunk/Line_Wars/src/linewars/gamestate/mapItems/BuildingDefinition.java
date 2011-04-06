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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8970630381058998453L;
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
	
	public String getToolTip()
	{
		return (String)super.getPropertyForName("toolTip").getValue();
	}
	
	public void setToolTip(String toolTip)
	{
		super.setPropertyForName("toolTip", new Property(Usage.STRING, toolTip));
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
		if(super.getPropertyForName("cost")!= null && 
				super.getPropertyForName("cost").getValue() != null)
			cost = (Double)super.getPropertyForName("cost").getValue();
		if(super.getPropertyForName("buildTime") != null &&
				super.getPropertyForName("buildTime").getValue() != null)
			buildTime = (Double)super.getPropertyForName("buildTime").getValue();
		if(super.getPropertyForName("iconConfig") != null)
			iconConfig = (IconConfiguration)super.getPropertyForName("iconConfig").getValue();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof BuildingDefinition)
		{
			BuildingDefinition bd = (BuildingDefinition) obj;
			return super.equals(obj) &&
					cost == bd.cost &&
					buildTime == bd.buildTime;
		}
		else 
			return false;
	}

}

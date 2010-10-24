package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.Node;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.parser.Parser.InvalidConfigFileException;
import linewars.parser.ParserKeys;

/**
 * 
 * @author cschenck
 *
 * This class defines buildings. It is a type of MapItemDefinition.
 * It knows how much a building costs and how long it takes to
 * build it.
 */
public class BuildingDefinition extends MapItemDefinition {
	
	private double cost;
	private double buildTime;

	public BuildingDefinition(String URI, Player owner)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
		cost = super.getParser().getNumericValue(ParserKeys.cost);
		buildTime = super.getParser().getNumericValue(ParserKeys.buildTime);
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
		return new Building(t, this, n);
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

}

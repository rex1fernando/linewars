package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.ConfigFileParser.InvalidConfigFileException;
import linewars.gamestate.Position;

public class BuildingDefinition extends MapItemDefinition {
	
	private double cost;
	private double buildTime;

	public BuildingDefinition(String URI, Player owner)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
		cost = super.getParser().getNumericValue("cost");
		buildTime = super.getParser().getNumericValue("buildTime");
	}

	public Building createBuilding(Position p, double rotation, Node n) {
		return new Building(p, rotation, this, n);
	}
	
	public double getCost()
	{
		return cost;
	}
	
	public double getBuildTime()
	{
		return buildTime;
	}

}

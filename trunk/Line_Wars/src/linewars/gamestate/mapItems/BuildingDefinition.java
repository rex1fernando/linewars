package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.parser.Parser.InvalidConfigFileException;
import linewars.parser.ParserKeys;

public class BuildingDefinition extends MapItemDefinition {
	
	private double cost;
	private double buildTime;

	public BuildingDefinition(String URI, Player owner)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
		cost = super.getParser().getNumericValue(ParserKeys.cost);
		buildTime = super.getParser().getNumericValue(ParserKeys.buildTime);
	}

	public Building createBuilding(Transformation t, Node n) {
		return new Building(t, this, n);
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

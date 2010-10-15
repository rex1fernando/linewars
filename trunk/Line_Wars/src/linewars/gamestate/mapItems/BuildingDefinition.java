package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.parser.ParserKeys;

public class BuildingDefinition extends MapItemDefinition {
	
	private double cost;
	private double buildTime;

	public BuildingDefinition(String URI, Player owner)
			throws FileNotFoundException {
		super(URI, owner);
		cost = super.getParser().getNumericValue(ParserKeys.cost);
		buildTime = super.getParser().getNumericValue(ParserKeys.buildTime);
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

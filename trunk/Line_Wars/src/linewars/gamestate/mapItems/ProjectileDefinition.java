package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.ConfigFileParser.InvalidConfigFileException;

public abstract class ProjectileDefinition extends MapItemDefinition {
	
	private double velocity;

	public ProjectileDefinition(String URI, Player owner)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
		
		velocity = super.getParser().getNumericValue("velocity");
		
	}
	
	public double getVelocity()
	{
		return velocity;
	}

}

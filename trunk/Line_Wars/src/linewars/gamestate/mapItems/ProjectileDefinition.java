package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.ConfigFileParser.InvalidConfigFileException;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.strategies.ImpactStrategy;

public abstract class ProjectileDefinition extends MapItemDefinition {
	
	private double velocity;
	private ImpactStrategy iStrat;

	public ProjectileDefinition(String URI, Player owner)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
		
		velocity = super.getParser().getNumericValue("velocity");
		String is = super.getParser().getStringValue("impactStrategy");
		//TODO convert string to impact strategy
		
	}
	
	public double getVelocity()
	{
		return velocity;
	}
	
	public Projectile createProjectile(Position p, double rotation)
	{
		return new Projectile(p, rotation, this, this.getCollisionStrategy(), iStrat);
	}

}

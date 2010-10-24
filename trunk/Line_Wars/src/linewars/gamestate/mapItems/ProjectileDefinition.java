package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.Lane;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.impact.DealDamageOnce;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategy;
import linewars.parser.Parser;
import linewars.parser.Parser.InvalidConfigFileException;
import linewars.parser.ParserKeys;

/**
 * 
 * @author cschenck
 *
 * This class defines a projectile. It is a type of MapItemDefinition. It
 * knows the velocity of the projectile and a template for its impact
 * strategy.
 */
public class ProjectileDefinition extends MapItemDefinition {
	
	private double velocity;
	private ImpactStrategy iStrat;

	public ProjectileDefinition(String URI, Player owner)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
		
		velocity = super.getParser().getNumericValue(ParserKeys.velocity);
		Parser is = super.getParser().getParser(ParserKeys.impactStrategy);
		if(is.getStringValue(ParserKeys.type).equalsIgnoreCase("DealDamageOnce"))
		{
			iStrat = new DealDamageOnce(is.getNumericValue(ParserKeys.damage));
		}
		else
			throw new IllegalArgumentException("Invalid impact strategy for " + this.getName());
		
	}
	
	/**
	 * 
	 * @return	the velocity of the projectile
	 */
	public double getVelocity()
	{
		return velocity;
	}
	
	/**
	 * 
	 * @param velocity	the new velocity of the projectile
	 */
	public void setVelocity(double velocity)
	{
		this.velocity = velocity;
	}
	
	/**
	 * Creates a projectile
	 * 
	 * @param t	the transformation to create the projectile at
	 * @return	the projectile
	 */
	public Projectile createProjectile(Transformation t, Lane l)
	{
		return new Projectile(t, this, this.getCollisionStrategy(), iStrat, l);
	}

}

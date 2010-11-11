package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.GameState;
import linewars.gamestate.Lane;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.strategies.impact.DealDamageOnce;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategy;

/**
 * 
 * @author cschenck
 *
 * This class defines a projectile. It is a type of MapItemDefinition. It
 * knows the velocity of the projectile and a template for its impact
 * strategy.
 */
public strictfp class ProjectileDefinition extends MapItemDefinition {
	
	private double velocity;
	private ImpactStrategy iStrat;

	public ProjectileDefinition(String URI, Player owner, GameState gameState)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner, gameState);		
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
		Projectile p = new Projectile(t, this, this.getCollisionStrategy(), iStrat, l);
		for(AbilityDefinition ad : this.getAbilityDefinitions())
			if(ad.startsActive())
				p.addActiveAbility(ad.createAbility(p));
		return p;
	}

	@Override
	protected void forceSubclassReloadConfigData() {
		velocity = super.getParser().getNumber(ParserKeys.velocity);
		ConfigData is = super.getParser().getConfig(ParserKeys.impactStrategy);
		if(is.getString(ParserKeys.type).equalsIgnoreCase("DealDamageOnce"))
		{
			iStrat = new DealDamageOnce(is.getNumber(ParserKeys.damage));
		}
		else
			throw new IllegalArgumentException("Invalid impact strategy for " + this.getName());		
	}

}

package linewars.gamestate.mapItems;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategyConfiguration;
import configuration.Property;
import configuration.Usage;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines a projectile. It is a type of MapItemDefinition. It
 * knows the velocity of the projectile and a template for its impact
 * strategy.
 */
public strictfp class ProjectileDefinition extends MapItemAggregateDefinition<Projectile> {
	
	private double velocity;
	private ImpactStrategyConfiguration iStrat;

	public ProjectileDefinition() {
		super();	
		super.setPropertyForName("velocity", new Property(Usage.NUMERIC_FLOATING_POINT));
		super.setPropertyForName("iStrat", new Property(Usage.CONFIGURATION));
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
		super.setPropertyForName("velocity", new Property(Usage.NUMERIC_FLOATING_POINT, velocity));
	}

	@Override
	protected Projectile createMapItemAggregate(Transformation t, Player owner, GameState gameState) {
		Projectile p = new Projectile(t, this, owner, gameState);
		return p;
	}

	public ImpactStrategyConfiguration getImpactStratConfig() {
		return iStrat;
	}
	
	public void setImpactStratConfig(ImpactStrategyConfiguration isc)
	{
		super.setPropertyForName("iStrat", new Property(Usage.CONFIGURATION, isc));
	}

	@Override
	protected void forceAggregateSubReloadConfigData() {
		if(super.getPropertyForName("velocity") != null && 
				super.getPropertyForName("velocity").getValue() != null)
			velocity = (Double)super.getPropertyForName("velocity").getValue();
		if(super.getPropertyForName("iStrat") != null)
			iStrat = (ImpactStrategyConfiguration)super.getPropertyForName("iStrat").getValue();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ProjectileDefinition)
		{
			ProjectileDefinition pd = (ProjectileDefinition) obj;
			return super.equals(obj) &&
					velocity == pd.velocity &&
					iStrat.equals(pd.iStrat);
		}
		else
			return false;
	}

}

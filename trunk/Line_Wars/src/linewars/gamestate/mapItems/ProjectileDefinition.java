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
	
	private static final long serialVersionUID = 7752872630909701946L;
	
	private double velocity;
	private double baseDurability;
	private ImpactStrategyConfiguration iStrat;

	public ProjectileDefinition() {
		super();	
		super.setPropertyForName("velocity", new Property(Usage.NUMERIC_FLOATING_POINT));
		super.setPropertyForName("baseDurability", new Property(Usage.NUMERIC_FLOATING_POINT));
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
	
	public double getBaseDurability()
	{
		return baseDurability;
	}
	
	/**
	 * 
	 * @param velocity	the new velocity of the projectile
	 */
	public void setVelocity(double velocity)
	{
		super.setPropertyForName("velocity", new Property(Usage.NUMERIC_FLOATING_POINT, velocity));
	}
	
	public void setBaseDurability(double baseDurability)
	{
		super.setPropertyForName("baseDurability", new Property(Usage.NUMERIC_FLOATING_POINT, baseDurability));
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
		
		if(super.getPropertyForName("baseDurability") != null && 
				super.getPropertyForName("baseDurability").getValue() != null)
			baseDurability = (Double)super.getPropertyForName("baseDurability").getValue();
		
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
					baseDurability == pd.baseDurability &&
					iStrat.equals(pd.iStrat);
		}
		else
			return false;
	}

}

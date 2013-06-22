package linewars.gamestate.mapItems;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.targeting.TargetingStrategyConfiguration;
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
	
	private double baseDurability;
	private ImpactStrategyConfiguration iStrat;
	private TargetingStrategyConfiguration tStrat;

	public ProjectileDefinition() {
		super();	
		super.setPropertyForName("baseDurability", new Property(Usage.NUMERIC_FLOATING_POINT));
		super.setPropertyForName("iStrat", new Property(Usage.CONFIGURATION));
		super.setPropertyForName("tStrat", new Property(Usage.CONFIGURATION));
	}
	
	public double getBaseDurability()
	{
		return baseDurability;
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
	
	public TargetingStrategyConfiguration getTargetingStratConfig()
	{
		return tStrat;
	}
	
	public void setTargetingStratConfig(TargetingStrategyConfiguration tsc)
	{
		super.setPropertyForName("tStrat", new Property(Usage.CONFIGURATION, tsc));
	}

	@Override
	protected void forceAggregateSubReloadConfigData() {
		if(super.getPropertyForName("baseDurability") != null && 
				super.getPropertyForName("baseDurability").getValue() != null)
			baseDurability = (Double)super.getPropertyForName("baseDurability").getValue();
		
		if(super.getPropertyForName("iStrat") != null)
			iStrat = (ImpactStrategyConfiguration)super.getPropertyForName("iStrat").getValue();
		
		if(super.getPropertyForName("tStrat") != null)
			tStrat = (TargetingStrategyConfiguration)super.getPropertyForName("tStrat").getValue();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ProjectileDefinition)
		{
			ProjectileDefinition pd = (ProjectileDefinition) obj;
			return super.equals(obj) &&
					baseDurability == pd.baseDurability &&
					iStrat.equals(pd.iStrat) &&
					tStrat.equals(pd.tStrat);
		}
		else
			return false;
	}

}

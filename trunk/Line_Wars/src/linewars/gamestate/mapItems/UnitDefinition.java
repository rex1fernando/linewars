package linewars.gamestate.mapItems;

import configuration.Property;
import configuration.Usage;
import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.strategies.combat.CombatStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.combat.NoCombatConfiguration.NoCombat;
import linewars.gamestate.mapItems.strategies.movement.ImmovableConfiguration.Immovable;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.movement.StraightConfiguration.Straight;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines a unit. It is a type of MapItemDefinition.
 * It knows the maximum health points allowed for the units it
 * creates, what type of combat strategy to use, and what type
 * of movement strategy to use.
 */
public strictfp class UnitDefinition extends MapItemAggregateDefinition<Unit> {
	
	private double maxHp;
	
	private CombatStrategyConfiguration combatStrat;
	private MovementStrategyConfiguration mStrat;

	public UnitDefinition() {
		super();
		super.setPropertyForName("maxHp", new Property(Usage.NUMERIC_FLOATING_POINT, null));
		super.setPropertyForName("combatStrat", new Property(Usage.CONFIGURATION, null));
		super.setPropertyForName("mStrat", new Property(Usage.CONFIGURATION, null));
	}
	
	/**
	 * 
	 * @return	the maximum hp allowed for units of this type.
	 */
	public double getMaxHP()
	{
		return maxHp;
	}
	
	/**
	 * 
	 * @param maxHp	the new max hp of the unit
	 */
	public void setMaxHP(double maxHp)
	{
		this.maxHp = maxHp;
	}

	@Override
	protected Unit createMapItemAggregate(Transformation t, Player owner, GameState gameState) {
		Unit u = new Unit(t, this, owner, gameState);
		return u;
	}

	@Override
	protected void forceAggregateSubReloadConfigData() {
		maxHp = (Double)super.getPropertyForName("maxHp").getValue();
		combatStrat = (CombatStrategyConfiguration)super.getPropertyForName("combatStrat").getValue();
		mStrat = (MovementStrategyConfiguration)super.getPropertyForName("mStrat").getValue();
	}
	
	public MovementStrategyConfiguration getMovementStratConfig()
	{
		return mStrat;
	}
	
	public CombatStrategyConfiguration getCombatStratConfig()
	{
		return combatStrat;
	}
	
	public void setMovementStratConfig(MovementStrategyConfiguration msc)
	{
		super.setPropertyForName("mStrat", new Property(Usage.CONFIGURATION, msc));
	}
	
	public void setCombatStratConfig(CombatStrategyConfiguration csc)
	{
		super.setPropertyForName("combatStrat", new Property(Usage.CONFIGURATION, csc));
	}

}

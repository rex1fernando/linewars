package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.combat.CombatStrategy;
import linewars.gamestate.mapItems.strategies.combat.CombatStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.combat.FocusOnTargetConfiguration;
import linewars.gamestate.mapItems.strategies.combat.NoCombatConfiguration;
import linewars.gamestate.mapItems.strategies.movement.ImmovableConfiguration;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategyConfiguration;

/**
 * 
 * @author , Connor Schenck
 *
 * This class defines gates that sit at the end of lanes.
 */
public strictfp class GateDefinition extends UnitDefinition {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2038792087029880725L;
	private MovementStrategyConfiguration mStrat;

	/**
	 * Creates a gate definition from the config at URI with owner
	 * owner.
	 * 
	 * @param URI			the URI where teh config for this gate is at
	 * @param owner			the player that owns this definition
	 * @param gameState		the game state associated with this definition
	 * @throws FileNotFoundException
	 * @throws InvalidConfigFileException
	 */
	public GateDefinition() {
		super();
		mStrat = new ImmovableConfiguration();
	}
	
	@Override
	public MovementStrategyConfiguration getMovementStratConfig()
	{
		return mStrat;
	}
	
	@Override
	public CombatStrategyConfiguration getCombatStratConfig()
	{
		return new CombatStrategyConfiguration() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 3222716959877851602L;

			@Override
			public boolean equals(Object obj) {
				return this == obj;
			}
			
			@Override
			public CombatStrategy createStrategy(final MapItem m) {
				return new CombatStrategy() {
					
					@Override
					public String name() {
						return "Gate Combat Strat";
					}
					
					@Override
					public CombatStrategyConfiguration getConfig() {
						return null;
					}
					
					@Override
					public double getRange() {
						double max = 0;
						for(Turret t : ((Unit)m).getTurrets())
							if(t.getTurretStrategy().getRange() > max)
								max = t.getTurretStrategy().getRange();
						return max;
					}
					
					@Override
					public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
						for(Turret t : ((Unit)m).getTurrets())
							t.getTurretStrategy().fight(availableEnemies, availableAllies);
					}
				};
			}
		};
	}
	
	@Override
	protected Unit createMapItemAggregate(Transformation t, Player owner, GameState gameState) {
		Unit u = new Gate(t, this, owner, gameState);
		return u;
	}
	
	public Gate createGate(Transformation t, Player owner, GameState gameState)
	{
		return (Gate)super.createMapItem(t, owner, gameState);
	}

}

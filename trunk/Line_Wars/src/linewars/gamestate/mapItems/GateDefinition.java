package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.mapItems.strategies.combat.CombatStrategyConfiguration;
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
	
	private MovementStrategyConfiguration mStrat;
	private CombatStrategyConfiguration combatStrat;

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
		combatStrat = new NoCombatConfiguration();
	}
	
	@Override
	public MovementStrategyConfiguration getMovementStratConfig()
	{
		return mStrat;
	}
	
	@Override
	public CombatStrategyConfiguration getCombatStratConfig()
	{
		return combatStrat;
	}

}

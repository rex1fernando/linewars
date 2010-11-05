package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.AllEnemies;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.combat.NoCombat;
import linewars.gamestate.mapItems.strategies.movement.Immovable;

/**
 * 
 * @author cschenck
 *
 * This class defines gates that sit at the end of lanes.
 */
public class GateDefinition extends UnitDefinition {

	public GateDefinition(String URI, Player owner, GameState gameState)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner, gameState);
	}
	
	@Override
	public Unit createUnit(Transformation t)
	{
		return createGate(t);
	}
	
	/**
	 * crteates a gate
	 * 
	 * @param t	the transformation of the gate
	 * @return	the gate
	 */
	public Gate createGate(Transformation t)
	{
		return new Gate(t, this, new Immovable(), new NoCombat());
	}
	
	@Override
	public CollisionStrategy getCollisionStrategy()
	{
		return new AllEnemies();
	}

}

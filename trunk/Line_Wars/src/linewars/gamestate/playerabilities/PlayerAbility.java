package linewars.gamestate.playerabilities;

import linewars.gamestate.Player;
import linewars.gamestate.Position;

public abstract class PlayerAbility {
	
	/**
	 * Returns true if this ability requires a valid map position
	 * to activate (e.g. a spot to nuke). False if not.
	 * 
	 * @return
	 */
	public abstract boolean requiresPosition();
	
	/**
	 * Applies this player ability at position p. If requiresPosition
	 * returns false, then ignores p.
	 * 
	 * @param p
	 */
	public abstract void apply(Position p, Player player);
	
	public abstract boolean equals(Object obj);

}

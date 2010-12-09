package linewars.gameLogic;

import linewars.gamestate.GameState;

/**
 * Provides access to the current state of the game in a thread-safe way.
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp interface GameStateProvider {
	
	/**
	 * Returns the most-updated GameState.  Throws an exception if the GameState was not first locked.
	 * @return 
	 * The most updated GameState
	 */
	public GameState getCurrentGameState();
	
	/**
	 * Locks the current GameState so it can be viewed.
	 */
	public void lockViewableGameState();
	
	/**
	 * Releases the current GameState so a new one can be acquired.
	 */
	public void unlockViewableGameState();
	
	/**
	 * Returns the update rate of this GameStateProvider, in terms of ticks per second.
	 * @return The update rate of the GameStateProvider.
	 */
	public double getUpdateRate();
}

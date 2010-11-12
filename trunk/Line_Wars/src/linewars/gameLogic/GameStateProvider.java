package linewars.gameLogic;

import linewars.gamestate.GameState;

/**
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp interface GameStateProvider {
	public GameState getCurrentGameState();
	public void lockViewableGameState();
	public void unlockViewableGameState();
	public double getUpdateRate();
}

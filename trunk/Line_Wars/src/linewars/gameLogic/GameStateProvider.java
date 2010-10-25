package linewars.gameLogic;

import linewars.gamestate.GameState;

public interface GameStateProvider {
	public GameState getCurrentGameState();
}

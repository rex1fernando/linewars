package linewars.gameLogic;

import linewars.network.messages.Message;

/**
 * Provides the ability for users to update the game with a set of Messages.
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp interface GameStateUpdater {
	
	/**
	 * Updates this GameStateUpdater's GameStates with the supplied Messages.
	 * 
	 * @param tickID
	 * The tick to which the orders belong.  This is expected to be precisely one more than the tickID supplied in the last call, or 1 if there was no previous call.
	 * @param orders
	 * The Messages which are to be applied to the GameState
	 * @return 
	 */
	public boolean addOrdersForTick(int tickID, Message[] orders);
}

package linewars.gameLogic;

import linewars.network.messages.Message;

/**
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp interface GameStateUpdater {
	public void addOrdersForTick(int tickID, Message[] orders);
}

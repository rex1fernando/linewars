package linewars.gameLogic;

import linewars.network.messages.Message;

public interface GameStateUpdater {
	public void addOrdersForTick(int tickID, Message[] orders);
}

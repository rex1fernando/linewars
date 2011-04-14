package linewars.gameLogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import linewars.gamestate.GameState;
import linewars.gamestate.MapConfiguration;
import linewars.init.PlayerData;
import linewars.network.messages.Message;

public class SingleGameStateManager implements GameStateProvider,
		GameStateUpdater {

	private long lastUpdateTime;
	private long lastLastUpdateTime;
	private Map<Integer, Message[]> orders;
	
	private GameState gameState;
	
	public SingleGameStateManager(MapConfiguration map, List<PlayerData> players)
	{
		gameState = new GameState(map, players);
		orders = new HashMap<Integer, Message[]>();
	}
	
	@Override
	public boolean addOrdersForTick(int tickID, Message[] newOrders) {
		lastLastUpdateTime = lastUpdateTime;
		lastUpdateTime = System.currentTimeMillis();
		
		Message[] copy = newOrders == null ? null : newOrders.clone();
		orders.put(tickID, copy);
		
		return updateFreeState(tickID);
	}
	
	private boolean updateFreeState(int maxTickID){
		for(int i = (int) (gameState.getTimerTick() + 1); i <= maxTickID; i++){
			final Message[] currentOrders = orders.get(i);
			
			gameState.update(currentOrders);
		}
		return (gameState.getWinningPlayer() != null);
	}

	@Override
	public GameState getCurrentGameState() {
		return gameState;
	}

	@Override
	public void lockViewableGameState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unlockViewableGameState() {
		// TODO Auto-generated method stub

	}

	@Override
	public double getUpdateRate() {
		return 1000.0 / (lastUpdateTime - lastLastUpdateTime);
	}

}

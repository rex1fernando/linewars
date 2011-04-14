package linewars.gameLogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import linewars.gamestate.GameState;
import linewars.gamestate.MapConfiguration;
import linewars.init.PlayerData;
import linewars.network.messages.Message;

public class GameStateSwappingManager implements GameStateProvider,
		GameStateUpdater {
	
	private static final int SLEEP_TIME_MS = 10;
	
	private Map<Integer, Message[]> orders;
	private GameState viewableState, freeState;
	
	private boolean waitingForSwap = false;
	
	private long lastUpdateTime;
	private long lastLastUpdateTime;
	
	public GameStateSwappingManager(MapConfiguration map, List<PlayerData> players)
	{
		orders = new HashMap<Integer, Message[]>();
		viewableState = new GameState(map, players);
		freeState = new GameState(map, players);
		
		lastUpdateTime = System.currentTimeMillis();
		lastLastUpdateTime = System.currentTimeMillis();
	}

	@Override
	public boolean addOrdersForTick(int tickID, Message[] newOrders) {
		lastLastUpdateTime = lastUpdateTime;
		lastUpdateTime = System.currentTimeMillis();
		if(orders.containsKey(tickID)){
			throw new IllegalStateException("Orders already exist for the given tickID!");
		}
		
		//add new orders to the map
		Message[] copy = newOrders == null ? null : newOrders.clone();
		orders.put(tickID, copy);
		
		//now update the free state
		boolean win = updateFreeState(tickID);
		
		
		//wait for the game states to get swapped
		waitForSwap();
		
		long time = SLEEP_TIME_MS - (System.currentTimeMillis() - lastUpdateTime);
		if(time > 0)
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		return win;
	}
	
	private boolean updateFreeState(int maxTickID){
		for(int i = (int) (freeState.getTimerTick() + 1); i <= maxTickID; i++){
			final Message[] currentOrders = orders.get(i);
			
			freeState.update(currentOrders);
		}
		return (freeState.getWinningPlayer() != null);
	}
	
	private void waitForSwap()
	{
		synchronized (this) {
			waitingForSwap = true;
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public GameState getCurrentGameState() {
		return viewableState;
	}

	@Override
	public void lockViewableGameState() {}

	@Override
	public void unlockViewableGameState() {
		synchronized (this) {
			if(waitingForSwap)
			{
				GameState temp = viewableState;
				viewableState = freeState;
				freeState = temp;
				waitingForSwap = false;
				viewableState.setLocked(true);
				freeState.setLocked(false);
				this.notify();
			}
		}
	}

	@Override
	public double getUpdateRate() {
		return 1000.0 / (lastUpdateTime - lastLastUpdateTime);
	}

}

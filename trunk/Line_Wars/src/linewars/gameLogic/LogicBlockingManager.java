package linewars.gameLogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import configuration.Configuration;

import linewars.gamestate.GameState;
import linewars.gamestate.Map;
import linewars.gamestate.MapConfiguration;
import linewars.gamestate.Race;
import linewars.init.PlayerData;
import linewars.network.messages.Message;

/**
 * This class manages a pair of GameStates in such a way as to give the Display priority access.
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp class LogicBlockingManager implements GameStateProvider, GameStateUpdater {
	
	private static final int SLEEP_TIME_MS = 10;
	
	private HashMap<Integer, Message[]> orders;
	private GameState viewableState, freeState;
	
	private long lastUpdateTime;
	private long lastLastUpdateTime;
	
	//Display: render continuously
	//Means all swapping must happen in getCurrentGameState()
	private boolean fullyUpdated;//true if there are no updates that can be done to the free state, implying that the states are ready for swapping
	private boolean locked;//true if users have locked the viewableState

	public LogicBlockingManager(MapConfiguration map, List<PlayerData> players){
		orders = new HashMap<Integer, Message[]>();
		viewableState = new GameState(map, players);
		freeState = new GameState(map, players);
		
		fullyUpdated = true;
		locked = false;
		
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
		
		boolean updated = false;
		do{
			/*synchronized (this)*/ {
				updated = fullyUpdated;
			}
			if(!updated)
				break;
			try {
				Thread.sleep(SLEEP_TIME_MS);
			} catch (InterruptedException e) {
				//FFFFFFFUUUUUUUUUUUUU
				e.printStackTrace();
			}
		} while(updated);
		
		updateFreeState(tickID);
		/*synchronized (this)*/ {
			swapStatesIfPossible();
		}
		
		if (freeState.getWinningPlayer() != null ||
			viewableState.getWinningPlayer() != null)
			return true;
		
		return false;
	}
	
	private void updateFreeState(int maxTickID){
		for(int i = (int) (freeState.getTimerTick() + 1); i <= maxTickID; i++){
			final Message[] currentOrders = orders.get(i);
			
			freeState.update(currentOrders);
			
			
			if(freeState.getTimerTick() == viewableState.getTimerTick()){
				if(!freeState.equals(viewableState)){
					System.out.println("Desync detected at tick " + freeState.getTimerTick());
					freeState.equals(viewableState);
				}
			}
		}
		/*synchronized(this)*/ {
			fullyUpdated = true;
		}
	}

	@Override
	public /*synchronized*/ GameState getCurrentGameState() {
		if(!locked){
			throw new IllegalStateException("Cannot return an unlocked GameState, please lock the GameState before requesting it.");
		}
		return viewableState;
	}

	@Override
	public /*synchronized*/ void lockViewableGameState() {
		if(locked){
			throw new IllegalStateException("GameState is already locked!");
		}
		swapStatesIfPossible();
		locked = true;
		viewableState.setLocked(true);
	}

	@Override
	public /*synchronized*/ void unlockViewableGameState() {
		locked = false;
		viewableState.setLocked(false);
		swapStatesIfPossible();
	}
	
	private void swapStatesIfPossible(){
		if(fullyUpdated && !locked){//if the free state is fully updated
			//swap
			GameState temp = freeState;
			freeState = viewableState;
			viewableState = temp;
			//free state is no longer necessarily fully updated
			fullyUpdated = false;
		}		
	}

	@Override
	public double getUpdateRate() {
		return 1000.0 / (lastUpdateTime - lastLastUpdateTime);
	}

}

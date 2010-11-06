package linewars.gameLogic;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.gamestate.GameState;
import linewars.network.messages.Message;

//TODO thread safety
//TODO document
public strictfp class LogicBlockingManager implements GameStateProvider, GameStateUpdater {
	
	private static final int SLEEP_TIME_MS = 10;
	
	private HashMap<Integer, Message[]> orders;
	private GameState viewableState, freeState;
	
	//Display: render continuously
	//Means all swapping must happen in getCurrentGameState()
	private boolean fullyUpdated;//true if there are no updates that can be done to the free state, implying that the states are ready for swapping
	private boolean locked;//true if users have locked the viewableState

	public LogicBlockingManager(String mapURI, int numPlayers, List<String> raceURIs, List<String> players) throws FileNotFoundException, InvalidConfigFileException {
		orders = new HashMap<Integer, Message[]>();
		viewableState = new GameState(mapURI, numPlayers, raceURIs, players);
		freeState = new GameState(mapURI, numPlayers, raceURIs, players);
		
		fullyUpdated = true;
		locked = false;
	}

	@Override
	public void addOrdersForTick(int tickID, Message[] newOrders) {
		if(orders.containsKey(tickID)){
			throw new IllegalStateException("Orders already exist for the given tickID!");
		}
		
		//add new orders to the map
		Message[] copy = newOrders == null ? null : newOrders.clone();
		orders.put(tickID, copy);
		
		while(fullyUpdated){
			try {
				Thread.sleep(SLEEP_TIME_MS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		updateFreeState(tickID);
		swapStatesIfPossible();
	}
	
	private void updateFreeState(int maxTickID){
		for(int i = (int) (freeState.getTimerTick() + 1); i <= maxTickID; i++){
			Message[] currentOrders = orders.get(i);
			
			freeState.update(currentOrders);
			
			if(freeState.getTimerTick() == viewableState.getTimerTick()){
				if(!freeState.equals(viewableState)){
					System.out.println("Desync detected at tick " + freeState.getTimerTick());
					freeState.equals(viewableState);
				}
			}
		}
		fullyUpdated = true;
	}

	@Override
	public GameState getCurrentGameState() {
		if(!locked){
			throw new IllegalStateException("Cannot return an unlocked GameState, please lock the GameState before requesting it.");
		}
		return viewableState;
	}

	@Override
	public void lockViewableGameState() {
		if(locked){
			throw new IllegalStateException("GameState is already locked!");
		}
		swapStatesIfPossible();
		locked = true;
	}

	@Override
	public void unlockViewableGameState() {
		locked = false;
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

}

package linewars.gameLogic;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import linewars.gamestate.GameState;
import linewars.network.messages.Message;
import linewars.parser.Parser;
import linewars.parser.Parser.InvalidConfigFileException;

//TODO thread safety
//TODO document
public class LogicBlockingManager implements GameStateProvider, GameStateUpdater {
	
	private static final int SLEEP_TIME_MS = 10;
	
	private HashMap<Integer, Message[]> orders;
	private GameState viewableState, freeState;
	
	//Display: render continuously
	//Means all swapping must happen in getCurrentGameState()
	boolean fullyUpdated;//true if there are no updates that can be done to the free state, implying that the states are ready for swapping

	public LogicBlockingManager(String mapURI, int numPlayers, List<String> raceURIs) throws FileNotFoundException, InvalidConfigFileException {
		orders = new HashMap<Integer, Message[]>();
		viewableState = new GameState(mapURI, numPlayers, raceURIs);
		freeState = new GameState(mapURI, numPlayers, raceURIs);
		
		fullyUpdated = true;
	}

	@Override
	public void addOrdersForTick(int tickID, Message[] newOrders) {
		if(orders.containsKey(tickID)){
			throw new IllegalStateException("Orders already exist for the given tickID!");
		}
		
		//add new orders to the map
		Message[] copy = newOrders.clone();
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
	}
	
	private void updateFreeState(int maxTickID){
		for(int i = (int) (freeState.getTime() + 1); i <= maxTickID; i++){
			Message[] currentOrders = orders.get(i);
			//TODO update GameState
		}
		fullyUpdated = true;
	}

	@Override
	public GameState getCurrentGameState() {
		if(fullyUpdated){//if the free state is fully updated
			//swap
			GameState temp = freeState;
			freeState = viewableState;
			viewableState = temp;
			//free state is no longer necessarily fully updated
			fullyUpdated = false;
		}
		return viewableState;
	}

}

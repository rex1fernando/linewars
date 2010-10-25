package linewars.gameLogic;

import java.util.ArrayList;
import java.util.HashMap;

import linewars.gamestate.GameState;
import linewars.network.messages.Message;
import linewars.parser.Parser;

//TODO thread safety
//TODO document
public class LuckyManager implements GameStateProvider, GameStateUpdater {
	
	private HashMap<Integer, Message[]> orders;
	private GameState viewableState, freeState;
	
	//Display: render continuously
	//Means all swapping must happen in getCurrentGameState()
	boolean fullyUpdated;//true if there are no updates that can be done to the free state, implying that the states are ready for swapping
	
	public LuckyManager(Parser initialState){
		orders = new HashMap<Integer, Message[]>();
		viewableState = new GameState(initialState);
		freeState = new GameState(initialState);
		
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
		
		updateFreeState(tickID);
	}
	
	private void updateFreeState(int maxTickID){
		fullyUpdated = false;
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

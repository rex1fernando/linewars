package linewars.network;

import java.util.ArrayList;
import java.util.HashMap;

import linewars.network.messages.Message;

public class SinglePlayerNetworkProxy implements MessageProvider,
		MessageReceiver {
	
	private int currentTick;
	private HashMap<Integer, ArrayList<Message>> messageBank;

	@Override
	public void addMessage(Message toAdd) {
		if(!messageBank.containsKey(new Integer(currentTick))){
			messageBank.put(new Integer(currentTick), new ArrayList<Message>());
		}
		messageBank.get(currentTick).add(toAdd);
	}

	@Override
	public Message[] getMessagesForTick(int tickID) {
		if(currentTick == tickID){
			currentTick++;
		}
		return messageBank.get(tickID).toArray(new Message[0]);
	}

}

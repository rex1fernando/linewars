package linewars.network;

import java.net.SocketException;

import linewars.network.messages.Message;

/**
 * Like Client, except it's a server.
 * 
 * Polls (Lazily ask) the Gatekeeper for Messages for the current tick x from the set of players from whom the server
 * hasn't heard this tick.
 * 		- If the Gatekeeper doesn’t get all of the Messages for the current tick within a certain time window, the
 * 		  server starts urgently polling
 * 
 * Once all of the Messages associated with some tick id have been received, send them all out to each Client.
 * 
 * 
 * @author Titus Klinge
 */
public class Server implements Runnable
{
	private static final int MAX_NUM_LAZY_CHECKS = 10;
	private static final long POLLING_INTERVAL_MS = 5;
	
	String[] clientAddresses;
	GateKeeper gateKeeper;
	
	int currentTick;
	Message[][] messagesForTick;
	
	private boolean lateData;
	
	public Server(String[] clientAddresses, int port) throws SocketException{
		this.clientAddresses = clientAddresses.clone();
		gateKeeper = new GateKeeper(port);//TODO retry if it fails?
		
		currentTick = 1;
	}
	
	private void doTick(){
		messagesForTick = new Message[clientAddresses.length][];
		lateData = false;
		
		//Check lazily for a while first
		for(int c = 0; c < MAX_NUM_LAZY_CHECKS; c++){
			boolean hasAllMessages = true;
			for(int i = 0; i < clientAddresses.length; i++){
				messagesForTick[i] = gateKeeper.pollMessagesForTick(currentTick, clientAddresses[i]);
				if(messagesForTick[i] == null){
					hasAllMessages = false;
				}else{
					lateData = true;
				}
			}
			if(hasAllMessages){
				//send messages
				sendMessages();
				//ready state for next tick
				finalizeTick();
				return;
			}
			//sleep so the thread isn't going nuts
			try {
				Thread.sleep(POLLING_INTERVAL_MS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//if we reach this point, we have determined that some of the data is late
		//so we should get some urgent polling on
		while(true){//TODO give up eventually?
			boolean hasAllMessages = true;
			for(int i = 0; i < clientAddresses.length; i++){
				messagesForTick[i] = gateKeeper.urgentlyPollMessagesForTick(currentTick, clientAddresses[i]);
				if(messagesForTick[i] == null){
					hasAllMessages = false;
				}
			}
			if(hasAllMessages){
				//send messages
				sendMessages();
				//ready state for next tick
				finalizeTick();
				return;
			}
			//sleep so the thread isn't going nuts
			try {
				Thread.sleep(POLLING_INTERVAL_MS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void finalizeTick() {
		// TODO Auto-generated method stub
		
	}

	private void sendMessages() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run()
	{
		//start Gatekeeper in a new thread
		//TODO or should this be done elsewhere?
		new Thread(
			new Runnable(){
			public void run(){
				gateKeeper.startListening();
			}
		}).start();
		
		while(true){
			doTick();
		}
	}
}

package linewars.network;

import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import linewars.network.messages.Message;
import linewars.network.messages.SupDawgMessage;

/**
 * Encapsulates the process of collating and distributing the Messages - in both
 * directions - as needed on the client side.
 * 
 * Receives Messages from Display and stores them
 * 
 * Urgently polls Gatekeeper for Messages from the server when GameLogic asks for its Messages
 * 
 * GameLogic says ‘tick x is starting, give me my messages!’
 * 		- Urgently ask the Gatekeeper for the full set of all messages for tick x from the server.
 * 		- If the messages are all there, give them to GameLogic
 * 		- If they aren’t, throw an exception or return something special, GameLogic will deal with it from there.
 * 		- On success
 * 			Pack up all Messages due to be sent to the server and send them with a tick id of x + k, where k is
 * 			some positive integral constant.
 * 
 * @author Titus Klinge
 */
public class Client implements MessageProvider, MessageReceiver, Runnable
{
	private static final int K = 6;
	
	private List<Message> messages;
	private GateKeeper gateKeeper;
	private String serverAddress;
	
	private int currentTick;
	
	private Object tickLock = new Object();
	
	public Client(String serverAddress, int port) throws SocketException
	{
		this.serverAddress = serverAddress;
		gateKeeper = new GateKeeper(port);
		
		currentTick = 1;
		messages = new LinkedList<Message>();
	}
	
	@Override
	public void addMessage(Message msg)
	{
		synchronized(tickLock)
		{
			msg.setTimeStep(currentTick + K);
			messages.add(msg);
		}
	}

	@Override
	public Message[] getMessagesForTick(int tickID)
	{
		Message[] toReturn = gateKeeper.urgentlyPollMessagesForTick(tickID);
		
		synchronized(tickLock)
		{
			Message[] msgs = messages.toArray(new Message[messages.size()]);
			gateKeeper.pushMessagesForTick(msgs, currentTick + K);
			currentTick++;
		}
		
		return toReturn;
	}

	@Override
	public void run()
	{
		// NOTE this method steals this thread!  the start listening uses
		// the current thread to do the listening.
		gateKeeper.startListening();
	}
}

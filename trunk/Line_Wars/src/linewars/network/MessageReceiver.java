package linewars.network;

import linewars.network.messages.Message;

public interface MessageReceiver
{
	public void addMessage(Message toAdd);
}

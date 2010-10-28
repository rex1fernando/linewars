package linewars.network;

import linewars.network.messages.Message;

public interface MessageProvider
{
	Message[] getMessagesForTick(int tickID);
}

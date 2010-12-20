package linewars.network;

import linewars.network.messages.Message;

/**
 * 
 * @author Taylor Bergquist
 *
 */
public interface MessageProvider
{
	Message[] getMessagesForTick(int tickID);
}

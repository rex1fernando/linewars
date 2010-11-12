package linewars.network;

import linewars.network.messages.Message;

/**
 * 
 * @author Taylor Bergquist
 *
 */
public interface MessageReceiver
{
	public void addMessage(Message toAdd);
}

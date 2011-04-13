package linewars.network;

/**
 * 
 * @author Taylor Bergquist
 *
 */
public interface MessageHandler extends MessageProvider, MessageReceiver, Runnable {

	void terminate();}

package titustest;

import java.net.SocketException;
import java.util.Scanner;

import linewars.network.Client;
import linewars.network.messages.BuildMessage;
import linewars.network.messages.Message;

public class ClientTest
{
	private static final String SERVER_ADDRESS = "127.0.0.1";
	private static final int SERVER_PORT = 8000;
	private static final int PLAYER_ID = 0;
	
	public static void main(String[] args) throws SocketException
	{
		Client client = new Client(SERVER_ADDRESS, SERVER_PORT);
		new Thread(client).start();
		
		Scanner stdin = new Scanner(System.in);
		while (stdin.hasNext())
		{
			String com = stdin.nextLine();
			if (com.equals("add"))
			{
				client.addMessage(new BuildMessage(PLAYER_ID, 5, 5));
				System.out.println("Added 1 message");
			} else if (com.substring(0, 3).equals("get"))
			{
				Message[] msgs = client.getMessagesForTick(Integer.parseInt(com.substring(3, com.length()).trim()));
				System.out.println(msgs.length);
			}
		}
	}
}

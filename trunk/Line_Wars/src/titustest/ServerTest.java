package titustest;

import java.net.SocketException;

import linewars.network.Server;

public class ServerTest
{
	private static final String CLIENT_ADDRESS = "127.0.0.1";
	private static final int CLIENT_PORT = 8000;
	
	public static void main(String[] args) throws SocketException
	{
		Server server = new Server(new String[]{CLIENT_ADDRESS}, CLIENT_PORT);
		new Thread(server).start();
	}
}

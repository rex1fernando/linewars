package menu.networking;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable
{
	private boolean isReplay;
	private Object selection;
	private List<PlayerBean> players;
	
	private ServerSocket serverSocket;
	private List<ClientConnection> clients;	// TODO make thread safe
	
	private boolean running;
	
	public Server(int port, boolean isReplay, Object selection)
	{
		clients = new ArrayList<ClientConnection>();
		players = new ArrayList<PlayerBean>();
		
		this.isReplay = isReplay;
		this.selection = selection;
		
		// try to open the socket
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO handle exception
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		// starts the server in its own thread
		Thread th = new Thread(this);
		th.setDaemon(true);
		th.setName("Server");
		th.start();
	}
	
	@Override
	public void run()
	{
		running = true;
		while (running)
		{
			// accepts a new client
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				// keep trying
				continue;
			}
			
			// now we have a new socket to work with
			ClientConnection client = new ClientConnection(clientSocket, clients.size());
			
			// add the new connection to our list of clients
			clients.add(client);
			
			// adds the new player
			players.add(getDefaultPlayerBean());
			
			// send the info needed to the new client
			NetworkUtil.writeObject(client.out, client.playerId);
			NetworkUtil.writeObject(client.out, players.toArray(new PlayerBean[0]));
			NetworkUtil.writeObject(client.out, isReplay);
			NetworkUtil.writeObject(client.out, selection);
			
			// notify the other players
			for (ClientConnection conn : clients)
			{
				if (conn != client)
					conn.sendMessage(MessageType.playerJoin, client.playerId, players.get(client.playerId));
			}
		}	
	}
	
	private PlayerBean getDefaultPlayerBean()
	{
		// TODO implement
		return new PlayerBean("Default Name", Color.black, 1, "Race 1");
	}
	
	private void forwardToClients(ClientConnection sender, MessageType msgType, Object ... objs)
	{
		Object[] newObjs = new Object[objs.length + 1];
		newObjs[0] = sender.playerId;
		for (int i = 1; i < newObjs.length; ++i)
			newObjs[i] = objs[i-1];
		
		for (ClientConnection c : clients)
				c.sendMessage(msgType, newObjs);
	}
	
	private class ClientConnection implements Runnable
	{
		private ObjectInputStream in;
		private ObjectOutputStream out;
		
		private int playerId;
		private boolean running;
		
		public ClientConnection(Socket socket, int playerId)
		{
			this.playerId = playerId;
			
			try {
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				// TODO handle this problem getting IO for socket
				e.printStackTrace();
			}
			
			// starts the server in its own thread
			Thread th = new Thread(this);
			th.setName("c" + playerId);
			th.setDaemon(true);
			th.start();
		}
		
		@Override
		public void run()
		{
			running = true;
			while (running)
			{
				MessageType type = (MessageType) NetworkUtil.readObject(in);
				handleMessage(type);
			}
		}
		
		@Override
		public boolean equals(Object o)
		{
			// TODO impelemnt
			return (o == this);
		}
		
		@Override
		public int hashCode()
		{
			// TODO implement
			return 0;
		}
		
		public void sendMessage(MessageType type, Object ... obj)
		{
			NetworkUtil.writeObject(out, type);
			for (int i = 0; i < obj.length; ++i)
			{
				NetworkUtil.writeObject(out, obj[i]);
			}
		}
		
		private void handleMessage(MessageType type)
		{
			PlayerBean pb = players.get(playerId);
			
			switch (type)
			{
				case name:
					String name = (String) NetworkUtil.readObject(in);
					if (!name.equals(pb.getName())) {
						pb.setName(name);
						forwardToClients(this, MessageType.name, name);						
					}
					break;
				case color:
					Color color = (Color) NetworkUtil.readObject(in);
					if (!color.equals(pb.getColor())) {
						pb.setColor(color);
						forwardToClients(this, MessageType.color, color);						
					}
					break;
				case slot:
					int slot = (Integer) NetworkUtil.readObject(in);
					if (pb.getSlot() != slot) {
						pb.setSlot(slot);
						forwardToClients(this, MessageType.slot, slot);
					}
					break;
				case race:
					String race = (String) NetworkUtil.readObject(in);
					if (!race.equals(pb.getRace())) {
						pb.setRace((race));
						forwardToClients(this, MessageType.race, race);	
					}
					break;
				case chat:
					forwardToClients(this, type, NetworkUtil.readObject(in));
					break;
				case selection:
					Object o = NetworkUtil.readObject(in);
					if (!o.equals(selection)) {
						selection = o;
						forwardToClients(this, MessageType.selection, selection);
					}
					break;
				case isReplay:
					boolean ir = (Boolean) NetworkUtil.readObject(in);
					if (ir != isReplay) {
						isReplay = ir;
						forwardToClients(this, type, isReplay);
					}
					break;
				case clientCancelGame:
					forwardToClients(this, type);
					for (int i = playerId + 1; i < clients.size(); ++i) clients.get(i).playerId -= 1;
					players.remove(playerId);
					clients.remove(playerId);
					running = false;
					break;
				case serverCancelGame:
					Server.this.running = false;
					try {
						serverSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					forwardToClients(this, type);
					break;
			}
		}
	}
}

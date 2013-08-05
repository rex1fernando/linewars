package menu.networking;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.Race;
import menu.ContentProvider;
import menu.panels.CreateGamePanel;

public class Server implements Runnable
{
	private ServerSocket serverSocket;
	private boolean isReplay;
	private Object selection;
	
	private List<PlayerBean> players;
	private List<ClientConnection> clients;
	private Object clientLock = new Object();
	
	private CreateGamePanel gamePanel;
	
	private boolean running;
	
	public Server(int port, boolean isReplay, Object selection, CreateGamePanel gp) throws IOException
	{
		gamePanel = gp;
		clients = new ArrayList<ClientConnection>();
		players = new ArrayList<PlayerBean>();
		
		this.isReplay = isReplay;
		this.selection = selection;
		
		serverSocket = new ServerSocket(port);
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
			ClientConnection client = null;
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
				client = new ClientConnection(clientSocket, clients.size());
			} catch (IOException e) {
				// keep trying
				continue;
			}
			
			synchronized (clientLock)
			{
				clients.add(client);
				PlayerBean pb = getDefaultPlayerBean();
				pb.setName(client.playerName);
				players.add(pb);
				
				// send the info needed to the new client
				NetworkUtil.writeObject(client.out, client.playerId);
				NetworkUtil.writeObject(client.out, isReplay);
				NetworkUtil.writeObject(client.out, selection);
				NetworkUtil.writeObject(client.out, players.toArray(new PlayerBean[0]));
				
				// notify the other players
				for (ClientConnection conn : clients)
				{
					if (conn != client)
						conn.sendMessage(MessageType.playerJoin, client.playerId, players.get(client.playerId));
				}
			}
		}	
	}
	
	private PlayerBean getDefaultPlayerBean()
	{
		PlayerBean pb = null;
		synchronized (clientLock)
		{
			pb = new PlayerBean("Player", ContentProvider.getColors()[0], 1, 0); // FIXME
		}
		return pb;
	}
	
	private void forwardToClients(ClientConnection sender, MessageType msgType, Object ... objs)
	{
		Object[] newObjs = new Object[objs.length + 1];
		newObjs[0] = sender.playerId;
		for (int i = 1; i < newObjs.length; ++i)
			newObjs[i] = objs[i-1];
		
		synchronized (clientLock)
		{
			for (ClientConnection c : clients) c.sendMessage(msgType, newObjs);	
		}
	}
	
	private class ClientConnection implements Runnable
	{
		private ObjectInputStream in;
		private ObjectOutputStream out;
		
		private Object privateLock = new Object();
		private String ipAddress;
		
		private int playerId;
		private boolean running;
		private String playerName;
		
		public ClientConnection(Socket socket, int playerId) throws IOException
		{
			this.playerId = playerId;
			ipAddress = socket.getInetAddress().toString();
			ipAddress = ipAddress.substring(ipAddress.lastIndexOf("/") + 1);
			
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			
			playerName = (String) NetworkUtil.readObject(in);
			
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
				if (type != null) handleMessage(type); else running = false;
			}
		}
		
		@Override
		public boolean equals(Object o)
		{
			if ((o instanceof ClientConnection) == false)
				return false;
			
			ClientConnection c = (ClientConnection) o;
			
			return (playerId == c.playerId && ipAddress.equals(c.ipAddress));
		}
		
		@Override
		public int hashCode()
		{
			return playerId * 53 + ipAddress.hashCode() * 29;
		}
		
		public void sendMessage(MessageType type, Object ... obj)
		{
			synchronized (privateLock)
			{
				NetworkUtil.writeObject(out, type);
				for (int i = 0; i < obj.length; ++i)
				{
					NetworkUtil.writeObject(out, obj[i]);
				}
			}
		}
		
		private void handleMessage(MessageType type)
		{
			PlayerBean pb = null;
			synchronized (clientLock) {
				pb = players.get(playerId);
			}
			
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
					Integer raceIndex = (Integer) NetworkUtil.readObject(in);
					Race race = ContentProvider.getRaces()[raceIndex];
					if (raceIndex != null && !race.equals(pb.getRaceIndex())) {
						pb.setRaceIndex(raceIndex);
						forwardToClients(this, MessageType.race, raceIndex);	
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
					synchronized (clientLock) {
						for (int i = playerId + 1; i < clients.size(); ++i) clients.get(i).playerId -= 1;
						players.remove(playerId);
						clients.remove(playerId);
					}
					running = false;
					break;
				case serverCancelGame:
					forwardToClients(this, type);
					Server.this.running = false;
					try {
						serverSocket.close();
					} catch (IOException e) { /* DO NOTHING */}
					break;
				case startGame:
					beginGameInitialization();
			}
		}
		
		private List<PlayerBean> createCopyOfBeans(List<PlayerBean> lst)
		{
			List<PlayerBean> ret = new ArrayList<PlayerBean>();
			for(PlayerBean pb : lst)
				ret.add(pb.copy());
			return ret;
		}
		
		private void beginGameInitialization()
		{
			synchronized (clientLock)
			{
				List<Object> list = new ArrayList<Object>();
				list.add(isReplay);
				list.add(selection);
				list.add(createCopyOfBeans(players).toArray(new PlayerBean[0]));
				
				List<String> ipAddresses = new ArrayList<String>();
				for (ClientConnection c : clients)
				{
					ipAddresses.add(c.ipAddress);
				}
				list.add(ipAddresses.toArray(new String[0]));
				
				// TODO implement observer
				
				for (ClientConnection c : clients)
				{
					list.add(0, c.playerId);
					c.sendMessage(MessageType.startGame, list.toArray());
					list.remove(0);
				}
			}
		}
	}
}

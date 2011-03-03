package menu.networking;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import menu.creategame.CreateGamePanel;

public class Client implements Runnable
{
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int playerIndex;
	private CreateGamePanel gamePanel;
	private boolean running;
	
	public Client(int port, String serverIp, CreateGamePanel gamePanel)
	{
		this.gamePanel = gamePanel;
		try {
			socket = new Socket(serverIp, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		
		playerIndex = (Integer) NetworkUtil.readObject(in);
		PlayerBean[] pbs = (PlayerBean[]) NetworkUtil.readObject(in);
		
		gamePanel.setReplay((Boolean) NetworkUtil.readObject(in));
		gamePanel.setSelection(NetworkUtil.readObject(in));
		
		for (int i = 0; i < pbs.length; ++i)
		{
			gamePanel.addPlayer((i == playerIndex));
			gamePanel.updatePlayerPanel(i, pbs[i]);
		}
	}
	
	public int getPlayerIndex()
	{
		return playerIndex;
	}
	
	public void start()
	{
		// starts the client in its own thread
		Thread th = new Thread(this);
		th.setDaemon(true);
		th.setName("Client");
		th.start();
	}
	
	public void sendMessage(MessageType type, Object ... obj)
	{
		NetworkUtil.writeObject(out, type);
		for (Object o : obj)
		{
			NetworkUtil.writeObject(out, o);
		}
	}

	@Override
	public void run()
	{
		running = true;
		while (running)
		{
			MessageType type = (MessageType) NetworkUtil.readObject(in);
			if (type != null)
				handleMessage(type);
		}
	}
	
	private void handleMessage(MessageType type)
	{
		int playerId = (Integer) NetworkUtil.readObject(in);
		switch (type)
		{
		case name:
			gamePanel.setPlayerName(playerId, (String) NetworkUtil.readObject(in));
			break;
		case slot:
			gamePanel.setPlayerSlot(playerId, (Integer) NetworkUtil.readObject(in));
			break;
		case race:
			gamePanel.setPlayerRace(playerId, (String) NetworkUtil.readObject(in));
			break;
		case color:
			gamePanel.setPlayerColor(playerId, (Color) NetworkUtil.readObject(in));
			break;	
		case chat:
			gamePanel.updateChat((String) NetworkUtil.readObject(in));
			break;
		case isReplay:
			gamePanel.setReplay((Boolean) NetworkUtil.readObject(in));
			break;
		case selection:
			gamePanel.setSelection(NetworkUtil.readObject(in));
			break;
		case playerJoin:
			gamePanel.addPlayer(false);
			gamePanel.updatePlayerPanel(playerId, (PlayerBean) NetworkUtil.readObject(in));
			break;
		case clientCancelGame:
			if (playerId < playerIndex) playerIndex--;
			gamePanel.removePlayer(playerId);
			running = false;
			break;
		case serverCancelGame:
			running = false;
			break;
		}
	}
}

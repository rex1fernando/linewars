package menu.networking;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;
import linewars.display.sound.SoundPlayer;
import linewars.display.sound.SoundPlayer.SoundType;
import linewars.gamestate.MapConfiguration;
import linewars.gamestate.Race;
import linewars.init.PlayerData;
import menu.ContentProvider;
import menu.GameInitializer;
import menu.panels.CreateGamePanel;
import menu.panels.OptionsPane;

public class Client implements Runnable
{
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int playerIndex;
	private CreateGamePanel gamePanel;
	private boolean running;
	
	public Client(int port, String serverIp, CreateGamePanel gamePanel, String playerName) throws SocketException
	{
		this.gamePanel = gamePanel;
		try {
			socket = new Socket(serverIp, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			throw new SocketException();
		}
		
		NetworkUtil.writeObject(out, playerName);
		
		playerIndex = (Integer) NetworkUtil.readObject(in);
		boolean isReplay = (Boolean) NetworkUtil.readObject(in);
		gamePanel.setReplay(isReplay);
		gamePanel.setSelection(NetworkUtil.readObject(in));
		PlayerBean[] pbs = (PlayerBean[]) NetworkUtil.readObject(in);
		
		for (int i = 0; i < pbs.length; ++i)
		{
			gamePanel.addPlayer((i == playerIndex && !isReplay));
			gamePanel.updatePlayerPanel(i, pbs[i]);
		}
	}
	
	private void cleanup()
	{
		socket = null;
		gamePanel = null;
		out = null;
		in = null;
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
			gamePanel.setPlayerRace(playerId, (Integer) NetworkUtil.readObject(in));
			break;
		case color:
			Color c = (Color) NetworkUtil.readObject(in);
			gamePanel.setPlayerColor(playerId, c);
			break;	
		case chat:
			gamePanel.updateChat((String) NetworkUtil.readObject(in));
			break;
		case isReplay:
			gamePanel.setReplay((Boolean) NetworkUtil.readObject(in));
			break;
		case selection:
			gamePanel.setSelection(NetworkUtil.readObject(in));
			gamePanel.repaint();
			break;
		case playerJoin:
			if (playerIndex != playerId) {
				gamePanel.addPlayer(false);
				gamePanel.updatePlayerPanel(playerId, (PlayerBean) NetworkUtil.readObject(in));
			}
			break;
		case clientCancelGame:
			if (playerId == playerIndex)
				running = false;
			else
				gamePanel.removePlayer(playerId);
			
			if (playerId < playerIndex) playerIndex--;
			break;
		case serverCancelGame:
			running = false;
			if (socket.getInetAddress().getHostAddress().toString().equals("127.0.0.1") == false)
				JOptionPane.showMessageDialog(gamePanel, "The host left the game.");
			
			gamePanel.goBackToTitleMenu();
			break;
		case startGame:
			boolean isReplay = (Boolean) NetworkUtil.readObject(in);
			Object selection = NetworkUtil.readObject(in);
			PlayerBean[] players = (PlayerBean[]) NetworkUtil.readObject(in);
			
			String[] ipAddresses = (String[]) NetworkUtil.readObject(in);
			boolean isObserver = false;  // TODO implement observing
			
			List<PlayerData> playerList = convertToPlayerData(players);
			List<String> clientList = new ArrayList<String>();
			for (int i = 0; i < ipAddresses.length; ++i) clientList.add(ipAddresses[i]);
			
			if (isReplay)
			{
				// TODO implement
			}
			else
			{
				GameInitializer gameInit = new GameInitializer();
				gameInit.setMap((MapConfiguration) selection);
				gameInit.setServerIp(socket.getInetAddress().getHostAddress());
				gameInit.setPlayerList(playerList);
				gameInit.setClientList(clientList);
				gameInit.setObserver(isObserver);
				gameInit.setPlayerId(playerId);
				gamePanel.startGame(gameInit);
			}			
			
			// cleanup();
			break;
		}
	}
	
	private List<PlayerData> convertToPlayerData(PlayerBean[] players)
	{
		List<PlayerData> playerList = new ArrayList<PlayerData>();
		Race[] races = ContentProvider.getRaces();
		for (int i = 0; i < players.length; ++i)
		{
			PlayerData pd = new PlayerData();
			pd.setColor(players[i].getColor());
			pd.setName(players[i].getName());
			pd.setRace(races[players[i].getRaceIndex()]);
			pd.setStartingSlot(players[i].getSlot());
			playerList.add(pd);
		}
		return playerList;
	}
}

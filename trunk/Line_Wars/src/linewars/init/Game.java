package linewars.init;

import java.net.SocketException;
import java.util.List;

import linewars.display.Display;
import linewars.display.ImageDrawer;
import linewars.display.sound.SoundPlayer;
import linewars.gameLogic.TimingManager;
import linewars.gamestate.MapConfiguration;
import linewars.network.Client;
import linewars.network.MessageHandler;
import linewars.network.Server;

/**
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp class Game {
	
	private static final int SOCKET_PORT = 9001;
	
	private Display display;
	private SoundPlayer sound;
	private MessageHandler networking;
	private TimingManager logic;
	private Server server;
	private List<PlayerData> players;
	
	public void run(){
		Thread serv = new Thread(server);
		if(server != null){
			serv.setName("Server");
			serv.setDaemon(true);
			serv.start();
		}
		
		Thread net = new Thread(networking);
		net.setDaemon(true);
		net.setName("Client GateKeeper");
		net.start();
		
		Thread log = new Thread(logic);
		log.setDaemon(true);
		log.setName("Game Logic");
		log.start();
		
		Thread disp = new Thread(display);
		for(int i = 0; i < players.size(); i++)
			ImageDrawer.getInstance().setPlayerColor(i, players.get(i).getColor());
		disp.setName("Display");
		disp.start();
		
		Thread sp = new Thread(sound);
		sp.setDaemon(true);
		sp.setName("Sound");
		sp.start();
		
		try {
			disp.join();
			net.join();
			log.join();
			sp.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Game(MapConfiguration map, List<PlayerData> players){
		logic = new TimingManager(map, players);
		this.players = players; 
	}
	
	/**
	 * Called only on the host computer.  Creates the server object for managing the game
	 * and gives it the address of each player so that the server can properly communicate
	 * with all players.
	 * 
	 * @param clientAddresses The list of all player addresses in the game.
	 */
	public void initializeServer(List<String> clientAddresses)
	{
		try {
			server = new Server(clientAddresses.toArray(new String[0]), SOCKET_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Called by every client in the game (including the host).  Initializes the client object
	 * with the server's address and also initializes the display module with the player's
	 * index in the game. 
	 * 
	 * @param serverAddress The address of the server computer.
	 * @param playerIndex This player's player index.
	 */
	public void initializeClient(String serverAddress, int playerIndex, boolean isObserver)
	{
		try {
			networking = new Client(serverAddress, SOCKET_PORT);
		} catch (SocketException e) {
			// if this happens.... well crap...
			e.printStackTrace();
		}
		
		display = new Display(logic.getGameStateManager(), networking, playerIndex);
		sound = SoundPlayer.getInstance();
		logic.setClientReference(networking);
	}
}

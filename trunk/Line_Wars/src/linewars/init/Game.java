package linewars.init;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.display.Display;
import linewars.gameLogic.TimingManager;
import linewars.network.Client;
import linewars.network.MessageHandler;
import linewars.network.Server;
import linewars.network.SinglePlayerNetworkProxy;

/**
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp class Game {
	
	private static final int SOCKET_PORT = 9001;
	
	private Display display;
	private MessageHandler networking;
	private TimingManager logic;
	private Server server;
	
	private String mapDefinitionURI;
	private int numPlayers;
	private ArrayList<String> raceDefinitionURIs;
	private ArrayList<String> playerNames;
	ArrayList<String> playerAddresses;
	private String serverAddress;

	
	/**
	 * 
	 * @deprecated
	 * @param args
	 * mapDefinitionURI numPlayers serverAddress raceURI0...raceURIn playerName0...playerNamen playerAddress0...playerAddressn
	 */
	public static void main(String[] args){
		ArrayList<String> raceURIs = new ArrayList<String>();
		ArrayList<String> players = new ArrayList<String>();
		ArrayList<String> playerAddresses = new ArrayList<String>();
		int numPlayers = Integer.parseInt(args[1]);
		for(int i = 0; i < numPlayers; i++){
			raceURIs.add(args[3 + i]);
			players.add(args[3 + numPlayers + i]);
			if(args[2].equals("127.0.0.1") && numPlayers > 1){
				playerAddresses.add(args[3 + 2 * numPlayers + i]);
			}
		}
		Game toStart = new Game(args[0], numPlayers, args[2], raceURIs, players, playerAddresses);
		
		toStart.initialize();
		
		toStart.run();
	}
	
	/**
	 * @deprecated
	 * @param map
	 * @param players
	 * @param server
	 * @param races
	 * @param names
	 * @param addresses
	 */
	public Game(String map, int players, String server, ArrayList<String> races, ArrayList<String> names, ArrayList<String> addresses){
		mapDefinitionURI = map;
		numPlayers = players;
		raceDefinitionURIs = races;
		serverAddress = server;
		playerNames = names;
		playerAddresses = addresses;
	}
	
	/**
	 * @deprecated
	 */
	public void initialize(){
		//single player init
		if(numPlayers == 1){
			if(playerAddresses.size() == 0){
				networking = new SinglePlayerNetworkProxy();
			}else{//if the player gave a client address, use the actual networking instead of the proxy
				try {
					server = new Server(playerAddresses.toArray(new String[0]), SOCKET_PORT);
				} catch (SocketException e) {
					e.printStackTrace();
				}
				try
				{
					networking = new Client(serverAddress, SOCKET_PORT);
				}
				catch (SocketException e)
				{
					// if this happens.... well crap...
					e.printStackTrace();
				}
			}
		}
		//multiplayer init
		else if(numPlayers > 1){
			
			//if this player is the server
			if(serverAddress.equals("127.0.0.1")){
				try {
					server = new Server(playerAddresses.toArray(new String[0]), SOCKET_PORT);
				} catch (SocketException e) {
					e.printStackTrace();
				}
			}
			
			try
			{
				networking = new Client(serverAddress, SOCKET_PORT);
			}
			catch (SocketException e)
			{
				// if this happens.... well crap...
				e.printStackTrace();
			}
		}
		
		//init for every # of players
		try {
			logic = new TimingManager(mapDefinitionURI, numPlayers, raceDefinitionURIs, playerNames);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidConfigFileException e) {
			e.printStackTrace();
		}
		
		//TODO pass in the actual current player to the display
		display = new Display(logic.getGameStateManager(), networking, 0);
		logic.setClientReference(networking);
	}
	
	public void run(){
		if(server != null){
			Thread serv = new Thread(server);
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
		disp.setName("Display");
		disp.start();
	}
	
	/**
	 * Creates a new Game object and initializes the the logic with the parameters given.
	 * 
	 * @param mapURI The location of the map configuration file to be played.
	 * @param numPlayers The number of players that will be playing in the game.
	 * @param raceURIs The list of races corresponding to each player.
	 * @param playerNames The list of names of each player corresponding to the races.
	 */
	public Game(String mapURI, int numPlayers, List<String> raceURIs, List<String> playerNames)
	{
		this.numPlayers = numPlayers;
		try {
			logic = new TimingManager(mapURI, numPlayers, raceURIs, playerNames);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidConfigFileException e) {
			e.printStackTrace();
		}
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
		//single player mode does not require a server
		if(numPlayers != 1)
		{
			try {
				server = new Server(clientAddresses.toArray(new String[0]), SOCKET_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
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
	public void initializeClient(String serverAddress, int playerIndex)
	{
		// if playing by yourself, use the proxy
		if (numPlayers == 1)
		{
			networking = new SinglePlayerNetworkProxy();
		}
		else
		{
			try {
				networking = new Client(serverAddress, SOCKET_PORT);
			} catch (SocketException e) {
				// if this happens.... well crap...
				e.printStackTrace();
			}
		}
		
		display = new Display(logic.getGameStateManager(), networking, playerIndex);
		logic.setClientReference(networking);
	}
}

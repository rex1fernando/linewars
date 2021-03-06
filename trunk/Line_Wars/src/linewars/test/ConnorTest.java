package linewars.test;



import java.io.FileNotFoundException;
import java.net.SocketException;
import java.util.ArrayList;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.display.Display;
import linewars.gameLogic.TimingManager;
import linewars.network.Client;
import linewars.network.MessageHandler;
import linewars.network.Server;
import linewars.network.SinglePlayerNetworkProxy;

//TODO test
//TODO document
public class ConnorTest {
	
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
	 * @param args
	 * mapDefinitionURI numPlayers serverAddress raceURI0...raceURIn playerName0...playerNamen
	 */
	public static void main(String[] args){
		ArrayList<String> raceURIs = new ArrayList<String>();
		ArrayList<String> players = new ArrayList<String>();
		ArrayList<String> playerAddresses = new ArrayList<String>();
		int numPlayers = Integer.parseInt(args[1]);
		for(int i = 0; i < numPlayers; i++){
			raceURIs.add(args[3 + i]);
			players.add(args[3 + numPlayers + i]);
			if(args[2].equals("127.0.0.1")){
				playerAddresses.add(args[3 + 2 * numPlayers + i]);
			}
		}
		ConnorTest toStart = new ConnorTest(args[0], numPlayers, args[2], raceURIs, players, playerAddresses);
		
		toStart.initialize();
		
		toStart.run();
	}
	
	public ConnorTest(String map, int players, String server, ArrayList<String> races, ArrayList<String> names, ArrayList<String> addresses){
		mapDefinitionURI = map;
		numPlayers = players;
		raceDefinitionURIs = races;
		serverAddress = server;
		playerNames = names;
		playerAddresses = addresses;
	}
	
	public void initialize(){
		//single player init
		if(numPlayers > 0){
			networking = new SinglePlayerNetworkProxy();
		}
		//multiplayer init
		else {
			
			//if this player is the server
			if(serverAddress.equals("127.0.0.1")){
				try {
					server = new Server(null, numPlayers);
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
			serv.setDaemon(true);
			serv.start();
		}
		Thread net = new Thread(networking);
		net.setDaemon(true);
		net.start();
		Thread log = new Thread(logic);
		log.setDaemon(true);
		log.start();
		Thread disp = new Thread(display);
		disp.start();
	}
}

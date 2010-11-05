package linewars.init;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.util.ArrayList;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.display.Display;
import linewars.gameLogic.TimingManager;
import linewars.network.Client;
import linewars.network.Server;
import linewars.network.SinglePlayerNetworkProxy;

//TODO test
//TODO document
public class Game {
	
	private static final int SOCKET_PORT = 9001;
	
	private Display display;
	private Client networking;
	//private SinglePlayerNetworkProxy networking;
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
		Game toStart = new Game(args[0], numPlayers, args[2], raceURIs, players, playerAddresses);
		
		toStart.initialize();
		
		toStart.run();
	}
	
	public Game(String map, int players, String server, ArrayList<String> races, ArrayList<String> names, ArrayList<String> addresses){
		mapDefinitionURI = map;
		numPlayers = players;
		raceDefinitionURIs = races;
		serverAddress = server;
		playerNames = names;
		playerAddresses = addresses;
	}
	
	public void initialize(){
		try {
			logic = new TimingManager(mapDefinitionURI, numPlayers, raceDefinitionURIs, playerNames);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidConfigFileException e) {
			e.printStackTrace();
		}
		if(serverAddress.equals("127.0.0.1")){
			try {
				server = new Server(null, numPlayers);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//networking = new SinglePlayerNetworkProxy();
		try
		{
			networking = new Client(serverAddress, SOCKET_PORT);
		}
		catch (SocketException e)
		{
			// if this happens.... well crap...
			e.printStackTrace();
		}
		display = new Display(logic.getGameStateManager(), networking);
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

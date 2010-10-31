package linewars.init;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.util.ArrayList;

import linewars.display.Display;
import linewars.gameLogic.TimingManager;
import linewars.network.Client;
import linewars.network.SinglePlayerNetworkProxy;
import linewars.parser.Parser.InvalidConfigFileException;

//TODO test
//TODO document
public class Game {
	
	private static final int SOCKET_PORT = 9001;
	
	private Display display;
	//private Client networking; TODO
	private SinglePlayerNetworkProxy networking;
	private TimingManager logic;
	
	private String mapDefinitionURI;
	private int numPlayers;
	private ArrayList<String> raceDefinitionURIs;
	private ArrayList<String> playerNames;
	private String serverAddress;
	
	/**
	 * 
	 * @param args
	 * mapDefinitionURI numPlayers serverAddress raceURI0...raceURIn playerName0...playerNamen
	 */
	public static void main(String[] args){
		ArrayList<String> raceURIs = new ArrayList<String>();
		ArrayList<String> players = new ArrayList<String>();
		int numPlayers = Integer.parseInt(args[1]);
		for(int i = 0; i < numPlayers; i++){
			raceURIs.add(args[3 + i]);
			players.add(args[3 + numPlayers + i]);
		}
		Game toStart = new Game(args[0], numPlayers, args[2], raceURIs, players);
		
		toStart.initialize();
		
		toStart.run();
	}
	
	public Game(String map, int players, String server, ArrayList<String> races, ArrayList<String> names){
		mapDefinitionURI = map;
		numPlayers = players;
		raceDefinitionURIs = races;
		serverAddress = server;
		playerNames = names;
	}
	
	public void initialize(){
		try {
			logic = new TimingManager(mapDefinitionURI, numPlayers, raceDefinitionURIs, playerNames);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidConfigFileException e) {
			e.printStackTrace();
		}
		networking = new SinglePlayerNetworkProxy();
		/*TODO
		try
		{
			//networking = new Client(serverAddress, SOCKET_PORT);
		}
		catch (SocketException e)
		{
			// if this happens.... well crap...
			e.printStackTrace();
		}*/
		display = new Display(logic.getGameStateManager(), networking);
		logic.setClientReference(networking);
		//TODO
	}
	
	public void run(){
		//Thread net = new Thread(networking); TODO
		//net.setDaemon(true);
		//net.start();
		Thread log = new Thread(logic);
		log.setDaemon(true);
		log.start();
		Thread disp = new Thread(display);
		disp.start();
	}
}

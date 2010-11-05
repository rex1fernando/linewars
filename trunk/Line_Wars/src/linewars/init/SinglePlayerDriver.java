package linewars.init;

import java.util.ArrayList;

public class SinglePlayerDriver {
	public static void main(String[] args){
		String mapDefinitionURI = "resources/maps/map1.cfg";
		int numPlayers = 1;
		String serverAddress = "127.0.0.1";
		String raceURI1 = "resource/races/thatOneRace.cfg";
		String playerName1 = "Knexer";
		String playerAddress1 = "127.0.0.1";
		ArrayList<String> raceURIs = new ArrayList<String>();
		raceURIs.add(raceURI1);
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add(playerName1);
		ArrayList<String> playerAddresses = new ArrayList<String>();
		playerAddresses.add(playerAddress1);
		
		Game toStart = new Game(mapDefinitionURI, numPlayers, serverAddress, raceURIs, playerNames, playerAddresses);
	}
}

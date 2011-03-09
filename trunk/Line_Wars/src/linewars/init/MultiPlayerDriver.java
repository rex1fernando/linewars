package linewars.init;

import java.util.ArrayList;

/**
 * For testing only.
 * @author Taylor Bergquist
 *
 */
public class MultiPlayerDriver {
	public static void main(String[] args) {
		String mapDefinitionURI = "resources/maps/3_lane_map.cfg";
		int numPlayers = 2;
		String serverAddress = "129.186.40.179";
		String raceURI1 = "resources/races/thatOneRace.cfg";
		String playerName1 = "Titus";
		String playerName2 = "Taylor";
		String playerAddress1 = serverAddress;
		String playerAddress2 = "127.0.0.1";
		ArrayList<String> raceURIs = new ArrayList<String>();
		raceURIs.add(raceURI1);
		raceURIs.add(raceURI1);
		ArrayList<String> playerNames = new ArrayList<String>();
		playerNames.add(playerName1);
		playerNames.add(playerName2);
		ArrayList<String> playerAddresses = new ArrayList<String>();
		playerAddresses.add(playerAddress1);
		playerAddresses.add(playerAddress2);

		Game toStart = new Game(mapDefinitionURI, numPlayers, serverAddress,
				raceURIs, playerNames, playerAddresses);
		toStart.initialize();
		toStart.run();
	}
}
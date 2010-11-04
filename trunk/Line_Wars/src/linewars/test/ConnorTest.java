package linewars.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.gamestate.GameState;

public class ConnorTest {

	/**
	 * @param args
	 * @throws InvalidConfigFileException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, InvalidConfigFileException {
		ArrayList<String> players = new ArrayList<String>();
		players.add("thatOneGuy");
		ArrayList<String> raceURIs = new ArrayList<String>();
		raceURIs.add("resources/races/thatOneRace.cfg");
		GameState gs = new GameState("resources/maps/map1.cfg", 1, raceURIs, players);
		//testing the constructor of GameMap
		System.out.println("Success!");
	}

}

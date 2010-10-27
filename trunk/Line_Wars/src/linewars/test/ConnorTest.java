package linewars.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import linewars.gamestate.GameState;
import linewars.parser.Parser.InvalidConfigFileException;

public class ConnorTest {

	/**
	 * @param args
	 * @throws InvalidConfigFileException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, InvalidConfigFileException {
		GameState gs = new GameState("resources/maps/map1.cfg", 0, new ArrayList<String>(), new ArrayList<String>());
		//testing the constructor of GameMap
		System.out.println("Success!");
	}

}

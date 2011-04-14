package linewars.test;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import linewars.gamestate.MapConfiguration;
import linewars.gamestate.NodeConfiguration;
import linewars.gamestate.Race;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.init.Game;
import linewars.init.PlayerData;
import linewars.init.UserData;

public class temp2userstart 
{
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		//get the map
		MapConfiguration testMap = (MapConfiguration)(new ObjectInputStream(new FileInputStream(new File("resources/maps/testMap.cfg"))).readObject());
//		for(NodeConfiguration n : testMap.nodes())
//			n.setStartNode(true);
		
		//get the race
		Race testRace = (Race)(new ObjectInputStream(new FileInputStream(new File("resources/races/testRace.cfg"))).readObject());
//		Race testRace = createRace();
		
		//construct the player
		PlayerData knexer = new PlayerData();
		knexer.setColor(Color.black);
		knexer.setName("Knexer");
		knexer.setRace(testRace);
		knexer.setStartingSlot(1);
		
		PlayerData rae = new PlayerData();
		rae.setColor(Color.white);
		rae.setName("rae");
		rae.setRace(testRace);
		rae.setStartingSlot(2);
		
		//create the list of players
		ArrayList<PlayerData> playerList = new ArrayList<PlayerData>();
		playerList.add(knexer);
		playerList.add(rae);
		
		//create the list of clients
		ArrayList<String> clientList = new ArrayList<String>();
		clientList.add("10.25.70.82");
		clientList.add("129.186.150.111");
		
		//construct the game
//		Game testGame = new Game(testMap, playerList);
//		testGame.initializeServer(clientList);
//		testGame.initializeClient("127.0.0.1", 1, false);
//		testGame.run();
	}
	
//	private static Race createRace()
//	{
//		BuildingDefinition b = new BuildingDefinition();
//		b.se
//		Race r = new Race();
//		r.addBuilding(b, true);
//		
//		return r;
//	}
}

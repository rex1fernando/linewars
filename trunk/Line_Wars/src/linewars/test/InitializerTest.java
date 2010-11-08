package linewars.test;

import java.util.*;

import org.junit.*;

import linewars.init.*;


public class InitializerTest {

	private String mapDefinitionURI;
	private int numPlayers;
	private ArrayList<String> raceDefinitionURIs;
	private ArrayList<String> playerNames;
	ArrayList<String> playerAddresses;
	private String serverAddress;
	private Game gm;
	
	@Before
	public void setUp()
	{
		mapDefinitionURI = "map1.cfg";
		numPlayers = 2;
		serverAddress = "129.186.197.7";
		raceDefinitionURIs.add("thatOneRace.cfg");
		playerNames.add("Ryan");
		playerAddresses.add("129.186.197.7");
		
	}
	
	@Test
	public void testEmptyGame()
	{
		gm = new Game(null, 0, null, null, null, null);
		
	}
	
	@Test
	public void testGame()
	{
		gm = new Game(mapDefinitionURI, numPlayers, serverAddress, raceDefinitionURIs, playerNames, playerAddresses);
	}
	
	@Test
	public void testInitialize()
	{
		gm.initialize();
	}
}

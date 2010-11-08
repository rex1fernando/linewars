package linewars.test;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.List;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.gamestate.GameState;
import org.junit.*;

public class GameStateTest {

	private String mapURI;
	private int numPlayers;
	private List<String> raceURIs;
	private List<String> playerNames;
	private GameState game;
	
	
	@Before
	public void setUp() throws FileNotFoundException, InvalidConfigFileException
	{
		numPlayers = 2;
		mapURI = "map1.cfg";
		raceURIs.add("Human");
		playerNames.add("Jo");
	}
	
	@Test
	public void testGameState() throws FileNotFoundException, InvalidConfigFileException
	{
		 new GameState(mapURI, numPlayers, raceURIs, playerNames);
	}
	
	@Test
	public void testFileNotFoundException()
	{
		
	}
	
	@Test
	public void testInvalidConfigFileException()
	{
		
	}
	
	@Test
	public void testGetNumPlayers()
	{
		assertEquals(2, game.getNumPlayers());
	}
}

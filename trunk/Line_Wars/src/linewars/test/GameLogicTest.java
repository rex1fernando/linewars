package linewars.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.*;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.gameLogic.LogicBlockingManager;


public class GameLogicTest {

	private TestVariables test;
	private LogicBlockingManager lbm;
	
	@Before
	public void setUp() throws FileNotFoundException, InvalidConfigFileException, NullPointerException
	{
		lbm = new LogicBlockingManager(test.getMapURI(), test.getNumPlayers(), test.getPlayers(), test.getRaceURIs());
	}
	
	@Test
	public void testNullPointerException() throws NullPointerException, FileNotFoundException, InvalidConfigFileException
	{
		//lbm = new LogicBlockingManager(null, 0, null, null);
	}
	
	@Test
	public void testFileNotFound()
	{
		
	}
	
	@Test
	public void testInvalidConfigFile()
	{
		
	}
	
	@Test
	public void testOrders()
	{
		//assertEquals(1, lbm.getOrders());
	}
	
	@Test
	public void testViewableState()
	{
		//assertEquals(1, lbm.getViewableState());
	}
	
	@Test
	public void testFreeState()
	{
		//assertEquals(1, lbm.getFreeState());
	}
	
	@Test
	public void testFullyUpdated()
	{
		//assertEquals(true, lbm.getFullyUpdate());
	}
	
	@Test
	public void testLocked()
	{
		//assertEquals(false, lbm.getLocked());
	}
	
	@Test
	public void testLastUpdateTime()
	{
		
	}
	
	@Test
	public void testLastLastUpdateTime()
	{
		
	}
}

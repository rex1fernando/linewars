package linewars.test;

import static org.junit.Assert.*;

import org.junit.*;
import linewars.network.messages.*;

/*
 * TODO Test apply(GamesState GameState) found in UpgradeMessage and Message
 */

public class MessageTest {

	private int id;
	private int timestep;
	private int pid;
	private int nodeid;
	private int abilityid;
	private SupDawgMessage m;
	
	@Before
	public void setUp()
	{
		id = 123;
		timestep = 2;
		pid = 50;
		nodeid = 3456;
		abilityid = 54;
		m = new SupDawgMessage(pid);
		m.setTimeStep(timestep);
	}
	
	@Test
	public void testBuildMessage()
	{
		BuildMessage bm = new BuildMessage(pid, nodeid, abilityid);
	}
	
	@Test
	public void testMessage()
	{
		
	}
	
	@Test
	public void testPlayerId()
	{
		assertEquals(50, m.getPlayerId());
	}
	
	@Test
	public void testSetTimeStep()
	{
		assertEquals(2, m.getTimeStep());
	}
	
	@Test
	public void testUpgradeMessage()
	{
		UpgradeMessage um = new UpgradeMessage(pid, nodeid, abilityid);
		assertEquals(3456, um.getTechGraphID());
		assertEquals(54, um.getTechID());
	}
}



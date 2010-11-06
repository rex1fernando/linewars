package linewars.test;

import org.junit.*;
import linewars.network.messages.*;

public class MessageTest {

	private int id;
	private int timestep;
	private int pid;
	private int nodeid;
	private int abilityid;
	
	@Before
	public void setUp()
	{
		id = 123;
		timestep = 2;
		pid = 50;
		nodeid = 3456;
		abilityid = 54;
	}
	
	@Test
	public void testPlayerID()
	{
		id = 123;
	}
	
	@Test
	public void testSetTimeStep()
	{
		timestep = 2;
	}
	
	@Test
	public void testBuildMessage()
	{
		BuildMessage bm = new BuildMessage(pid, nodeid, abilityid);
	}
}

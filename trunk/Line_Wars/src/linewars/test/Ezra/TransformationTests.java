package linewars.test.Ezra;

import static org.junit.Assert.*;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

import org.junit.Before;
import org.junit.Test;


public class TransformationTests {

	Transformation t1, t2, t3;
	
	@Before
	public void setUp()
	{
		t1 = new Transformation(new Position (5.5, 5), 55);
		t3 = new Transformation(new Position (5.5, 5), 55);
		t2 = new Transformation (null, -5);
	}
	
	@Test
	public void testGetPosition()
	{
		assertEquals(t1.getPosition(), new Position (5.5, 5));
		assertEquals(t2.getPosition(), null);
	}

	@Test
	public void testGetRotation()
	{
		assertEquals(t1.getRotation(), 55, 0.0);
		assertEquals(t2.getRotation(), -5, 0.0);
	}

	@Test
	public void testEquals()
	{
		assertEquals (t1, t3);
		assertEquals(false, t2.equals(t3));
		assertEquals(true, t3.equals(t1));
	}
	
}

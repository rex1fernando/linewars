package linewars.test;

import static org.junit.Assert.*;


import org.junit.*;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.*;



public class AABBTest {

	
	@Before
	public void setUp()
	{
	}
	
	@Test
	public void testAABBForRectangle()
	{
		Rectangle r = new Rectangle(new Transformation(new Position(50, 100), 0), 150, 200);
		assertEquals(-25, r.getAABB().getXMin(), 0.001);
		assertEquals(0, r.getAABB().getYMin(), 0.001);
		assertEquals(125, r.getAABB().getXMax(), 0.001);
		assertEquals(200, r.getAABB().getYMax(), 0.001);
	}
	
	@Test
	public void testAABBForShapeAggregate()
	{
		
	}
	
}

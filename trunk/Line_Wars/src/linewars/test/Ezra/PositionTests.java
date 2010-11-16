package linewars.test.Ezra;

//Author: Ezra Cheron
//Tests Position class 

import static org.junit.Assert.*;
import linewars.gamestate.Position;

import org.junit.After;
import org.junit.Before;

import org.junit.Test;


public class PositionTests 
{

	private Position p1, p2, p3, p4, p5, p6;
	
	@Before
	public void setUp() 
	{
		p1 = new Position (-55, 5.7);
		p2 = new Position (78.3, 0);
		p3 = new Position ("66.7, 5");
		p4 = new Position ("5a66");
		p5 = new Position (20, 0);
		p6 = new Position (66.7, 5);
		//TODO:
		//test third ctor
	}
	
	@Test
	public void testXY()
	{
		assertEquals(p1.getX(), -55, 0.0);
		assertEquals(p2.getX(), 78.3, 0.0);
		assertEquals(p3.getX(), 66.7, 0.0);
		assertEquals(p4.getX(), 5, 0.0);
		assertEquals(p1.getY(), 5.7, 0.0);
		assertEquals(p2.getY(), 0, 0.0);
		assertEquals(p3.getY(), 5, 0.0);
		assertEquals(p4.getY(), 66 , 0.0);
	}
	
	

	
	@Test
	public void testAdd()
	{
		Position p = p1.add(p2);
		assertEquals(p.getX(), (-55 + 78.3), 0.0);
		assertEquals(p.getY(), (5.7 + 0), 0.0);
		//Make sure p1 and p2 are unchanged
		assertEquals(p1.getX(), -55, 0.0);
		assertEquals(p2.getX(), 78.3, 0.0);
		assertEquals(p1.getY(), 5.7, 0.0);
		assertEquals(p2.getY(), 0, 0.0);
		
		
		p = p2.add(p3);
		assertEquals(p.getX(), (78.3 + 66.7), 0.0);
		assertEquals(p.getY(), (0 + 5), 0.0);
		
		
		p = p2.add(p3.getX(), p3.getY());
		assertEquals(p.getX(), (78.3 + 66.7), 0.0);
		assertEquals(p.getY(), (0 + 5), 0.0);
	}
	
	@Test
	public void testSubtract ()
	{
		Position p = p1.subtract(p2);
		assertEquals(p.getX(), (-55 - 78.3), 0.0);
		assertEquals(p.getY(), (5.7 - 0), 0.0);
		//Make sure p1 and p2 are unchanged
		assertEquals(p1.getX(), -55, 0.0);
		assertEquals(p2.getX(), 78.3, 0.0);
		assertEquals(p1.getY(), 5.7, 0.0);
		assertEquals(p2.getY(), 0, 0.0);
		
		
		p = p2.subtract(p3);
		assertEquals(p.getX(), (78.3 - 66.7), 0.0);
		assertEquals(p.getY(), (0 - 5), 0.0);
		p = p2.subtract(p3.getX(), p3.getY());
		assertEquals(p.getX(), (78.3 - 66.7), 0.0);
		assertEquals(p.getY(), (0 - 5), 0.0);
	}
	
	@Test
	public void testScale()
	{
		Position p = p1.scale(.5);
		assertEquals(p.getX(), (.5 * -55), 0.0);
		assertEquals(p.getY(), (.5 * 5.7), 0.0);
		p = p2.scale(4.6);
		assertEquals(p.getX(), (4.6 * 78.3), 0.0);
		assertEquals(p.getY(), (4.6 * 0), 0.0);
	}
	
	@Test
	public void testDistanceSquared()
	{
		assertEquals(p2.distanceSquared(p5), ((78.3-20) * (78.3 - 20)), 0.0);
		assertEquals(p1.distanceSquared(p1), 0, 0.0);
		assertEquals(p1.distanceSquared(p3), ((-55-66.7) *(-55-66.7)) + ((5.7-5) * (5.7-5)), 0.0);
	}
	
	@Test
	public void testEquals ()
	{
		assertEquals (false, p1.equals("-55, 5.7"));
		assertEquals(true, p1.equals(p1));
		assertEquals(false, p1.equals(p6));
		assertEquals(true, p3.equals(p6));
		assertEquals(true, p6.equals(p3));
		assertEquals(false, p5.equals(null));
	}
	
	@Test
	public void testNormalize()
	{
		//TODO:
	}
	
	@Test
	public void testOrthogonal()
	{
		//TODO:
	}
	
	@Test
	public void testDot()
	{
		//TODO:
	}

	@Test
	public void testScalarProjection()
	{
		//TODO:
	}

	@Test
	public void testVectorProjection()
	{
		//TODO:
	}

	@Test
	public void testHashCode()
	{
		//TODO:
	}
	
	@Test
	public void testToString()
	{
		//TODO:
	}

	@Test
	public void testLength()
	{
		//TODO:
	}

	@Test
	public void testGetUnitVector()
	{
		//TODO:
	}

}

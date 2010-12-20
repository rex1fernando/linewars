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

	private Position p1, p2, p3, p4, p5, p6, p7;
	
	@Before
	public void setUp() 
	{
		p1 = new Position (-55, 5.7);
		p2 = new Position (78.3, 0);
		p3 = new Position ("66.7, 5");
		p4 = new Position ("5a66");
		p5 = new Position (20, 0);
		p6 = new Position (66.7, 5);
		p7 = new Position (3, 4);
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
		assertEquals(p1.normalize().length(), 1, 0.01);
		assertEquals(p1.normalize(), p1.normalize());
		assertEquals(p1.getX(), -55, 0);
		
	}
	
	@Test
	public void testOrthogonal()
	{
		//simple method, just needs to compute (y, -x)
		assertEquals(p1.orthogonal().getX(), p1.getY(), 0.0);
		assertEquals(p1.orthogonal().getY(), -p1.getX(), 0.0);
		assertEquals(p2.orthogonal().getX(), p2.getY(), 0.0);
		assertEquals(p2.orthogonal().getY(), -p2.getX(), 0.0);
		assertEquals(p1.dot(p1.orthogonal()), 0, 0.00000000000000000);
		assertEquals(p2.dot(p2.orthogonal()), 0, 0.00000000000000000);
		//insure that p1 remains unchanged
		assertEquals(p1.getX(), -55, 0);
		assertEquals(p1.getY(), 5.7, 0.0);
	}
	
	@Test
	public void testDot()
	{
		assertEquals(p1.dot(p2), (p1.getX() * p2.getX()) + (p1.getY()* p2.getY()) , 0.0);
		assertEquals(p5.dot(p5), p5.getX() * p5.getX(), 0.00);
	}

	@Test
	public void testScalarProjection()
	{
		assertEquals(p1.scalarProjection(p1), p1.length(), 0.0001);
		assertEquals(p1.scalarProjection(p5), -55, 0);
		assertEquals(p6.scalarProjection(p7), p6.dot(p7.normalize()), 0.0);
		assertEquals(p6.getX(), 66.7, 0);
	}

	@Test
	public void testVectorProjection()
	{
		assertEquals(p1.vectorProjection(p3), p3.normalize().scale(p1.scalarProjection(p3)));
		assertEquals(p4.vectorProjection(p5), p5.normalize().scale(p4.scalarProjection(p5)));
		assertEquals(p5.vectorProjection(p4), p4.normalize().scale(p5.scalarProjection(p4)));
		assertEquals(p5.vectorProjection(p5), p5);
		assertEquals(p2.vectorProjection(p2), p2);
	}

	@Test
	public void testLength()
	{
		assertEquals(p7.length(), 5, 0);
		assertEquals(p3.length(), 66.88714, 0.0001);
		assertEquals(p5.length(), 20, 0);
		assertEquals(p1.length(), 55.29457, 0.0001);
	}

}

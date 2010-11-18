package linewars.test.Ezra;

import static org.junit.Assert.*;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.Circle;
import linewars.gamestate.shapes.Rectangle;

import org.junit.Before;
import org.junit.Test;


public class RectangleTests 
{
	//TODO:
	//Test the static constructFromParser
	
	Rectangle r1, r2, r3, r4, r5;
	//private double width, height;
	private Transformation trans, trans1, trans2;
	private Position pos, pos1, pointNW, pointNE, pointSW, pointSE;
	private Circle circ;
	
	
	@Before
	public void setUp()
	{
		//TODO:
		//constructors
		
		pos = new Position(0, 0);
		pos1 = new Position(102.2, 36.1234);
		trans = new Transformation(pos, 0);
		trans1 = new Transformation(pos, 0);
		trans2 = new Transformation(pos1, 0);
		r1 = new Rectangle(trans, 100, 50);
		r2 = new Rectangle(trans1, 6.3, 7.2);
		r3 = new Rectangle(trans, 230, -5);
		r4 = new Rectangle(trans1, 0, 0);
		r5 = new Rectangle(trans2, -1, 1);
		//circ = new Circle(trans, 55.90169943749474);
	}
	
	@Test
	public void testBoundingCircle()
	{
		// Want to test that the rectangle is inside the circle
		// This is done by:
		// 1. Calculating the diagonals of the rectangle vs the diameter of the circle
		double diagonal = Math.sqrt(Math.pow(r1.getWidth(), 2) + Math.pow(r1.getHeight(), 2))/2;
		circ = new Circle(trans, diagonal);
		//assertEquals(circ, r1.boundingCircle());
		
		pointNW = new Position(0, 0);
		pointNE = new Position(100, 0);
		pointSW = new Position(0, 50);
		pointSE = new Position(100, 50);
		assertEquals(circ.positionIsInShape(pointNW), r1.positionIsInShape(pos));
		
	}
	
	@Test
	public void testBoundingRectangle()
	{
		
	}
	
	@Test
	public void testGetData()
	{
		
	}
	
	@Test
	public void testGetEdgeVectors()
	{
	
	}
	
	@Test
	public void testGetHeight()
	{
		assertEquals(100, r1.getWidth(), 0.0);
		assertEquals(6.3, r2.getWidth(), 0.0);
		assertEquals(230, r3.getWidth(), 0.0);
		assertEquals(0, r4.getWidth(), 0.0);
		assertEquals(-1, r5.getWidth(), 0.0);
	}
	
	@Test
	public void testGetVertexPositions()
	{
		
	}
	
	@Test
	public void testGetWidth()
	{
		assertEquals(50, r1.getHeight(), 0.0);
		assertEquals(7.2, r2.getHeight(), 0.0);
		assertEquals(-5, r3.getHeight(), 0.0);
		assertEquals(0, r4.getHeight(), 0.0);
		assertEquals(1, r5.getHeight(), 0.0);
	}

	@Test
	public void testGetHashCode()
	{
		
	}
	
	@Test
	public void testIsCollidingWith()
	{
		
	}
	
	@Test
	public void testPosition()
	{
		
	}
	
	@Test
	public void testPositionIsInShape()
	{
		
	}
	
	@Test
	public void testStretch()
	{
		
	}
	
	@Test
	public void testTransform()
	{
		
	}
	
	@Test
	public void testEquals()
	{
		
	}
}

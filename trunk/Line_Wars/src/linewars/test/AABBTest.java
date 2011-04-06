package linewars.test;

import static org.junit.Assert.*;

import java.util.ArrayList;


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
		Rectangle r = new Rectangle(new Transformation(new Position(0, 0), 90), 200, 150);
		assertEquals(-75, r.getAABB().getXMin(), 0.001);
		assertEquals(-100, r.getAABB().getYMin(), 0.001);
		assertEquals(75, r.getAABB().getXMax(), 0.001);
		assertEquals(100, r.getAABB().getYMax(), 0.001);
	}
	
	@Test
	public void testAABBForShapeAggregate()
	{
		Rectangle r = new Rectangle(new Transformation(new Position(100, 0), 0), 150, 200);
		Rectangle r2 = new Rectangle(new Transformation(new Position(200, 0), 0), 150, 200);
		
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		shapes.add(r);
		shapes.add(r2);
		
		ArrayList<Transformation> relativePositions = new ArrayList<Transformation>();
		relativePositions.add(new Transformation(new Position(-100, 0), 0));
		relativePositions.add(new Transformation(new Position(-200, 0), 0));
		
		ShapeAggregate sa = new ShapeAggregate(new Transformation(new Position(0,0), 0), shapes, relativePositions);
		
		
	}
	
}

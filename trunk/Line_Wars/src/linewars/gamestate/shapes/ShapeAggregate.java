package linewars.gamestate.shapes;

import java.util.ArrayList;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.parser.Parser;
import linewars.parser.Parser.NoSuchKeyException;
import linewars.parser.ParserKeys;

public class ShapeAggregate extends Shape {
	
	static {
		Shape.addClassForInitialization("shapeaggregate", ShapeAggregate.class);
	}
	
	private ArrayList<Shape> members;
	private double rotation;


	/**
	 * Constructs a ShapeAggregate on the given Parser.
	 * This constructor expects to see a list of shape names mapped to
	 * the ParserKeys.shapes key which each have a Parser mapped to them.
	 * 
	 * If the ParserKeys.rotation key is specified, that value will be the
	 * default rotation of the ShapeAggregate; if it is not specified, it
	 * to 0 (facing to the right).
	 * 
	 * @param p
	 * The Parser which defines this ShapeAggregate
	 */
	public ShapeAggregate(Parser p){
		members = new ArrayList<Shape>();
		String[] keys = p.getList(ParserKeys.shapes);
		for(String key : keys){
			members.add(Shape.buildFromParser(p.getParser(key)));
		}
		try{
			rotation = p.getNumericValue(ParserKeys.rotation);			
		}catch(NoSuchKeyException e){
			rotation = 0;//defaults to 0
		}
	}

	//TODO document after implementing
	@Override
	public Shape stretch(Transformation change) {
		//TODO make an array(list?) of shapes
		//TODO for each Shape
			//TODO shift shape
			//TODO stretch shape
			//TODO add shape to array
		return null;//TODO construct a new ShapeAggregate on that array(list?)
	}

	@Override
	public Shape transform(Transformation change) {
		//TODO for each Shape
			//TODO translate by -1 * this.position
			//TODO rotate by change
			//TODO translate by this.position and change
		//TODO change any rotation-related state variables?
		return this;
	}

	@Override
	public Transformation position() {
		//TODO compute average position of the composing Shapes (ideally weighted by their area)
		//TODO how to compute rotation?
		return members.get(0).position();
	}
	
	/**
	 * Returns the Shapes which compose this ShapeAggregate
	 */
	public Shape[] getMembers() {
		return members.toArray(new Shape[0]);
	}

	@Override
	public Circle boundingCircle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle boundingRectangle() {
		//TODO compute a bounding rectangle for each Shape
		//TODO take the min and max of their x and y values to compute a bounding rect for all of them
		// TODO Auto-generated method stub
		return members.get(0).boundingRectangle();
	}

	@Override
	public boolean positionIsInShape(Position toTest) {
		//The point is in the Shape if it is in any of the sub-shapes
		for(Shape currentShape : members){
			if(currentShape.positionIsInShape(toTest)){
				return true;
			}
		}
		return false;
	}
}

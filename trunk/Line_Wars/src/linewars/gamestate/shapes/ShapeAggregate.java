package linewars.gamestate.shapes;

import java.util.ArrayList;

import linewars.gamestate.Transformation;
import linewars.parser.Parser;
import linewars.parser.ParserKeys;

public class ShapeAggregate extends Shape {
	
	private ArrayList<Shape> members;
	//TODO rotation-related state variable needed?
	
	//TODO document
	public ShapeAggregate(Parser p){//TODO check to see if the Parser does in fact define a ShapeAggregate
		members = new ArrayList<Shape>();
		String[] keys = p.getList(ParserKeys.shapes);
		for(String key : keys){
			members.add(Shape.buildFromParser(p.getParser(key)));
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
		return null;
	}

	@Override
	public Transformation position() {
		//TODO compute average position of the composing Shapes (ideally weighted by their area)
		//TODO how to compute rotation?
		return null;
	}
	
	//TODO document
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
		// TODO Auto-generated method stub
		return null;
	}
}

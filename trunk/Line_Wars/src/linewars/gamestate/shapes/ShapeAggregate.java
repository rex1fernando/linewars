package linewars.gamestate.shapes;

import java.util.ArrayList;
import java.util.List;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

/**
 * Represents a collection of Shapes, each with a specific relative position and orientation.
 * 
 * NYI
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp class ShapeAggregate extends Shape {
	
	static {
		//Adds this Shape to the map of Shapes for lookup
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
	public ShapeAggregate(ConfigData p){
		members = new ArrayList<Shape>();
		List<ConfigData> list = p.getConfigList(ParserKeys.shapes);
		for(ConfigData cfg : list){
			members.add(Shape.buildFromParser(cfg));
		}
		try{
			rotation = p.getNumber(ParserKeys.rotation);			
		}catch(NoSuchKeyException e){
			rotation = 0;//defaults to 0
		}
	}

	public ShapeAggregate() {
		members = new ArrayList<Shape>();
		rotation = 0;
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
		ShapeAggregate ret = new ShapeAggregate();
		//for each Shape
		for(int i = 0; i < members.size(); i++){
			//translate by -1 * this.position
			Shape atOrigin = members.get(i).transform(new Transformation(position().getPosition().scale(-1), 0));
			//rotate by change
			Shape rotated = atOrigin.transform(new Transformation(new Position(0, 0), change.getRotation()));
			//translate by this.position and change
			Shape finalShape = rotated.transform(new Transformation(change.getPosition().add(position().getPosition()), 0));
			ret.members.add(finalShape);
		}
		ret.rotation = rotation + change.getRotation();
		return ret;
	}

	@Override
	public Transformation position() {
		Position sum = new Position(0, 0);
		
		//compute the average position of the sub-shapes
		for(Shape toSum : members){
			//TODO weight this by the area of the Shape?
			//TODO somehow discount overlap?
			sum = sum.add(toSum.position().getPosition());
		}
		
		return new Transformation(sum.scale(1.0 / members.size()), rotation);
	}
	
	/**
	 * Returns the Shapes which compose this ShapeAggregate
	 */
	public Shape[] getMembers() {
		return members.toArray(new Shape[0]);
	}

	@Override
	public Circle boundingCircle() {
		//could use some sort of 'gimme the point farthest in this direction' method
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

	@Override
	public ConfigData getData() {
		ConfigData ret = new ConfigData();
		ret.set(ParserKeys.rotation, rotation);
		for(Shape toAdd : members){
			ret.add(ParserKeys.shapes, toAdd.getData());
		}
		return ret;
	}
}

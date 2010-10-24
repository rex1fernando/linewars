package linewars.gamestate.shapes;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.parser.Parser;
import linewars.parser.Parser.NoSuchKeyException;
import linewars.parser.ParserKeys;

public class Circle extends Shape {
	
	static {
		Shape.addClassForInitialization("circle", Circle.class);
	}
	
	//TODO document
	private Transformation position;
	private double radius;
	
	//TODO document
	public Circle(Transformation pos, double radius){
		this.radius = radius;
		position = pos;
	}
	
	public Circle(Parser config){
		radius = config.getNumericValue(ParserKeys.radius);
		double rotation = 0;
		try{
			rotation = Math.PI * config.getNumericValue(ParserKeys.rotation);
		}catch(NoSuchKeyException e){
			//just means rotation wasn't set, so it is 0 by default
		}
		position = new Transformation(new Position(config.getNumericValue(ParserKeys.x), config.getNumericValue(ParserKeys.y)), rotation);
	}

	@Override
	public Shape stretch(Transformation change) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Shape transform(Transformation change) {
		return new Circle(new Transformation(position.getPosition().add(change.getPosition()), position.getRotation() + change.getRotation()), radius);
	}

	@Override
	public Transformation position() {
		return position;
	}

	@Override
	public Circle boundingCircle() {
		return this;
	}

	@Override
	public Rectangle boundingRectangle() {
		return new Rectangle(position, radius * 2, radius * 2);
	}

	/**
	 * Returns the radius of the circle.
	 */
	public double getRadius(){
		return radius;
	}
}
